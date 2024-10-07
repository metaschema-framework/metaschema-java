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
import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.UriFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class FnResolveUri {
  private static final String NAME = "resolve-uri";
  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("relative")
          .type(IStringItem.class)
          .zeroOrOne()
          .build())
      .returnType(IAnyUriItem.class)
      .returnZeroOrOne()
      .functionHandler(FnResolveUri::executeOneArg)
      .build();

  @NonNull
  static final IFunction SIGNATURE_TWO_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("relative")
          .type(IStringItem.class)
          .zeroOrOne()
          .build())
      .argument(IArgument.builder()
          .name("base")
          .type(IStringItem.class)
          .one()
          .build())
      .returnType(IAnyUriItem.class)
      .returnZeroOrOne()
      .functionHandler(FnResolveUri::executeTwoArg)
      .build();

  private FnResolveUri() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyUriItem> executeOneArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    ISequence<? extends IStringItem> relativeSequence
        = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));
    if (relativeSequence.isEmpty()) {
      return ISequence.empty(); // NOPMD - readability
    }

    IStringItem relativeString = relativeSequence.getFirstItem(true);
    IAnyUriItem resolvedUri = null;
    if (relativeString != null) {
      resolvedUri = fnResolveUri(relativeString, null, dynamicContext);
    }
    return ISequence.of(resolvedUri);
  }

  /**
   * Implements the two argument version of the XPath 3.1 function <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-resolve-uri">resolve-uri</a>.
   *
   * @param function
   *          the function definition
   * @param arguments
   *          a list of sequence arguments with an expected size of 2
   * @param dynamicContext
   *          the evaluation context
   * @param focus
   *          the current focus item
   * @return a sequence containing the resolved URI or and empty sequence if
   *         either the base or relative URI is {@code null}
   */
  @SuppressWarnings("PMD.UnusedPrivateMethod") // used in lambda
  @NonNull
  private static ISequence<IAnyUriItem> executeTwoArg(
      @NonNull IFunction function, // NOPMD - ok
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext, // NOPMD - ok
      IItem focus) { // NOPMD - ok

    /* there will always be two arguments */
    assert arguments.size() == 2;

    ISequence<? extends IStringItem> relativeSequence = FunctionUtils.asType(
        ObjectUtils.requireNonNull(arguments.get(0)));
    if (relativeSequence.isEmpty()) {
      return ISequence.empty(); // NOPMD - readability
    }

    ISequence<? extends IStringItem> baseSequence = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(1)));
    IStringItem baseString = baseSequence.getFirstItem(true);

    if (baseString == null) {
      throw new InvalidArgumentFunctionException(
          InvalidArgumentFunctionException.INVALID_ARGUMENT_TO_RESOLVE_URI,
          "Invalid argument to fn:resolve-uri().");
    }
    IAnyUriItem baseUri = IAnyUriItem.cast(baseString);

    IStringItem relativeString = relativeSequence.getFirstItem(true);

    IAnyUriItem resolvedUri = null;
    if (relativeString != null) {
      resolvedUri = fnResolveUri(relativeString, baseUri, dynamicContext);
    }
    return ISequence.of(resolvedUri);
  }

  /**
   * Resolve the {@code relative} URI against the provided {@code base} URI.
   *
   * @param relative
   *          the relative URI to resolve
   * @param base
   *          the base URI to resolve against
   * @param dynamicContext
   *          the evaluation context used to get the static base URI if needed
   * @return the resolved URI or {@code null} if the {@code relative} URI in
   *         {@code null}
   */
  @Nullable
  public static IAnyUriItem fnResolveUri(
      @NonNull IStringItem relative,
      @Nullable IAnyUriItem base,
      @NonNull DynamicContext dynamicContext) {
    return fnResolveUri(IAnyUriItem.cast(relative), base, dynamicContext);
  }

  /**
   * Resolve the {@code relative} URI against the provided {@code base} URI.
   *
   * @param relative
   *          the relative URI to resolve
   * @param base
   *          the base URI to resolve against
   * @param dynamicContext
   *          the evaluation context used to get the static base URI if needed
   * @return the resolved URI or {@code null} if the {@code relative} URI in
   *         {@code null}
   */
  @NonNull
  public static IAnyUriItem fnResolveUri(
      @NonNull IAnyUriItem relative,
      @Nullable IAnyUriItem base,
      @NonNull DynamicContext dynamicContext) {

    IAnyUriItem baseUri = base;
    if (baseUri == null) {
      baseUri = FnStaticBaseUri.fnStaticBaseUri(dynamicContext);
      if (baseUri == null) {
        throw new UriFunctionException(UriFunctionException.BASE_URI_NOT_DEFINED_IN_STATIC_CONTEXT,
            "The base-uri is not defined in the static context");
      }
    }

    return baseUri.resolve(relative);
  }
}
