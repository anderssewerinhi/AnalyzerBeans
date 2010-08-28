package org.eobjects.analyzer.job;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eobjects.analyzer.configuration.AnalyzerBeansConfiguration;
import org.eobjects.analyzer.data.MetaModelInputColumn;
import org.eobjects.analyzer.job.runner.AnalysisRunnerImpl;
import org.eobjects.analyzer.result.AnalyzerResult;
import org.eobjects.analyzer.result.CrosstabResult;
import org.eobjects.analyzer.test.TestHelper;

import junit.framework.TestCase;

public class JaxbJobFactoryTest extends TestCase {

	public void testInvalidRead() throws Exception {
		JaxbJobFactory factory = new JaxbJobFactory(
				TestHelper.createAnalyzerBeansConfiguration());
		try {
			factory.create(new File(
					"src/test/resources/example-job-invalid.xml"));
			fail("Exception expected");
		} catch (IllegalArgumentException e) {
			assertEquals(
					"javax.xml.bind.UnmarshalException: unexpected element "
							+ "(uri:\"http://eobjects.org/analyzerbeans/job/1.0\", local:\"datacontext\"). "
							+ "Expected elements are <{http://eobjects.org/analyzerbeans/job/1.0}columns>,"
							+ "<{http://eobjects.org/analyzerbeans/job/1.0}data-context>",
					e.getMessage());
		}
	}

	public void testMissingDatastore() throws Exception {
		AnalyzerBeansConfiguration configuration = TestHelper
				.createAnalyzerBeansConfiguration();
		JaxbJobFactory factory = new JaxbJobFactory(configuration);
		try {
			factory.create(new File("src/test/resources/example-job-valid.xml"));
			fail("Exception expected");
		} catch (IllegalStateException e) {
			assertEquals("No such datastore: my database", e.getMessage());
		}
	}

	public void testMissingTransformerDescriptor() throws Exception {
		AnalyzerBeansConfiguration configuration = TestHelper
				.createAnalyzerBeansConfiguration(TestHelper
						.createSampleDatabaseDatastore("my database"));
		JaxbJobFactory factory = new JaxbJobFactory(configuration);
		try {
			factory.create(new File(
					"src/test/resources/example-job-missing-descriptor.xml"));
			fail("Exception expected");
		} catch (IllegalStateException e) {
			assertEquals("No such transformer descriptor: tokenizerDescriptor",
					e.getMessage());
		}
	}

	public void testValidJob() throws Exception {
		AnalyzerBeansConfiguration configuration = TestHelper
				.createAnalyzerBeansConfiguration(TestHelper
						.createSampleDatabaseDatastore("my database"));
		JaxbJobFactory factory = new JaxbJobFactory(configuration);
		AnalysisJobBuilder builder = factory.create(new File(
				"src/test/resources/example-job-valid.xml"));
		assertTrue(builder.isConfigured());

		List<MetaModelInputColumn> sourceColumns = builder.getSourceColumns();
		assertEquals(3, sourceColumns.size());
		assertEquals(
				"MetaModelInputColumn[JdbcColumn[name=FIRSTNAME,columnNumber=2,type=VARCHAR,nullable=false,indexed=false,nativeType=VARCHAR,columnSize=50]]",
				sourceColumns.get(0).toString());
		assertEquals(
				"MetaModelInputColumn[JdbcColumn[name=LASTNAME,columnNumber=1,type=VARCHAR,nullable=false,indexed=false,nativeType=VARCHAR,columnSize=50]]",
				sourceColumns.get(1).toString());
		assertEquals(
				"MetaModelInputColumn[JdbcColumn[name=EMAIL,columnNumber=4,type=VARCHAR,nullable=false,indexed=false,nativeType=VARCHAR,columnSize=100]]",
				sourceColumns.get(2).toString());

		assertEquals(1, builder.getTransformerJobBuilders().size());
		assertEquals(
				"[TransformedInputColumn[id=trans-1,name=username,type=STRING], "
						+ "TransformedInputColumn[id=trans-2,name=domain,type=STRING]]",
				builder.getTransformerJobBuilders().get(0).getOutputColumns()
						.toString());
		assertEquals(
				"[TransformedInputColumn[id=trans-1,name=username,type=STRING], "
						+ "TransformedInputColumn[id=trans-2,name=domain,type=STRING], "
						+ "MetaModelInputColumn[JdbcColumn[name=FIRSTNAME,columnNumber=2,type=VARCHAR,nullable=false,indexed=false,nativeType=VARCHAR,columnSize=50]], "
						+ "MetaModelInputColumn[JdbcColumn[name=LASTNAME,columnNumber=1,type=VARCHAR,nullable=false,indexed=false,nativeType=VARCHAR,columnSize=50]]]",
				Arrays.toString(builder.getAnalyzerJobBuilders().get(0)
						.toAnalyzerJob().getInput()));

		List<AnalyzerResult> results = new AnalysisRunnerImpl(configuration)
				.run(builder.toAnalysisJob()).getResults();
		assertEquals(1, results.size());
		CrosstabResult crosstabResult = (CrosstabResult) results.get(0);
		assertEquals("Crosstab:|" + "domain,Lowercase chars: 95%|"
				+ "domain,Uppercase chars: 0%|" + "domain,Avg white spaces: 0|"
				+ "username,Max chars: 10|" + "FIRSTNAME,Word count: 24|"
				+ "domain,Word count: 23|" + "username,Max white spaces: 0|"
				+ "FIRSTNAME,Max words: 2|" + "LASTNAME,Min words: 1|"
				+ "FIRSTNAME,Max white spaces: 1|"
				+ "username,Char count: 172|" + "username,Avg chars: 7,48|"
				+ "LASTNAME,Word count: 23|" + "username,Min white spaces: 0|"
				+ "LASTNAME,Max white spaces: 0|" + "LASTNAME,Char count: 147|"
				+ "FIRSTNAME,Avg chars: 5,39|" + "domain,Min white spaces: 0|"
				+ "domain,Max chars: 20|" + "LASTNAME,Avg chars: 6,39|"
				+ "username,Uppercase chars: 0%|"
				+ "username,Lowercase chars: 100%|" + "LASTNAME,Max words: 1|"
				+ "LASTNAME,Avg white spaces: 0|" + "LASTNAME,Min chars: 3|"
				+ "domain,Min chars: 20|" + "FIRSTNAME,Min white spaces: 0|"
				+ "username,Avg white spaces: 0|"
				+ "FIRSTNAME,Char count: 124|"
				+ "LASTNAME,Min white spaces: 0|" + "domain,Avg chars: 20|"
				+ "FIRSTNAME,Non-letter chars: 0%|" + "domain,Max words: 1|"
				+ "FIRSTNAME,Uppercase chars: 19%|" + "username,Max words: 1|"
				+ "FIRSTNAME,Min words: 1|" + "username,Min chars: 5|"
				+ "LASTNAME,Lowercase chars: 84%|" + "username,Min words: 1|"
				+ "FIRSTNAME,Max chars: 8|" + "domain,Min words: 1|"
				+ "FIRSTNAME,Avg white spaces: 0,04|"
				+ "username,Word count: 23|" + "LASTNAME,Uppercase chars: 15%|"
				+ "LASTNAME,Non-letter chars: 0%|" + "domain,Char count: 460|"
				+ "username,Non-letter chars: 0%|" + "FIRSTNAME,Min chars: 3|"
				+ "domain,Non-letter chars: 5%|"
				+ "domain,Max white spaces: 0|"
				+ "FIRSTNAME,Lowercase chars: 79%|" + "LASTNAME,Max chars: 9",
				crosstabResult.toString().replaceAll("\n", "|"));
	}
}
