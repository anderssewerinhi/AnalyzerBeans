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
package org.eobjects.analyzer.descriptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eobjects.analyzer.beans.api.Analyzer;
import org.eobjects.analyzer.beans.api.Filter;
import org.eobjects.analyzer.beans.api.Renderer;
import org.eobjects.analyzer.beans.api.Transformer;

/**
 * A simple descriptor provider with a method signature suitable externalizing
 * class names of analyzer and transformer beans. For example, if you're using
 * the Spring Framework you initialize this descriptor provider as follows:
 * 
 * <pre>
 * &lt;bean id="descriptorProvider" class="org.eobjects.analyzer.descriptors.SimpleDescriptorProvider"&gt;
 *   &lt;property name="analyzerClassNames"&gt;
 *     &lt;list&gt;
 *       &lt;value&gt;org.eobjects.analyzer.beans.StringAnalyzer&lt;/value&gt;
 *       &lt;value&gt;org.eobjects.analyzer.beans.valuedist.ValueDistributionAnalyzer&lt;/value&gt;
 *       ...
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 *   &lt;property name="transformerClassNames"&gt;
 *     &lt;list&gt;
 *       &lt;value&gt;org.eobjects.analyzer.beans.TokenizerTransformer&lt;/value&gt;
 *       ...
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 *   &lt;property name="rendererClassNames"&gt;
 *     &lt;list&gt;
 *       &lt;value&gt;org.eobjects.analyzer.result.renderer.DefaultTextRenderer&lt;/value&gt;
 *       ...
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * @author Kasper Sørensen
 */
public class SimpleDescriptorProvider extends AbstractDescriptorProvider {

    private List<AnalyzerBeanDescriptor<?>> _analyzerBeanDescriptors = new ArrayList<AnalyzerBeanDescriptor<?>>();
    private List<TransformerBeanDescriptor<?>> _transformerBeanDescriptors = new ArrayList<TransformerBeanDescriptor<?>>();
    private List<RendererBeanDescriptor<?>> _rendererBeanDescriptors = new ArrayList<RendererBeanDescriptor<?>>();
    private List<FilterBeanDescriptor<?, ?>> _filterBeanDescriptors = new ArrayList<FilterBeanDescriptor<?, ?>>();

    private final boolean _autoDiscover;

    public SimpleDescriptorProvider() {
        this(true);
    }

    public SimpleDescriptorProvider(boolean autoDiscover) {
        _autoDiscover = autoDiscover;
    }

    @Override
    protected <A extends Analyzer<?>> AnalyzerBeanDescriptor<A> notFoundAnalyzer(Class<A> analyzerClass) {
        if (!_autoDiscover) {
            return null;
        }
        return Descriptors.ofAnalyzer(analyzerClass);
    }

    @Override
    protected FilterBeanDescriptor<?, ?> notFoundFilter(Class<?> filterClass) {
        if (!_autoDiscover) {
            return null;
        }
        return Descriptors.ofFilterUnbound(filterClass);
    }

    @Override
    protected <R extends Renderer<?, ?>> RendererBeanDescriptor<R> notFoundRenderer(Class<R> rendererClass) {
        if (!_autoDiscover) {
            return null;
        }
        return Descriptors.ofRenderer(rendererClass);
    }

    @Override
    protected <A extends Transformer<?>> TransformerBeanDescriptor<A> notFoundTransformer(Class<A> transformerClass) {
        if (!_autoDiscover) {
            return null;
        }
        return Descriptors.ofTransformer(transformerClass);
    }

    public void addAnalyzerBeanDescriptor(AnalyzerBeanDescriptor<?> analyzerBeanDescriptor) {
        _analyzerBeanDescriptors.add(analyzerBeanDescriptor);
    }

    public void addTransformerBeanDescriptor(TransformerBeanDescriptor<?> transformerBeanDescriptor) {
        _transformerBeanDescriptors.add(transformerBeanDescriptor);
    }

    public void addRendererBeanDescriptor(RendererBeanDescriptor<?> rendererBeanDescriptor) {
        _rendererBeanDescriptors.add(rendererBeanDescriptor);
    }

    public void addFilterBeanDescriptor(FilterBeanDescriptor<?, ?> descriptor) {
        _filterBeanDescriptors.add(descriptor);
    }

    @Override
    public List<AnalyzerBeanDescriptor<?>> getAnalyzerBeanDescriptors() {
        return _analyzerBeanDescriptors;
    }

    public void setAnalyzerBeanDescriptors(List<AnalyzerBeanDescriptor<?>> descriptors) {
        _analyzerBeanDescriptors = descriptors;
    }

    @Override
    public List<TransformerBeanDescriptor<?>> getTransformerBeanDescriptors() {
        return _transformerBeanDescriptors;
    }

    public void setTransformerBeanDescriptors(List<TransformerBeanDescriptor<?>> transformerBeanDescriptors) {
        _transformerBeanDescriptors = transformerBeanDescriptors;
    }

    @Override
    public List<RendererBeanDescriptor<?>> getRendererBeanDescriptors() {
        return _rendererBeanDescriptors;
    }

    public void setRendererBeanDescriptors(List<RendererBeanDescriptor<?>> rendererBeanDescriptors) {
        _rendererBeanDescriptors = rendererBeanDescriptors;
    }

    @Override
    public Collection<FilterBeanDescriptor<?, ?>> getFilterBeanDescriptors() {
        return _filterBeanDescriptors;
    }

    public void setFilterBeanDescriptors(List<FilterBeanDescriptor<?, ?>> filterBeanDescriptors) {
        _filterBeanDescriptors = filterBeanDescriptors;
    }

    public void setAnalyzerClassNames(Collection<String> classNames) throws ClassNotFoundException {
        for (String className : classNames) {
            @SuppressWarnings("unchecked")
            Class<? extends Analyzer<?>> c = (Class<? extends Analyzer<?>>) Class.forName(className);
            AnalyzerBeanDescriptor<?> descriptor = getAnalyzerBeanDescriptorForClass(c);
            if (descriptor == null || !_analyzerBeanDescriptors.contains(descriptor)) {
                addAnalyzerBeanDescriptor(Descriptors.ofAnalyzer(c));
            }
        }
    }

    public void setTransformerClassNames(Collection<String> classNames) throws ClassNotFoundException {
        for (String className : classNames) {
            @SuppressWarnings("unchecked")
            Class<? extends Transformer<?>> c = (Class<? extends Transformer<?>>) Class.forName(className);
            TransformerBeanDescriptor<?> descriptor = getTransformerBeanDescriptorForClass(c);
            if (descriptor == null || !_transformerBeanDescriptors.contains(descriptor)) {
                addTransformerBeanDescriptor(Descriptors.ofTransformer(c));
            }
        }
    }

    public void setRendererClassNames(Collection<String> classNames) throws ClassNotFoundException {
        for (String className : classNames) {
            @SuppressWarnings("unchecked")
            Class<? extends Renderer<?, ?>> c = (Class<? extends Renderer<?, ?>>) Class.forName(className);
            RendererBeanDescriptor<?> descriptor = getRendererBeanDescriptorForClass(c);
            if (descriptor == null || !_rendererBeanDescriptors.contains(descriptor)) {
                addRendererBeanDescriptor(Descriptors.ofRenderer(c));
            }
        }
    }

    public void setFilterClassNames(Collection<String> classNames) throws ClassNotFoundException {
        for (String className : classNames) {
            @SuppressWarnings("unchecked")
            Class<? extends Filter<?>> c = (Class<? extends Filter<?>>) Class.forName(className);

            FilterBeanDescriptor<?, ?> descriptor = getFilterBeanDescriptorForClassUnbounded(c);

            if (descriptor == null || !_filterBeanDescriptors.contains(descriptor)) {
                addFilterBeanDescriptor(Descriptors.ofFilterUnbound(c));
            }
        }
    }

}
