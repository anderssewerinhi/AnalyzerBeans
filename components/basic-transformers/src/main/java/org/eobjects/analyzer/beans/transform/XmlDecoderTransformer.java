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

import org.apache.commons.lang.StringEscapeUtils;
import org.eobjects.analyzer.beans.api.Categorized;
import org.eobjects.analyzer.beans.api.Configured;
import org.eobjects.analyzer.beans.api.Description;
import org.eobjects.analyzer.beans.api.OutputColumns;
import org.eobjects.analyzer.beans.api.Transformer;
import org.eobjects.analyzer.beans.api.TransformerBean;
import org.eobjects.analyzer.beans.categories.StringManipulationCategory;
import org.eobjects.analyzer.data.InputColumn;
import org.eobjects.analyzer.data.InputRow;
import org.eobjects.analyzer.data.MockInputColumn;

@TransformerBean("XML decoder")
@Description("Decodes XML content into plain text")
@Categorized({ StringManipulationCategory.class })
public class XmlDecoderTransformer implements Transformer<String> {

	@Configured
	InputColumn<String> column;
	
	public XmlDecoderTransformer() {
	}

	public XmlDecoderTransformer(MockInputColumn<String> column) {
		this();
		this.column = column;
	}

	@Override
	public OutputColumns getOutputColumns() {
		return new OutputColumns(column.getName() + " (XML decoded)");
	}

	@Override
	public String[] transform(InputRow inputRow) {
		String value = inputRow.getValue(column);
		if (value != null) {
			value = StringEscapeUtils.unescapeXml(value);
		}
		return new String[] { value };
	}

}
