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

import org.apache.commons.lang.SerializationUtils;

import junit.framework.TestCase;

public class JdbcDatastoreTest extends TestCase {

	public void testEquals() throws Exception {
		JdbcDatastore ds1 = new JdbcDatastore("hello", "url", "driver", "username", "pw");
		JdbcDatastore ds2;

		ds2 = new JdbcDatastore("hello", "url", "driver", "username", "pw");
		assertEquals(ds1, ds2);

		ds2 = new JdbcDatastore("hello1", "url", "driver", "username", "pw");
		assertFalse(ds1.equals(ds2));

		ds2 = new JdbcDatastore("hello", "url2", "driver", "username", "pw");
		assertFalse(ds1.equals(ds2));
	}
	
	public void testSerializationAndDeserialization() throws Exception {
		JdbcDatastore ds = new JdbcDatastore("name", "url", "driver", "username", "pw");
		
		Object clone = SerializationUtils.clone(ds);
		assertEquals(ds, clone);
	}

	public void testGetters() throws Exception {
		JdbcDatastore ds = new JdbcDatastore("name", "url", "driver", "username", "pw");

		assertEquals(null, ds.getDatasourceJndiUrl());
		assertEquals("name", ds.getName());
		assertEquals("url", ds.getJdbcUrl());
		assertEquals("driver", ds.getDriverClass());
		assertEquals("username", ds.getUsername());
		assertEquals("pw", ds.getPassword());
		
		assertEquals("JdbcDatastore[name=name,url=url]", ds.toString());
	}
	
	public void testToStringDataSource() throws Exception {
		JdbcDatastore ds = new JdbcDatastore("foo","bar");
		
		assertEquals("JdbcDatastore[name=foo,jndi=bar]", ds.toString());
	}
}
