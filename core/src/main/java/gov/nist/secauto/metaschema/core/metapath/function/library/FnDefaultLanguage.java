/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-default-language">fn:default-language</a>
 * function.
 * <p>
 * This function returns the default language from the static context. If no
 * default language has been configured, "en" (English) is returned as the
 * default value per the XPath 3.1 specification.
 */
public final class FnDefaultLanguage {
  private static final String NAME = "default-language";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(FnDefaultLanguage::execute)
      .build();

  private FnDefaultLanguage() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    return ISequence.of(fnDefaultLanguage(dynamicContext));
  }

  /**
   * Implements <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-default-language">fn:default-language</a>.
   * <p>
   * Returns the default language from the static context. The default language is
   * used by functions like fn:lang() when processing language-sensitive
   * operations.
   *
   * @param dynamicContext
   *          the dynamic evaluation context
   * @return the default language code as a string
   */
  @NonNull
  public static IStringItem fnDefaultLanguage(@NonNull DynamicContext dynamicContext) {
    String language = dynamicContext.getStaticContext().getDefaultLanguage();
    return IStringItem.valueOf(language);
  }
}
