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
package org.eobjects.analyzer.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eobjects.metamodel.DataContext;
import org.eobjects.metamodel.DataContextFactory;
import org.eobjects.metamodel.MetaModelException;
import org.eobjects.metamodel.QueryPostprocessDataContext;
import org.eobjects.metamodel.data.DataSet;
import org.eobjects.metamodel.schema.Column;
import org.eobjects.metamodel.schema.MutableColumn;
import org.eobjects.metamodel.schema.MutableSchema;
import org.eobjects.metamodel.schema.MutableTable;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;
import org.eobjects.metamodel.util.CollectionUtils;
import org.eobjects.metamodel.util.HasNameMapper;

public class SchemaNavigatorTest extends TestCase {

    public void testTheBasics() throws Exception {
        DataContext dc = DataContextFactory.createCsvDataContext(new File("src/test/resources/employees.csv"), ',',
                '\"');
        SchemaNavigator sn = new SchemaNavigator(dc);
        sn.refreshSchemas();

        // columns
        assertEquals("Column[name=email,columnNumber=1,type=VARCHAR,nullable=true,nativeType=null,columnSize=null]", sn
                .convertToColumn("email").toString());
        assertEquals("Column[name=email,columnNumber=1,type=VARCHAR,nullable=true,nativeType=null,columnSize=null]", sn
                .convertToColumn("employees.email").toString());
        assertEquals("Column[name=email,columnNumber=1,type=VARCHAR,nullable=true,nativeType=null,columnSize=null]", sn
                .convertToColumn("employees.csv.employees.email").toString());
        assertEquals("Column[name=email,columnNumber=1,type=VARCHAR,nullable=true,nativeType=null,columnSize=null]", sn
                .convertToColumn("employees.csv.email").toString());

        assertNull(sn.convertToColumns("foo", "bar", null));
        assertEquals(0, (sn.convertToColumns("foo", "bar", new String[0])).length);

        assertEquals(
                "[Column[name=email,columnNumber=1,type=VARCHAR,nullable=true,nativeType=null,columnSize=null], Column[name=name,columnNumber=0,type=VARCHAR,nullable=true,nativeType=null,columnSize=null]]",
                Arrays.toString(sn.convertToColumns(new String[] { "email", "employees.name" })));
        assertEquals(
                "[Column[name=email,columnNumber=1,type=VARCHAR,nullable=true,nativeType=null,columnSize=null], null, Column[name=name,columnNumber=0,type=VARCHAR,nullable=true,nativeType=null,columnSize=null]]",
                Arrays.toString(sn.convertToColumns("employees.csv", "employees", new String[] { "email",
                        "not-existing", "name" })));
        assertEquals(
                "[Column[name=email,columnNumber=1,type=VARCHAR,nullable=true,nativeType=null,columnSize=null], null, Column[name=name,columnNumber=0,type=VARCHAR,nullable=true,nativeType=null,columnSize=null]]",
                Arrays.toString(sn
                        .convertToColumns(null, "employees", new String[] { "email", "not-existing", "name" })));

        try {
            sn.convertToColumns("not-existing", "employees", new String[] { "email", "not-existing", "name" });
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(
                    "Schema not-existing not found. Available schema names are: [information_schema, employees.csv]",
                    e.getMessage());
        }

        try {
            sn.convertToColumns("employees.csv", "not-existing", new String[] { "email", "not-existing", "name" });
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals("Table not found. Available table names are: [employees]", e.getMessage());
        }

        // tables
        assertEquals("Table[name=employees,type=TABLE,remarks=null]", sn.convertToTable("employees").toString());
        assertEquals("Table[name=employees,type=TABLE,remarks=null]", sn.convertToTable(null, "employees").toString());
        assertEquals("Table[name=employees,type=TABLE,remarks=null]", sn.convertToTable(null, null).toString());

        try {
            sn.convertToTable("information_schema", null);
            fail("Excpetion expected");
        } catch (Exception e) {
            assertEquals(
                    "No table name specified, and multiple options exist. Available table names are: [tables, columns, relationships]",
                    e.getMessage());
        }

        assertEquals("Table[name=employees,type=TABLE,remarks=null]", sn.convertToTable("employees.csv", "employees")
                .toString());
        assertEquals(
                "[Table[name=employees,type=TABLE,remarks=null], Table[name=employees,type=TABLE,remarks=null], null]",
                Arrays.toString(sn.convertToTables(new String[] { "employees", "employees.csv.employees", "foo" })));

        // schemas
        assertEquals("Schema[name=employees.csv]", sn.convertToSchema("employees.csv").toString());
        assertEquals(null, sn.convertToSchema("foo"));

        assertEquals("[null, Schema[name=employees.csv], Schema[name=information_schema]]",
                Arrays.toString(sn.convertToSchemas(new String[] { "foo", "employees.csv", "information_schema" })));

        assertEquals("Schema[name=employees.csv]", sn.getDefaultSchema().toString());
        assertEquals(null, sn.getSchemaByName("foo"));
        assertEquals("[Schema[name=information_schema], Schema[name=employees.csv]]", Arrays.toString(sn.getSchemas()));
    }

    public void testSchemaWithDot() throws Exception {
        DataContext dc = DataContextFactory.createCsvDataContext(new File("src/test/resources/employees.csv"), ',',
                '\"');

        assertEquals(2, dc.getDefaultSchema().getTables()[0].getColumnCount());

        SchemaNavigator sn = new SchemaNavigator(dc);

        Column column = sn.convertToColumn("employees.csv.employees.email");
        assertEquals("Column[name=email,columnNumber=1,type=VARCHAR,nullable=true,nativeType=null,columnSize=null]",
                column.toString());
    }

    public void testColumnNamesWithDots() throws Exception {
        final MutableSchema schema = new MutableSchema("SCHE");
        schema.addTable(new MutableTable("tabl1").setSchema(schema));
        MutableTable orgTable = new MutableTable("tabl2").setSchema(schema);

        orgTable.addColumn(new MutableColumn("source_id").setTable(orgTable));
        orgTable.addColumn(new MutableColumn("BLOB.BLOBNumberMain").setTable(orgTable));
        orgTable.addColumn(new MutableColumn("BLOB.BLOBNumberBranch").setTable(orgTable));

        schema.addTable(orgTable);

        DataContext dataContext = new QueryPostprocessDataContext() {
            @Override
            protected DataSet materializeMainSchemaTable(Table table, Column[] columns, int maxRows) {
                throw new UnsupportedOperationException();
            }

            @Override
            protected String getMainSchemaName() throws MetaModelException {
                return schema.getName();
            }

            @Override
            protected Schema getMainSchema() throws MetaModelException {
                return schema;
            }
        };

        final SchemaNavigator schemaNavigator = new SchemaNavigator(dataContext);
        String[] columnNames = new String[] { "SCHE.tabl2.source_id", "SCHE.tabl2.BLOB.BLOBNumberMain",
                "SCHE.tabl2.BLOB.BLOBNumberBranch" };

        Column[] columnsResult = schemaNavigator.convertToColumns(columnNames);

        for (Column column : columnsResult) {
            assertNotNull(column);
        }

        List<String> columnNamesResult = CollectionUtils.map(columnsResult, new HasNameMapper());
        assertEquals("[source_id, BLOB.BLOBNumberMain, BLOB.BLOBNumberBranch]", columnNamesResult.toString());
    }
}
