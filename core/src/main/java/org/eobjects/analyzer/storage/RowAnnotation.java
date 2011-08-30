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
package org.eobjects.analyzer.storage;

/**
 * Represents an annotation (aka a mark, a label or a categorization) of a row.
 * RowAnnotations are used typically by analyzers in order to label rows for
 * later use, typically drill-to-detail functionality.
 * 
 * RowAnnotations are created through the RowAnnotationFactory, which is
 * injectable using the @Provided annotation.
 * 
 * @see RowAnnotationFactory
 * 
 * @author Kasper Sørensen
 */
public interface RowAnnotation {

	public int getRowCount();
}