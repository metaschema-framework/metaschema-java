/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.constraint.ILet;
import gov.nist.secauto.metaschema.core.model.constraint.ISource;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.Let;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;

import javax.xml.namespace.QName;

class ConstraintFactoryTest {
  @RegisterExtension
  final JUnit5Mockery context = new JUnit5Mockery();

  @SuppressWarnings("null")
  @Test
  void letExpressionTest() {
    ISource source = ISource.externalSource(StaticContext.builder()
        .baseUri(ObjectUtils.notNull(URI.create("https://example.com/")))
        .build());

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
    assertAll(
        () -> assertEquals(new QName(variable), let.getName()),
        () -> assertEquals(expression, let.getValueExpression().getPath()),
        () -> assertEquals(source, let.getSource()),
        () -> assertEquals("Test", let.getRemarks().toMarkdown()));
  }

}
