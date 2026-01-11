/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import java.net.URI;

import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.format.IPathFormatter;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.item.node.IFlagNodeItem;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.testsupport.mocking.MockNodeItemFactory;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for {@link IMatchesConstraint} validation.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
class MatchesConstraintTest {
  @NonNull
  private static final String NS = ObjectUtils.notNull(URI.create("http://example.com/ns").toASCIIString());

  @NonNull
  private static IEnhancedQName qname(@NonNull String name) {
    return IEnhancedQName.of(NS, name);
  }

  /**
   * Tests that a value matching the regex pattern passes validation.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testMatchesRegexSuccess() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("email"), IStringItem.valueOf("test@example.com"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "expected validation to pass");
  }

  /**
   * Tests that a value not matching the regex pattern fails validation.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testMatchesRegexFailure() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("email"), IStringItem.valueOf("invalid-email"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "expected validation to fail"),
        () -> assertThat("expected one finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding is for the flag node", handler.getFindings(),
            hasItem(hasProperty("node", is(flag)))));
  }

  /**
   * Tests that a value conforming to a datatype passes validation.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testMatchesDatatypeSuccess() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("uuid"),
        IStringItem.valueOf("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .datatype(MetaschemaDataTypeProvider.UUID)
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "expected validation to pass");
  }

  /**
   * Tests that a value not conforming to a datatype fails validation.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testMatchesDatatypeFailure() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("uuid"), IStringItem.valueOf("not-a-uuid"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .datatype(MetaschemaDataTypeProvider.UUID)
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "expected validation to fail"),
        () -> assertThat("expected one finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding is for the flag node", handler.getFindings(),
            hasItem(hasProperty("node", is(flag)))));
  }

  /**
   * Tests that a value matching both regex and datatype passes validation.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testMatchesRegexAndDatatypeSuccess() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("uuid"),
        IStringItem.valueOf("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$")
        .datatype(MetaschemaDataTypeProvider.UUID)
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "expected validation to pass");
  }

  /**
   * Tests that a value matching regex but not datatype fails validation.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testMatchesRegexButNotDatatype() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    // This value matches a simple UUID-like pattern but is not a valid UUID (has
    // invalid characters)
    IFlagNodeItem flag = itemFactory.flag(qname("uuid"),
        IStringItem.valueOf("not-a-valid-uuid-value-here"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^[a-z-]+$")
        .datatype(MetaschemaDataTypeProvider.UUID)
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    // The validation should fail because the datatype check fails
    assertAll(
        () -> assertFalse(handler.isPassing(), "expected validation to fail"),
        () -> assertThat("expected one finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding is for the flag node", handler.getFindings(),
            hasItem(hasProperty("node", is(flag)))));
  }

  /**
   * Tests complex regex patterns with special characters.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testComplexRegexPattern() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    // Test a complex pattern for version strings
    IFlagNodeItem flag = itemFactory.flag(qname("version"), IStringItem.valueOf("1.2.3-beta.1"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^\\d+\\.\\d+\\.\\d+(-(alpha|beta|rc)\\.\\d+)?$")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "expected validation to pass");
  }

  /**
   * Tests edge case with empty string.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testEmptyStringFailsPattern() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf(""));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    // Pattern requires at least one character
    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^.+$")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "expected validation to fail"),
        () -> assertThat("expected one finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding is for the flag node", handler.getFindings(),
            hasItem(hasProperty("node", is(flag)))));
  }

  /**
   * Tests edge case with empty string that matches pattern.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testEmptyStringMatchesPattern() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf(""));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    // Pattern allows empty string
    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^.*$")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "expected validation to pass");
  }

  /**
   * Tests pattern with special regex characters.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testSpecialCharactersInValue() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("path"), IStringItem.valueOf("/usr/local/bin"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    // Pattern for Unix-style paths
    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^(/[a-z]+)+$")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "expected validation to pass");
  }

  /**
   * Tests URI datatype validation with valid URI.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testUriDatatypeSuccess() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("url"),
        IStringItem.valueOf("https://example.com/path"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .datatype(MetaschemaDataTypeProvider.URI)
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "expected validation to pass");
  }

  /**
   * Tests case-sensitive regex pattern matching.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testCaseSensitiveRegex() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("code"), IStringItem.valueOf("ABC123"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    // Pattern requires uppercase letters followed by digits
    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^[A-Z]+\\d+$")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertTrue(handler.isPassing(), "expected validation to pass");
  }

  /**
   * Tests case-sensitive regex pattern failing with wrong case.
   *
   * @throws ConstraintValidationException
   *           if validation fails unexpectedly
   */
  @SuppressWarnings("null")
  @Test
  void testCaseSensitiveRegexFailure() throws ConstraintValidationException {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory();

    IFlagNodeItem flag = itemFactory.flag(qname("code"), IStringItem.valueOf("abc123"));

    IFlagDefinition flagDefinition = mock(IFlagDefinition.class);
    ISource source = mock(ISource.class);

    // Pattern requires uppercase letters followed by digits
    IMatchesConstraint matchesConstraint = IMatchesConstraint.builder()
        .source(source)
        .regex("^[A-Z]+\\d+$")
        .build();

    doReturn(flagDefinition).when(flag).getDefinition();
    doReturn("flag/path").when(flag).toPath(any(IPathFormatter.class));

    doReturn(CollectionUtil.emptyMap()).when(flagDefinition).getLetExpressions();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getAllowedValuesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getExpectConstraints();
    doReturn(CollectionUtil.singletonList(matchesConstraint)).when(flagDefinition).getMatchesConstraints();
    doReturn(CollectionUtil.emptyList()).when(flagDefinition).getIndexHasKeyConstraints();

    StaticContext staticContext = StaticContext.instance();
    doReturn(staticContext).when(source).getStaticContext();

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    try (DefaultConstraintValidator validator = new DefaultConstraintValidator(handler)) {
      DynamicContext dynamicContext = new DynamicContext(staticContext);
      validator.validate(flag, dynamicContext);
      validator.finalizeValidation(dynamicContext);
    }
    assertAll(
        () -> assertFalse(handler.isPassing(), "expected validation to fail"),
        () -> assertThat("expected one finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding is for the flag node", handler.getFindings(),
            hasItem(hasProperty("node", is(flag)))));
  }
}
