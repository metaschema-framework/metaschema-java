/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.net.URI;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.qname.IEnhancedQName;

/**
 * Tests for constraint and let-statement identifier generation.
 */
class ConstraintIdentifierTest {

  @Test
  void testConstraintWithExplicitIdUsesIt() {
    ISource source = ISource.externalSource("https://example.com/module");

    IExpectConstraint constraint = IExpectConstraint.builder()
        .source(source)
        .identifier("my-explicit-id")
        .test(IMetapathExpression.compile("true()"))
        .build();

    assertEquals("my-explicit-id", constraint.getInternalIdentifier());
  }

  @Test
  void testConstraintWithoutIdGetsDeterministicFallback() {
    ISource source = ISource.externalSource("https://example.com/module");

    IExpectConstraint constraint = IExpectConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("string-length(.) > 0"))
        .build();

    String identifier = constraint.getInternalIdentifier();
    assertNotNull(identifier);
    assertTrue(identifier.startsWith("expect-"),
        "fallback identifier should start with constraint type name, got: " + identifier);
  }

  @Test
  void testConstraintIdentifierIsCached() {
    ISource source = ISource.externalSource("https://example.com/module");

    IExpectConstraint constraint = IExpectConstraint.builder()
        .source(source)
        .identifier("cached-id")
        .test(IMetapathExpression.compile("true()"))
        .build();

    String first = constraint.getInternalIdentifier();
    String second = constraint.getInternalIdentifier();
    assertSame(first, second, "repeated calls should return the same cached instance");
  }

  @Test
  void testConstraintFallbackIsDeterministicAcrossCalls() {
    ISource source = ISource.externalSource("https://example.com/module");

    IExpectConstraint constraint = IExpectConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("string-length(.) > 0"))
        .build();

    String first = constraint.getInternalIdentifier();
    String second = constraint.getInternalIdentifier();
    assertEquals(first, second, "fallback identifier should be deterministic");
  }

  @Test
  void testDifferentConstraintTypesGetDifferentPrefixes() {
    ISource source = ISource.externalSource("https://example.com/module");

    IExpectConstraint expect = IExpectConstraint.builder()
        .source(source)
        .test(IMetapathExpression.compile("true()"))
        .build();

    StaticContext staticContext = StaticContext.builder()
        .baseUri(URI.create("https://example.com/module"))
        .build();

    IMatchesConstraint matches = IMatchesConstraint.builder()
        .source(ISource.externalSource(staticContext, false))
        .regex(".*")
        .build();

    assertTrue(expect.getInternalIdentifier().startsWith("expect-"),
        "expect constraint identifier should start with 'expect-'");
    assertTrue(matches.getInternalIdentifier().startsWith("matches-"),
        "matches constraint identifier should start with 'matches-'");
  }

  @Test
  void testLetIdentifierFormat() {
    ISource source = ISource.externalSource("https://example.com/module");
    IEnhancedQName name = IEnhancedQName.of("my-variable");

    ILet let = ILet.of(
        name,
        IMetapathExpression.compile("count(//item)"),
        source,
        null);

    String identifier = let.getInternalIdentifier();
    assertNotNull(identifier);
    assertTrue(identifier.startsWith("let-my-variable"),
        "let identifier should start with 'let-{name}', got: " + identifier);
  }

  @Test
  void testLetIdentifierIsCached() {
    ISource source = ISource.externalSource("https://example.com/module");
    IEnhancedQName name = IEnhancedQName.of("cached-var");

    ILet let = ILet.of(
        name,
        IMetapathExpression.compile("true()"),
        source,
        null);

    String first = let.getInternalIdentifier();
    String second = let.getInternalIdentifier();
    assertSame(first, second, "repeated calls should return the same cached instance");
  }

  @Test
  void testLetIdentifierIsDeterministic() {
    ISource source = ISource.externalSource("https://example.com/module");
    IEnhancedQName name = IEnhancedQName.of("det-var");

    ILet let = ILet.of(
        name,
        IMetapathExpression.compile("count(//item)"),
        source,
        null);

    String first = let.getInternalIdentifier();
    String second = let.getInternalIdentifier();
    assertEquals(first, second, "let identifier should be deterministic");
  }
}
