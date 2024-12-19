/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.function.library.impl.MockedDocumentGenerator;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.DefaultConstraintValidator;
import gov.nist.secauto.metaschema.core.model.constraint.FindingCollectingConstraintValidationHandler;
import gov.nist.secauto.metaschema.core.model.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.net.URI;

class AbstractConfigurableMessageConstraintTest
    extends ExpressionTestBase {

  @Test
  void testDifferentNS() {
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
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    validator.validate(
        document,
        new DynamicContext(document.getStaticContext()));

    assertAll(
        () -> assertTrue(handler.isPassing()),
        () -> assertEquals(0, handler.getFindings().size()));
  }

  @Test
  void testWildCard() {
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
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    validator.validate(
        document,
        new DynamicContext(document.getStaticContext()));

    assertAll(
        () -> assertTrue(handler.isPassing()),
        () -> assertEquals(0, handler.getFindings().size()));
  }

  @Test
  void testPrefix() {
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
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    validator.validate(
        document,
        new DynamicContext(document.getStaticContext()));

    assertAll(
        () -> assertTrue(handler.isPassing()),
        () -> assertEquals(0, handler.getFindings().size()));
  }

  @Test
  void testQualifiedName() {
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
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    validator.validate(
        document,
        new DynamicContext(document.getStaticContext()));

    assertAll(
        () -> assertTrue(handler.isPassing()),
        () -> assertEquals(0, handler.getFindings().size()));
  }
}
