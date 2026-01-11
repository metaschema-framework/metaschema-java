/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.impl;

import com.squareup.javapoet.AnnotationSpec;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;
import java.util.List;
import java.util.Map;

import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.ILet;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.annotations.BoundFlag;

class AnnotationGeneratorTest {
  @RegisterExtension
  final JUnit5Mockery context = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };

  @Test
  void letAssignmentTest() {
    ISource source = ISource.externalSource(ObjectUtils.notNull(URI.create("https://example.com/")));

    String variable = "var1";
    String expression = "1 + 1";
    MarkupMultiline remarks = MarkupMultiline.fromMarkdown("Test");

    ILet let = ILet.of(
        IEnhancedQName.of(variable),
        IMetapathExpression.compile(expression, source.getStaticContext()),
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
        allowing(flag).getReportConstraints();
        will(returnValue(List.of()));
      }
    });

    AnnotationGenerator.buildValueConstraints(annotation, flag);
  }
}
