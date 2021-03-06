/**
 * eobjects.org AnalyzerBeans
 * Copyright (C) 2010 eobjects.org
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.eobjects.analyzer.beans.writers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.eobjects.analyzer.beans.api.Analyzer;
import org.eobjects.analyzer.beans.api.AnalyzerBean;
import org.eobjects.analyzer.beans.api.Categorized;
import org.eobjects.analyzer.beans.api.ColumnProperty;
import org.eobjects.analyzer.beans.api.Concurrent;
import org.eobjects.analyzer.beans.api.Configured;
import org.eobjects.analyzer.beans.api.Description;
import org.eobjects.analyzer.beans.api.FileProperty;
import org.eobjects.analyzer.beans.api.MappedProperty;
import org.eobjects.analyzer.beans.api.SchemaProperty;
import org.eobjects.analyzer.beans.api.TableProperty;
import org.eobjects.analyzer.beans.api.FileProperty.FileAccessMode;
import org.eobjects.analyzer.beans.api.Initialize;
import org.eobjects.analyzer.beans.api.Validate;
import org.eobjects.analyzer.beans.convert.ConvertToBooleanTransformer;
import org.eobjects.analyzer.beans.convert.ConvertToNumberTransformer;
import org.eobjects.analyzer.connection.CsvDatastore;
import org.eobjects.analyzer.connection.FileDatastore;
import org.eobjects.analyzer.connection.UpdateableDatastore;
import org.eobjects.analyzer.connection.UpdateableDatastoreConnection;
import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.data.InputRow;
import org.eobjects.analyzer.util.SchemaNavigator;
import org.eobjects.metamodel.BatchUpdateScript;
import org.eobjects.metamodel.UpdateCallback;
import org.eobjects.metamodel.UpdateScript;
import org.eobjects.metamodel.UpdateableDataContext;
import org.eobjects.metamodel.create.TableCreationBuilder;
import org.eobjects.metamodel.csv.CsvDataContext;
import org.eobjects.metamodel.insert.RowInsertionBuilder;
import org.eobjects.metamodel.query.FilterItem;
import org.eobjects.metamodel.query.OperatorType;
import org.eobjects.metamodel.query.SelectItem;
import org.eobjects.metamodel.schema.Column;
import org.eobjects.metamodel.schema.ColumnType;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.eobjects.metamodel.update.RowUpdationBuilder;
import org.eobjects.metamodel.util.Action;
import org.eobjects.metamodel.util.FileHelper;
import org.eobjects.metamodel.util.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnalyzerBean("Update table")
@Description("Update records in a table in a registered datastore. This component allows you to map the values available in the flow with the columns of the target table, in order to update the values of these columns in the datastore.")
@Categorized(WriteDataCategory.class)
@Concurrent(true)
public class UpdateTableAnalyzer implements Analyzer<WriteDataResult>, Action<Iterable<Object[]>> {

    private static final String PROPERTY_NAME_VALUES = "Values";
    
    private static final File TEMP_DIR = FileHelper.getTempDir();

    private static final String ERROR_MESSAGE_COLUMN_NAME = "update_table_error_message";

    private static final Logger logger = LoggerFactory.getLogger(UpdateTableAnalyzer.class);

    @Inject
    @Configured(PROPERTY_NAME_VALUES)
    @Description("Values to update in the table")
    InputColumn<?>[] values;

    @Inject
    @Configured
    @Description("Names of columns in the target table, on which the values will be updated.")
    @ColumnProperty
    @MappedProperty(PROPERTY_NAME_VALUES)
    String[] columnNames;

    @Inject
    @Configured
    @Description("Values that make up the condition of the table update")
    InputColumn<?>[] conditionValues;

    @Inject
    @Configured
    @Description("Names of columns in the target table, which form the conditions of the update.")
    @ColumnProperty
    String[] conditionColumnNames;

    @Inject
    @Configured
    @Description("Datastore to write to")
    UpdateableDatastore datastore;

    @Inject
    @Configured(required = false)
    @Description("Schema name of target table")
    @SchemaProperty
    String schemaName;

    @Inject
    @Configured(required = false)
    @Description("Table to target (update)")
    @TableProperty
    String tableName;

    @Inject
    @Configured("Buffer size")
    @Description("How much data to buffer before committing batches of data. Large batches often perform better, but require more memory.")
    WriteBufferSizeOption bufferSizeOption = WriteBufferSizeOption.MEDIUM;

    @Inject
    @Configured(value = "How to handle updation errors?")
    ErrorHandlingOption errorHandlingOption = ErrorHandlingOption.STOP_JOB;

    @Inject
    @Configured(value = "Error log file location", required = false)
    @Description("Directory or file path for saving erroneous records")
    @FileProperty(accessMode = FileAccessMode.SAVE, extension = ".csv")
    File errorLogFile = TEMP_DIR;

    @Inject
    @Configured(required = false)
    @Description("Additional values to write to error log")
    InputColumn<?>[] additionalErrorLogValues;

    private Column[] _targetColumns;
    private Column[] _targetConditionColumns;
    private WriteBuffer _writeBuffer;
    private AtomicInteger _updatedRowCount;
    private AtomicInteger _errorRowCount;
    private CsvDataContext _errorDataContext;

    @Validate
    public void validate() {
        if (values.length != columnNames.length) {
            throw new IllegalStateException("Values and column names should have equal length");
        }

        if (conditionValues.length != conditionColumnNames.length) {
            throw new IllegalStateException("Condition values and condition column names should have equal length");
        }
    }

    @Initialize
    public void init() throws IllegalArgumentException {
        if (logger.isDebugEnabled()) {
            logger.debug("At init() time, InputColumns are: {}", Arrays.toString(values));
        }

        _errorRowCount = new AtomicInteger();
        _updatedRowCount = new AtomicInteger();
        if (errorHandlingOption == ErrorHandlingOption.SAVE_TO_FILE) {
            _errorDataContext = createErrorDataContext();
        }

        int bufferSize = bufferSizeOption.calculateBufferSize(values.length);
        logger.info("Row buffer size set to {}", bufferSize);

        _writeBuffer = new WriteBuffer(bufferSize, this);

        final UpdateableDatastoreConnection con = datastore.openConnection();
        try {
            final SchemaNavigator schemaNavigator = con.getSchemaNavigator();

            final List<String> columnsNotFound = new ArrayList<String>();

            _targetColumns = schemaNavigator.convertToColumns(schemaName, tableName, columnNames);
            for (int i = 0; i < _targetColumns.length; i++) {
                if (_targetColumns[i] == null) {
                    columnsNotFound.add(columnNames[i]);
                }
            }

            _targetConditionColumns = schemaNavigator.convertToColumns(schemaName, tableName, conditionColumnNames);
            for (int i = 0; i < _targetConditionColumns.length; i++) {
                if (_targetConditionColumns[i] == null) {
                    columnsNotFound.add(conditionColumnNames[i]);
                }
            }

            if (!columnsNotFound.isEmpty()) {
                throw new IllegalArgumentException("Could not find column(s): " + columnsNotFound);
            }
        } finally {
            con.close();
        }
    }

    private void validateCsvHeaders(CsvDataContext dc) {
        Schema schema = dc.getDefaultSchema();
        if (schema.getTableCount() == 0) {
            // nothing to worry about, we will create the table ourselves
            return;
        }
        Table table = schema.getTables()[0];

        // verify that table names correspond to what we need!

        for (String columnName : columnNames) {
            Column column = table.getColumnByName(columnName);
            if (column == null) {
                throw new IllegalStateException("Error log file does not have required column header: " + columnName);
            }
        }
        for (String columnName : conditionColumnNames) {
            Column column = table.getColumnByName(columnName);
            if (column == null) {
                throw new IllegalStateException("Error log file does not have required column header: " + columnName);
            }
        }
        if (additionalErrorLogValues != null) {
            for (InputColumn<?> inputColumn : additionalErrorLogValues) {
                String columnName = translateAdditionalErrorLogColumnName(inputColumn.getName());
                Column column = table.getColumnByName(columnName);
                if (column == null) {
                    throw new IllegalStateException("Error log file does not have required column header: "
                            + columnName);
                }
            }
        }

        Column column = table.getColumnByName(ERROR_MESSAGE_COLUMN_NAME);
        if (column == null) {
            throw new IllegalStateException("Error log file does not have required column: "
                    + ERROR_MESSAGE_COLUMN_NAME);
        }
    }

    private String translateAdditionalErrorLogColumnName(String columnName) {
        if (ArrayUtils.contains(columnNames, columnName)) {
            return translateAdditionalErrorLogColumnName(columnName + "_add");
        }
        return columnName;
    }

    private CsvDataContext createErrorDataContext() {
        final File file;

        if (errorLogFile == null || TEMP_DIR.equals(errorLogFile)) {
            try {
                file = File.createTempFile("updation_error", ".csv");
            } catch (IOException e) {
                throw new IllegalStateException("Could not create new temp file", e);
            }
        } else if (errorLogFile.isDirectory()) {
            file = new File(errorLogFile, "updation_error_log.csv");
        } else {
            file = errorLogFile;
        }

        final CsvDataContext dc = new CsvDataContext(file);

        final Schema schema = dc.getDefaultSchema();

        if (file.exists() && file.length() > 0) {
            validateCsvHeaders(dc);
        } else {
            // create table if no table exists.
            dc.executeUpdate(new UpdateScript() {
                @Override
                public void run(UpdateCallback cb) {
                    TableCreationBuilder tableBuilder = cb.createTable(schema, "error_table");
                    for (String columnName : columnNames) {
                        tableBuilder = tableBuilder.withColumn(columnName);
                    }
                    for (String columnName : conditionColumnNames) {
                        tableBuilder = tableBuilder.withColumn(columnName);
                    }

                    if (additionalErrorLogValues != null) {
                        for (InputColumn<?> inputColumn : additionalErrorLogValues) {
                            String columnName = translateAdditionalErrorLogColumnName(inputColumn.getName());
                            tableBuilder = tableBuilder.withColumn(columnName);
                        }
                    }

                    tableBuilder = tableBuilder.withColumn(ERROR_MESSAGE_COLUMN_NAME);

                    tableBuilder.execute();
                }
            });
        }

        return dc;
    }

    @Override
    public void run(InputRow row, int distinctCount) {
        if (logger.isDebugEnabled()) {
            logger.debug("At run() time, InputColumns are: {}", Arrays.toString(values));
        }

        final Object[] rowData;
        if (additionalErrorLogValues == null) {
            rowData = new Object[values.length + conditionColumnNames.length];
        } else {
            rowData = new Object[values.length + conditionColumnNames.length + additionalErrorLogValues.length];
        }
        for (int i = 0; i < values.length; i++) {
            rowData[i] = row.getValue(values[i]);
        }
        for (int i = 0; i < conditionValues.length; i++) {
            rowData[i + values.length] = row.getValue(conditionValues[i]);
        }

        if (additionalErrorLogValues != null) {
            for (int i = 0; i < additionalErrorLogValues.length; i++) {
                Object value = row.getValue(additionalErrorLogValues[i]);
                rowData[values.length + +conditionColumnNames.length + i] = value;
            }
        }

        try {
            // perform conversion in a separate loop, since it might crash and
            // the
            // error data will be more complete if first loop finished.
            for (int i = 0; i < values.length; i++) {
                rowData[i] = convertType(rowData[i], _targetColumns[i]);

                if (logger.isDebugEnabled()) {
                    logger.debug("Value for {} set to: {}", columnNames[i], rowData[i]);
                }
            }
            for (int i = 0; i < conditionValues.length; i++) {
                int index = i + values.length;
                rowData[index] = convertType(rowData[index], _targetConditionColumns[i]);

                if (logger.isDebugEnabled()) {
                    logger.debug("Value for {} set to: {}", conditionColumnNames[i], rowData[index]);
                }
            }
        } catch (RuntimeException e) {
            for (int i = 0; i < distinctCount; i++) {
                errorOccurred(rowData, e);
            }
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Adding row data to buffer: {}", Arrays.toString(rowData));
        }

        for (int i = 0; i < distinctCount; i++) {
            _writeBuffer.addToBuffer(rowData);
        }
    }

    private Object convertType(final Object value, Column targetColumn) throws IllegalArgumentException {
        if (value == null) {
            return null;
        }
        Object result = value;
        ColumnType type = targetColumn.getType();
        if (type.isLiteral()) {
            // for strings, only convert some simple cases, since JDBC drivers
            // typically also do a decent job here (with eg. Clob types, char[]
            // types etc.)
            if (value instanceof Number || value instanceof Date) {
                result = value.toString();
            }
        } else if (type.isNumber()) {
            Number numberValue = ConvertToNumberTransformer.transformValue(value);
            if (numberValue == null && !"".equals(value)) {
                throw new IllegalArgumentException("Could not convert " + value + " to number");
            }
            result = numberValue;
        } else if (type == ColumnType.BOOLEAN) {
            Boolean booleanValue = ConvertToBooleanTransformer.transformValue(value);
            if (booleanValue == null && !"".equals(value)) {
                throw new IllegalArgumentException("Could not convert " + value + " to boolean");
            }
            result = booleanValue;
        }
        return result;
    }

    @Override
    public WriteDataResult getResult() {
        _writeBuffer.flushBuffer();

        final int updatedRowCount = _updatedRowCount.get();

        final FileDatastore errorDatastore;
        if (_errorDataContext != null) {
            Resource resource = _errorDataContext.getResource();
            errorDatastore = new CsvDatastore(resource.getName(), resource);
        } else {
            errorDatastore = null;
        }

        return new WriteDataResultImpl(0, updatedRowCount, datastore, schemaName, tableName, _errorRowCount.get(),
                errorDatastore);
    }

    /**
     * Method invoked when flushing the buffer
     */
    @Override
    public void run(final Iterable<Object[]> buffer) throws Exception {

        UpdateableDatastoreConnection con = datastore.openConnection();
        try {
            final Column[] updateColumns = con.getSchemaNavigator()
                    .convertToColumns(schemaName, tableName, columnNames);
            final Column[] whereColumns = con.getSchemaNavigator().convertToColumns(schemaName, tableName,
                    conditionColumnNames);

            if (logger.isDebugEnabled()) {
                logger.debug("Updating columns: {}", Arrays.toString(updateColumns));
            }

            final UpdateableDataContext dc = con.getUpdateableDataContext();
            dc.executeUpdate(new BatchUpdateScript() {
                @Override
                public void run(UpdateCallback callback) {
                    for (Object[] rowData : buffer) {
                        RowUpdationBuilder updationBuilder = callback.update(updateColumns[0].getTable());
                        for (int i = 0; i < updateColumns.length; i++) {
                            final Object value = rowData[i];
                            updationBuilder = updationBuilder.value(updateColumns[i], value);
                        }

                        for (int i = 0; i < whereColumns.length; i++) {
                            final Object value = rowData[i + updateColumns.length];
                            final Column whereColumn = whereColumns[i];
                            final FilterItem filterItem = new FilterItem(new SelectItem(whereColumn),
                                    OperatorType.EQUALS_TO, value);

                            updationBuilder = updationBuilder.where(filterItem);
                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug("Updating: {}", Arrays.toString(rowData));
                        }

                        try {
                            updationBuilder.execute();
                            _updatedRowCount.incrementAndGet();
                        } catch (final RuntimeException e) {
                            errorOccurred(rowData, e);
                        }
                    }
                }
            });
        } finally {
            con.close();
        }
    }

    protected void errorOccurred(final Object[] rowData, final RuntimeException e) {
        _errorRowCount.incrementAndGet();
        if (errorHandlingOption == ErrorHandlingOption.STOP_JOB) {
            throw e;
        } else {
            logger.warn("Error occurred while updating record. Writing to error stream", e);
            _errorDataContext.executeUpdate(new UpdateScript() {
                @Override
                public void run(UpdateCallback cb) {
                    RowInsertionBuilder insertBuilder = cb
                            .insertInto(_errorDataContext.getDefaultSchema().getTables()[0]);
                    for (int i = 0; i < columnNames.length; i++) {
                        insertBuilder = insertBuilder.value(columnNames[i], rowData[i]);
                    }

                    if (additionalErrorLogValues != null) {
                        for (int i = 0; i < additionalErrorLogValues.length; i++) {
                            String columnName = translateAdditionalErrorLogColumnName(additionalErrorLogValues[i]
                                    .getName());
                            Object value = rowData[columnNames.length + i];
                            insertBuilder = insertBuilder.value(columnName, value);
                        }
                    }

                    insertBuilder = insertBuilder.value(ERROR_MESSAGE_COLUMN_NAME, e.getMessage());
                    insertBuilder.execute();
                }
            });
        }
    }
}
