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
package org.eobjects.analyzer.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A very simple AnalyzerResult that simply holds a list of values
 * 
 * @author Kasper Sørensen
 * 
 * @param <E>
 */
public class ListResult<E> implements AnalyzerResult {

	private static final long serialVersionUID = 1L;

	private final List<E> _values;
	
	public ListResult(List<E> values) {
	    _values = values;
	}
	
	public ListResult(Collection<E> values) {
	    if (values instanceof List) {
	        _values = (List<E>) values;
	    } else {
	        _values = new ArrayList<E>(values);
	    }
	}

	public List<E> getValues() {
		return _values;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (E value : _values) {
			if (sb.length() > 0) {
				sb.append('\n');
			}
			sb.append(value);
		}
		return sb.toString();
	}
}
