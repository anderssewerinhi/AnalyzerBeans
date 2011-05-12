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
package org.eobjects.analyzer.connection;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import org.eobjects.analyzer.util.ReadObjectBuilder;
import org.eobjects.metamodel.DataContext;
import org.eobjects.metamodel.DataContextFactory;

/**
 * Datastore implementation for Excel spreadsheets.
 * 
 * @author Kasper Sørensen
 */
public final class ExcelDatastore extends UsageAwareDatastore implements FileDatastore {

	private static final long serialVersionUID = 1L;

	private final String _filename;

	public ExcelDatastore(String name, String filename) {
		super(name);
		_filename = filename;
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		ReadObjectBuilder.create(this, ExcelDatastore.class).readObject(stream);
	}

	@Override
	public String getFilename() {
		return _filename;
	}

	@Override
	protected UsageAwareDataContextProvider createDataContextProvider() {
		DataContext dc = DataContextFactory.createExcelDataContext(new File(_filename));
		return new SingleDataContextProvider(dc, this);
	}

	@Override
	public PerformanceCharacteristics getPerformanceCharacteristics() {
		return new PerformanceCharacteristicsImpl(false);
	}

	@Override
	protected void decorateIdentity(List<Object> identifiers) {
		super.decorateIdentity(identifiers);
		identifiers.add(_filename);
	}
}
