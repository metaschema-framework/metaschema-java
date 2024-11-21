/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.impl;

import com.squareup.javapoet.AnnotationSpec;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.ILet;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;
import java.util.List;
import java.util.Map;

class AnnotationGeneratorTest {
  @RegisterExtension
  final JUnit5Mockery context = new JUnit5Mockery();

  @Test
  void letAssignmentTest() {
    ISource source = ISource.externalSource(ObjectUtils.notNull(URI.create("https://example.com/")));

    String variable = "var1";
    String expression = "1 + 1";
    MarkupMultiline remarks = MarkupMultiline.fromMarkdown("Test");

    ILet let = ILet.of(
        IEnhancedQName.of(variable),
        expression,
        source,
        remarks);

    AnnotationSpec.Builder annotation = ObjectUtils.notNull(AnnotationSpec.builder(BoundFlag.class));
    IFlagDefinition flag = ObjectUtils.notNull(context.mock(IFlagDefinition.class));

    context.checking(new Expectations() {
      {
        allowing(flag).getLetExpressions();
        will(returnValue(Map.ofEntries(Map.entry(let.getName(), let))));
        allowing(flag).getAllowedValuesConstraints();
        will(returnValue(List.of()));
        allowing(flag).getIndexHasKeyConstraints();
        will(returnValue(List.of()));
        allowing(flag).getMatchesConstraints();
        will(returnValue(List.of()));
        allowing(flag).getExpectConstraints();
        will(returnValue(List.of()));
      }
    });

    AnnotationGenerator.buildValueConstraints(annotation, flag);
  }
}
