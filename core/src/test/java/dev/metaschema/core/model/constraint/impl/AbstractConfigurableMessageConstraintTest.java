/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.net.URI;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.item.node.IDocumentNodeItem;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.ConstraintValidationException;
import dev.metaschema.core.model.constraint.DefaultConstraintValidator;
import dev.metaschema.core.model.constraint.FindingCollectingConstraintValidationHandler;
import dev.metaschema.core.model.constraint.IExpectConstraint;
import dev.metaschema.core.testsupport.mocking.MockedDocumentGenerator;
import dev.metaschema.core.util.ObjectUtils;

class AbstractConfigurableMessageConstraintTest
    extends ExpressionTestBase {

  @Test
  void testDifferentNS() throws ConstraintValidationException {
    StaticContext constraintContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .baseUri(ObjectUtils.notNull(URI.create("https://example.com/other")))
        .build();

    IExpectConstraint expect = IExpectConstraint.builder()
        .target(IMetapathExpression.compile("assembly", constraintContext))
        .test(IMetapathExpression.compile("ancestor::root", constraintContext))
        .source(ISource.externalSource(constraintContext, false))
        .build();

    IDocumentNodeItem document = MockedDocumentGenerator.generateDocumentNodeItem();
    document.getRootAssemblyNodeItem().getDefinition().addConstraint(expect);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      validator.validate(
          document,
          new DynamicContext(document.getStaticContext()));
    }
    assertAll(
        () -> assertTrue(handler.isPassing()),
        () -> assertEquals(0, handler.getFindings().size()));
  }

  @Test
  void testWildCard() throws ConstraintValidationException {
    StaticContext constraintContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .baseUri(ObjectUtils.notNull(URI.create("https://example.com/other")))
        .build();

    IExpectConstraint expect = IExpectConstraint.builder()
        .target(IMetapathExpression.compile("assembly", constraintContext))
        .test(IMetapathExpression.compile("ancestor::*:root", constraintContext))
        .source(ISource.externalSource(constraintContext, false))
        .build();

    IDocumentNodeItem document = MockedDocumentGenerator.generateDocumentNodeItem();
    document.getRootAssemblyNodeItem().getDefinition().addConstraint(expect);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      validator.validate(
          document,
          new DynamicContext(document.getStaticContext()));
    }
    assertAll(
        () -> assertTrue(handler.isPassing()),
        () -> assertEquals(0, handler.getFindings().size()));
  }

  @Test
  void testPrefix() throws ConstraintValidationException {
    StaticContext constraintContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .baseUri(ObjectUtils.notNull(URI.create("https://example.com/other")))
        .namespace("ns", MockedDocumentGenerator.NS)
        .build();

    IExpectConstraint expect = IExpectConstraint.builder()
        .target(IMetapathExpression.compile("ns:assembly", constraintContext))
        .test(IMetapathExpression.compile("ancestor::ns:root", constraintContext))
        .source(ISource.externalSource(constraintContext, false))
        .build();

    IDocumentNodeItem document = MockedDocumentGenerator.generateDocumentNodeItem();
    document.getRootAssemblyNodeItem().getDefinition().addConstraint(expect);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      validator.validate(
          document,
          new DynamicContext(document.getStaticContext()));
    }
    assertAll(
        () -> assertTrue(handler.isPassing()),
        () -> assertEquals(0, handler.getFindings().size()));
  }

  @Test
  void testQualifiedName() throws ConstraintValidationException {
    StaticContext constraintContext = StaticContext.builder()
        .defaultModelNamespace(NS)
        .baseUri(ObjectUtils.notNull(URI.create("https://example.com/other")))
        .build();

    IExpectConstraint expect = IExpectConstraint.builder()
        .target(IMetapathExpression.compile("Q{" + MockedDocumentGenerator.NS + "}assembly", constraintContext))
        .test(IMetapathExpression.compile("ancestor::Q{" + MockedDocumentGenerator.NS + "}root", constraintContext))
        .source(ISource.externalSource(constraintContext, false))
        .build();

    IDocumentNodeItem document = MockedDocumentGenerator.generateDocumentNodeItem();
    document.getRootAssemblyNodeItem().getDefinition().addConstraint(expect);

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      validator.validate(
          document,
          new DynamicContext(document.getStaticContext()));
    }
    assertAll(
        () -> assertTrue(handler.isPassing()),
        () -> assertEquals(0, handler.getFindings().size()));
  }
}
