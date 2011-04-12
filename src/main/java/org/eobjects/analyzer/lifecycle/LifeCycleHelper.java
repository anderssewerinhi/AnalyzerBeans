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
package org.eobjects.analyzer.lifecycle;

import java.lang.reflect.Array;

import org.eobjects.analyzer.configuration.AnalyzerBeansConfiguration;
import org.eobjects.analyzer.connection.DatastoreCatalog;
import org.eobjects.analyzer.descriptors.ComponentDescriptor;
import org.eobjects.analyzer.descriptors.SimpleComponentDescriptor;
import org.eobjects.analyzer.reference.ReferenceDataCatalog;

/**
 * Utility/convenience class for doing simple lifecycle management and/or
 * mimicing the lifecycle of components lifecycle in a job execution.
 * 
 * @author Kasper Sørensen
 */
public final class LifeCycleHelper {

	private final DatastoreCatalog _datastoreCatalog;
	private final ReferenceDataCatalog _referenceDataCatalog;
	private final InitializeCallback _initializeCallback;
	private final CloseCallback _closeCallback;

	public LifeCycleHelper(DatastoreCatalog datastoreCatalog, ReferenceDataCatalog referenceDataCatalog) {
		_datastoreCatalog = datastoreCatalog;
		_referenceDataCatalog = referenceDataCatalog;
		_initializeCallback = new InitializeCallback(_datastoreCatalog, _referenceDataCatalog);
		_closeCallback = new CloseCallback();
	}

	public LifeCycleHelper(AnalyzerBeansConfiguration conf) {
		this(conf.getDatastoreCatalog(), conf.getReferenceDataCatalog());
	}

	public void initialize(Object o) {
		if (o.getClass().isArray()) {
			int length = Array.getLength(o);
			for (int i = 0; i < length; i++) {
				Object object = Array.get(o, i);
				initialize(object);
			}
		} else {
			SimpleComponentDescriptor<? extends Object> descriptor = SimpleComponentDescriptor.create(o.getClass());
			initialize(descriptor, o);
		}
	}

	public void initialize(ComponentDescriptor<?> descriptor, Object bean) {
		_initializeCallback.onEvent(LifeCycleState.INITIALIZE, bean, descriptor);
	}

	public void close(Object o) {
		if (o.getClass().isArray()) {
			int length = Array.getLength(o);
			for (int i = 0; i < length; i++) {
				Object object = Array.get(o, i);
				close(object);
			}
		} else {
			SimpleComponentDescriptor<? extends Object> descriptor = SimpleComponentDescriptor.create(o.getClass());
			close(descriptor, o);
		}
	}

	public void close(ComponentDescriptor<?> descriptor, Object bean) {
		_closeCallback.onEvent(LifeCycleState.CLOSE, bean, descriptor);
	}
}