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
package org.eobjects.analyzer.util;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

	public void testRightTrim() throws Exception {
		assertEquals("hello", StringUtils.rightTrim("hello  "));
		assertEquals("hello", StringUtils.rightTrim("hello \t "));
		assertEquals("hello", StringUtils.rightTrim("hello"));
		assertEquals("  hello", StringUtils.rightTrim("  hello  "));
		assertEquals("", StringUtils.rightTrim(" "));
		assertEquals("", StringUtils.rightTrim(""));
	}

	public void testLeftTrim() throws Exception {
		assertEquals("hello", StringUtils.leftTrim("  hello"));
		assertEquals("hello", StringUtils.leftTrim(" \t hello"));
		assertEquals("hello", StringUtils.leftTrim("hello"));
		assertEquals("hello  ", StringUtils.leftTrim("  hello  "));
		assertEquals("", StringUtils.leftTrim(" "));
		assertEquals("", StringUtils.leftTrim(""));
	}

	public void testBuiltInTrimUsesWhitespaces() throws Exception {
		assertEquals("hello", " hello \t ".trim());
	}
	
	public void testIsLatin() throws Exception {
		assertTrue(StringUtils.isLatin('a'));
		assertTrue(StringUtils.isLatin('z'));
		assertTrue(StringUtils.isLatin('A'));
		assertTrue(StringUtils.isLatin('Z'));
		assertTrue(StringUtils.isLatin('c'));
		assertTrue(StringUtils.isLatin('D'));
		assertFalse(StringUtils.isLatin('æ'));
		assertFalse(StringUtils.isLatin('á'));
		assertFalse(StringUtils.isLatin('Æ'));
		assertFalse(StringUtils.isLatin('Ä'));
	}
	
	public void testIsDiacritic() throws Exception {
		assertTrue(StringUtils.isDiacritic('æ'));
		assertFalse(StringUtils.isDiacritic('a'));
		assertFalse(StringUtils.isDiacritic('z'));
		assertFalse(StringUtils.isDiacritic('Z'));
		assertTrue(StringUtils.isDiacritic('Ø'));
		assertTrue(StringUtils.isDiacritic('ó'));
		assertTrue(StringUtils.isDiacritic('š'));
		assertTrue(StringUtils.isDiacritic('á'));
	}
	
	public void testIndexOf() throws Exception {
		assertEquals(-1, StringUtils.indexOf('a', "bcd".toCharArray()));
		assertEquals(0, StringUtils.indexOf('a', "abcd".toCharArray()));
		assertEquals(2, StringUtils.indexOf('c', "abcd".toCharArray()));
		assertEquals(3, StringUtils.indexOf('d', "abcd".toCharArray()));
	}
}