/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.function.regex.RegexUtil;
import gov.nist.secauto.metaschema.core.metapath.function.regex.RegularExpressionMetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-matches">fn:matches</a>.
 */
public final class FnMatches {
  private static final String NAME = "matches";
  @NonNull
  static final IFunction SIGNATURE_TWO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("input")
          .type(IStringItem.class)
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("pattern")
          .type(IStringItem.class)
          .one()
          .build())
      .returnType(IBooleanItem.class)
      .returnOne()
      .functionHandler(FnMatches::executeTwoArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_THREE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("input")
          .type(IStringItem.class)
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("pattern")
          .type(IStringItem.class)
          .one()
          .build())
      .argument(IArgument.builder()
          .name("flags")
          .type(IStringItem.class)
          .one()
          .build())
      .returnType(IBooleanItem.class)
      .returnOne()
      .functionHandler(FnMatches::executeThreeArg)
      .build();

  @NonNull
  private static ISequence<IBooleanItem> executeTwoArg(
      @SuppressWarnings("unused") @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @SuppressWarnings("unused") @NonNull DynamicContext dynamicContext,
      @SuppressWarnings("unused") IItem focus) {
    IStringItem input = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    IStringItem pattern = ObjectUtils.requireNonNull(FunctionUtils.asTypeOrNull(arguments.get(1).getFirstItem(true)));

    return execute(input, pattern, IStringItem.valueOf(""));
  }

  @NonNull
  private static ISequence<IBooleanItem> executeThreeArg(
      @SuppressWarnings("unused") @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @SuppressWarnings("unused") @NonNull DynamicContext dynamicContext,
      @SuppressWarnings("unused") IItem focus) {
    IStringItem input = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    IStringItem pattern = ObjectUtils.requireNonNull(FunctionUtils.asTypeOrNull(arguments.get(1).getFirstItem(true)));
    IStringItem flags = ObjectUtils.requireNonNull(FunctionUtils.asTypeOrNull(arguments.get(2).getFirstItem(true)));

    return execute(input, pattern, flags);
  }

  @NonNull
  private static ISequence<IBooleanItem> execute(
      @Nullable IStringItem input,
      @NonNull IStringItem pattern,
      @NonNull IStringItem flags) {
    return input == null
        ? ISequence.empty()
        : ISequence.of(
            IBooleanItem.valueOf(
                fnMatches(input.asString(), pattern.asString(), flags.asString())));
  }

  /**
   * Implements <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-matches">fn:matches</a>.
   *
   * @param input
   *          the string to match against
   * @param pattern
   *          the regular expression to use for matching
   * @param flags
   *          matching options
   * @return {@code true} if the pattern matches or {@code false} otherwise
   */
  public static boolean fnMatches(@NonNull String input, @NonNull String pattern, @NonNull String flags) {
    try {
      return Pattern.compile(pattern, RegexUtil.parseFlags(flags))
          .matcher(input).find();
    } catch (PatternSyntaxException ex) {
      throw new RegularExpressionMetapathException(
          RegularExpressionMetapathException.INVALID_EXPRESSION,
          "Invalid regular expression pattern: '" + pattern + "'",
          ex);
    } catch (IllegalArgumentException ex) {
      throw new RegularExpressionMetapathException(
          RegularExpressionMetapathException.INVALID_FLAG,
          "Invalid regular expression flags: '" + flags + "'",
          ex);
    }
  }

  private FnMatches() {
    // disable construction
  }
}