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
 * 
 * This annotation is used to specify string inputs as single line or multiline
 */
package org.eobjects.analyzer.beans.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Annotation containing supplementary metadata about a string property. This
 * metadata can be used as a way to give hints to the UI as to how the content
 * should be presented.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
@Inherited
@Qualifier
public @interface StringProperty {

    /**
     * @return true if the input field is multiline
     */
    public boolean multiline() default false;

    /**
     * @return the mime type (and optionally aliases/alternative mime types) of
     *         the property
     */
    public String[] mimeType() default {};

    /**
     * @return true if the input field represents a password (or other similar
     *         security token), not shown literally to the user.
     */
    public boolean password() default false;
    
    /**
     * @return true if an empty string value is acceptable
     */
    public boolean emptyString() default false;
}
