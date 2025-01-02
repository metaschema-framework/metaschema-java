/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.ILet;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.Let;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;

class ConstraintFactoryTest {
  @RegisterExtension
  final JUnit5Mockery context = new JUnit5Mockery();

  @SuppressWarnings("null")
  @Test
  void letExpressionTest() {
    ISource source = ISource.externalSource(ObjectUtils.notNull(URI.create("https://example.com/")));

    String variable = "var1";
    String expression = "1 + 1";
    MarkupMultiline remarks = MarkupMultiline.fromMarkdown("Test");

    Let annotation = context.mock(Let.class);

    context.checking(new Expectations() {
      {
        allowing(annotation).name();
        will(returnValue(variable));
        allowing(annotation).target();
        will(returnValue(expression));
        allowing(annotation).remarks();
        will(returnValue(remarks.toMarkdown()));
      }
    });

    ILet let = ConstraintFactory.newLetExpression(annotation, source);
    MarkupMultiline letRemarks = let.getRemarks();
    assertAll(
        () -> assertEquals(IEnhancedQName.of(variable), let.getName()),
        () -> assertEquals(expression, let.getValueExpression().getPath()),
        () -> assertEquals(source, let.getSource()),
        () -> assertEquals("Test", letRemarks == null ? null : letRemarks.toMarkdown()));
  }

}
