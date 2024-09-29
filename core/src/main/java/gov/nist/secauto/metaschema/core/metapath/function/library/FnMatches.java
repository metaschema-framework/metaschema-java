
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
  @NonNull
  static final IFunction SIGNATURE_TWO_ARG = IFunction.builder()
      .name("matches")
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
      .name("matches")
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

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IBooleanItem> executeTwoArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IStringItem input = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    IStringItem pattern = ObjectUtils.requireNonNull(FunctionUtils.asTypeOrNull(arguments.get(1).getFirstItem(true)));

    return execute(input, pattern, IStringItem.valueOf(""));
  }

  @SuppressWarnings("unused")

  @NonNull
  private static ISequence<IBooleanItem> executeThreeArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    IStringItem input = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    IStringItem pattern = ObjectUtils.requireNonNull(FunctionUtils.asTypeOrNull(arguments.get(1).getFirstItem(true)));
    IStringItem flags = ObjectUtils.requireNonNull(FunctionUtils.asTypeOrNull(arguments.get(2).getFirstItem(true)));

    return execute(input, pattern, flags);
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @NonNull
  private static ISequence<IBooleanItem> execute(
      @Nullable IStringItem input,
      @NonNull IStringItem pattern,
      @NonNull IStringItem flags) {
    if (input == null) {
      return ISequence.empty();
    }

    return ISequence.of(fnMatches(input, pattern, flags));
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
   * @return {@link IBooleanItem#TRUE} if the pattern matches or
   *         {@link IBooleanItem#FALSE} otherwise
   */
  public static IBooleanItem fnMatches(
      @NonNull IStringItem input,
      @NonNull IStringItem pattern,
      @NonNull IStringItem flags) {
    return IBooleanItem.valueOf(fnMatches(input.asString(), pattern.asString(), flags.asString()));
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
      throw new RegularExpressionMetapathException(RegularExpressionMetapathException.INVALID_EXPRESSION, ex);
    } catch (IllegalArgumentException ex) {
      throw new RegularExpressionMetapathException(RegularExpressionMetapathException.INVALID_FLAG, ex);
    }
  }

  private FnMatches() {
    // disable construction
  }
}
