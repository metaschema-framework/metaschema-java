/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import java.util.List;
import java.util.Set;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.IArgument;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.item.atomic.ITimeItem;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-format-time">fn:format-time</a>
 * functions.
 *
 * @see <a href=
 *      "https://www.w3.org/TR/xpath-functions-31/#func-format-time">XPath 3.1
 *      fn:format-time</a>
 */
public final class FnFormatTime {
  private static final String NAME = "format-time";

  /**
   * The set of component specifiers allowed for time values, which excludes
   * date-only markers (Y, M, D, d, F, W, w) and era (E), since era is defined as
   * "a baseline for the numbering of years" and is not available in
   * {@code xs:time} values.
   */
  @NonNull
  static final Set<Character> TIME_MARKERS = Set.of(
      'H', 'h', 'P', 'm', 's', 'f',
      'Z', 'z', 'C');

  @NonNull
  static final IFunction SIGNATURE_TWO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("value")
          .type(ITimeItem.type())
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("picture")
          .type(IStringItem.type())
          .one()
          .build())
      .returnType(IStringItem.type())
      .returnZeroOrOne()
      .functionHandler(FnFormatTime::executeTwoArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_FIVE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("value")
          .type(ITimeItem.type())
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("picture")
          .type(IStringItem.type())
          .one()
          .build())
      .argument(IArgument.builder()
          .name("language")
          .type(IStringItem.type())
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("calendar")
          .type(IStringItem.type())
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("place")
          .type(IStringItem.type())
          .zeroOrOne()
          .build())
      .returnType(IStringItem.type())
      .returnZeroOrOne()
      .functionHandler(FnFormatTime::executeFiveArg)
      .build();

  private FnFormatTime() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeTwoArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ITimeItem value = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    if (value == null) {
      return ISequence.empty();
    }

    IStringItem picture = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(1).getFirstItem(true)));

    String lang = dynamicContext.getStaticContext().getDefaultLanguage();

    return ISequence.of(IStringItem.valueOf(
        formatTime(value, picture.asString(), lang, null, null)));
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeFiveArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ITimeItem value = FunctionUtils.asTypeOrNull(arguments.get(0).getFirstItem(true));
    if (value == null) {
      return ISequence.empty();
    }

    IStringItem picture = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(1).getFirstItem(true)));
    IStringItem language = FunctionUtils.asTypeOrNull(arguments.get(2).getFirstItem(true));
    IStringItem calendar = FunctionUtils.asTypeOrNull(arguments.get(3).getFirstItem(true));
    IStringItem place = FunctionUtils.asTypeOrNull(arguments.get(4).getFirstItem(true));

    return ISequence.of(IStringItem.valueOf(
        formatTime(
            value,
            picture.asString(),
            language == null ? dynamicContext.getStaticContext().getDefaultLanguage() : language.asString(),
            calendar == null ? null : calendar.asString(),
            place == null ? null : place.asString())));
  }

  /**
   * Format a time value using a picture string per the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-format-time">fn:format-time</a>
   * specification.
   *
   * @param value
   *          the time value to format
   * @param picture
   *          the picture string
   * @param language
   *          the language, or {@code null}
   * @param calendar
   *          the calendar, or {@code null}
   * @param place
   *          the place, or {@code null}
   * @return the formatted string
   */
  @NonNull
  public static String formatTime(
      @NonNull ITimeItem value,
      @NonNull String picture,
      @Nullable String language,
      @Nullable String calendar,
      @Nullable String place) {
    return DateTimeFormatUtil.formatDateTime(value, picture, language, calendar, place, TIME_MARKERS);
  }
}
