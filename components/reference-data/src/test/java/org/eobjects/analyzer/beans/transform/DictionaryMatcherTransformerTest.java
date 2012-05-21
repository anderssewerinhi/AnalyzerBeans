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
package org.eobjects.analyzer.beans.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.eobjects.analyzer.beans.convert.ConvertToNumberTransformer;
import org.eobjects.analyzer.beans.valuedist.ValueDistributionAnalyzer;
import org.eobjects.analyzer.beans.valuedist.ValueDistributionResult;
import org.eobjects.analyzer.configuration.AnalyzerBeansConfigurationImpl;
import org.eobjects.analyzer.connection.CsvDatastore;
import org.eobjects.analyzer.connection.Datastore;
import org.eobjects.analyzer.data.MutableInputColumn;
import org.eobjects.analyzer.job.AnalysisJob;
import org.eobjects.analyzer.job.builder.AnalysisJobBuilder;
import org.eobjects.analyzer.job.builder.AnalyzerJobBuilder;
import org.eobjects.analyzer.job.builder.TransformerJobBuilder;
import org.eobjects.analyzer.job.runner.AnalysisResultFuture;
import org.eobjects.analyzer.job.runner.AnalysisRunnerImpl;
import org.eobjects.analyzer.reference.Dictionary;
import org.eobjects.analyzer.reference.ReferenceDataCatalogImpl;
import org.eobjects.analyzer.reference.SimpleDictionary;
import org.eobjects.analyzer.reference.SimpleSynonym;
import org.eobjects.analyzer.reference.SimpleSynonymCatalog;
import org.eobjects.analyzer.reference.StringPattern;
import org.eobjects.analyzer.reference.SynonymCatalog;
import org.eobjects.analyzer.result.AnalyzerResult;

public class DictionaryMatcherTransformerTest extends TestCase {

	public void testParseAndAssignDictionaries() throws Throwable {
		Collection<Dictionary> dictionaries = new ArrayList<Dictionary>();
		dictionaries.add(new SimpleDictionary("eobjects.org products", "MetaModel", "DataCleaner", "AnalyzerBeans"));
		dictionaries.add(new SimpleDictionary("apache products", "commons-lang", "commons-math", "commons-codec",
				"commons-logging"));
		dictionaries.add(new SimpleDictionary("logging products", "commons-logging", "log4j", "slf4j", "java.util.Logging"));

		Collection<SynonymCatalog> synonymCatalogs = new ArrayList<SynonymCatalog>();
		synonymCatalogs.add(new SimpleSynonymCatalog("translated terms", new SimpleSynonym("hello", "howdy", "hi", "yo",
				"hey"), new SimpleSynonym("goodbye", "bye", "see you", "hey")));

		Collection<StringPattern> stringPatterns = new ArrayList<StringPattern>();

		ReferenceDataCatalogImpl ref = new ReferenceDataCatalogImpl(dictionaries, synonymCatalogs, stringPatterns);

		Datastore datastore = new CsvDatastore("my database", "src/test/resources/projects.csv");
		AnalyzerBeansConfigurationImpl conf = new AnalyzerBeansConfigurationImpl();
		AnalysisJobBuilder job = new AnalysisJobBuilder(conf);
		job.setDatastore(datastore);
		job.addSourceColumns("product", "version");
		TransformerJobBuilder<DictionaryMatcherTransformer> tjb1 = job.addTransformer(DictionaryMatcherTransformer.class);
		tjb1.setConfiguredProperty(
				"Dictionaries",
				new Dictionary[] { ref.getDictionary("eobjects.org products"), ref.getDictionary("apache products"),
						ref.getDictionary("logging products") });
		tjb1.addInputColumn(job.getSourceColumnByName("product"));
		List<MutableInputColumn<?>> outputColumns = tjb1.getOutputColumns();
		assertEquals(3, outputColumns.size());
		outputColumns.get(0).setName("eobjects match");
		outputColumns.get(1).setName("apache match");
		outputColumns.get(2).setName("logging match");

		TransformerJobBuilder<ConvertToNumberTransformer> tjb2 = job.addTransformer(ConvertToNumberTransformer.class);
		tjb2.addInputColumn(outputColumns.get(2));
		tjb2.getOutputColumns().get(0).setName("logging match -> number");

		AnalyzerJobBuilder<ValueDistributionAnalyzer> ajb = job
				.addAnalyzer(ValueDistributionAnalyzer.class);
		ajb.addInputColumns(tjb1.getOutputColumns());
		ajb.addInputColumns(tjb2.getOutputColumns());

		assertTrue(job.isConfigured());

		AnalysisJob analysisJob = job.toAnalysisJob();
		AnalysisResultFuture resultFuture = new AnalysisRunnerImpl(conf).run(analysisJob);
		
		if (!resultFuture.isSuccessful()) {
			throw resultFuture.getErrors().get(0);
		}
		
		List<AnalyzerResult> results = resultFuture.getResults();

		assertEquals(4, results.size());
		ValueDistributionResult res = (ValueDistributionResult) results.get(0);
		assertEquals("eobjects match", res.getColumnName());
		assertEquals(8, res.getSingleValueDistributionResult().getCount("true").intValue());
		assertEquals(4, res.getSingleValueDistributionResult().getCount("false").intValue());

		res = (ValueDistributionResult) results.get(1);
		assertEquals("apache match", res.getColumnName());
		assertEquals(2, res.getSingleValueDistributionResult().getCount("true").intValue());
		assertEquals(10, res.getSingleValueDistributionResult().getCount("false").intValue());

		res = (ValueDistributionResult) results.get(2);
		assertEquals("logging match", res.getColumnName());
		assertEquals(3, res.getSingleValueDistributionResult().getCount("true").intValue());
		assertEquals(9, res.getSingleValueDistributionResult().getCount("false").intValue());

		res = (ValueDistributionResult) results.get(3);
		assertEquals("logging match -> number", res.getColumnName());
		assertEquals(3, res.getSingleValueDistributionResult().getCount("1").intValue());
		assertEquals(9, res.getSingleValueDistributionResult().getCount("0").intValue());
	}

	public void testTransform() throws Exception {
		Dictionary[] dictionaries = new Dictionary[] {
				new SimpleDictionary("danish male names", "kasper", "kim", "asbjørn"),
				new SimpleDictionary("danish female names", "trine", "kim", "lene") };
		DictionaryMatcherTransformer transformer = new DictionaryMatcherTransformer(null, dictionaries);
		assertEquals("[true, false]", Arrays.toString(transformer.transform("kasper")));
		assertEquals("[false, false]", Arrays.toString(transformer.transform("foobar")));
		assertEquals("[false, true]", Arrays.toString(transformer.transform("trine")));
		assertEquals("[true, true]", Arrays.toString(transformer.transform("kim")));
		
		transformer._outputType = MatchOutputType.INPUT_OR_NULL;
		assertEquals("[kim, kim]", Arrays.toString(transformer.transform("kim")));
		assertEquals("[null, trine]", Arrays.toString(transformer.transform("trine")));
	}
}