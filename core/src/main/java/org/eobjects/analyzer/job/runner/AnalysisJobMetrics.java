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
package org.eobjects.analyzer.job.runner;

import org.eobjects.analyzer.job.AnalysisJob;
import org.eobjects.analyzer.job.AnalyzerJob;
import org.eobjects.metamodel.schema.Table;

/**
 * Provides useful metrics and information about an analysis job.
 */
public interface AnalysisJobMetrics {

	/**
	 * Gets the analysis job being executed.
	 * 
	 * @return the analysis job being executed.
	 */
	public AnalysisJob getAnalysisJob();

	public AnalyzerMetrics getAnalyzerMetrics(AnalyzerJob analyzerJob);

	public Table getRowProcessingTable(AnalyzerJob analyzerJob);

	public Table[] getRowProcessingTables();

	public RowProcessingMetrics getRowProcessingMetrics(Table table);
}
