/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;

import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-static-base-uri">fn:static-base-uri</a>
 * function.
 */
public final class FnStaticBaseUri {
  private static final String NAME = "static-base-uri";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(IStringItem.type())
          .zeroOrOne()
          .build())
      .returnType(IAnyUriItem.type())
      .returnOne()
      .functionHandler(FnStaticBaseUri::execute)
      .build();

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IAnyUriItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {

    IAnyUriItem uri = fnStaticBaseUri(dynamicContext);
    return ISequence.of(uri);
  }

  private FnStaticBaseUri() {
    // disable construction
  }

  /**
   * Get the static base URI from the static context.
   *
   * @param context
   *          the dynamic context
   * @return the base URI or {@code null} if none was set
   */
  @Nullable
  public static IAnyUriItem fnStaticBaseUri(@NonNull DynamicContext context) {
    URI staticBaseUri = context.getStaticContext().getBaseUri();

    return staticBaseUri == null ? null : IAnyUriItem.valueOf(staticBaseUri);
  }
}
