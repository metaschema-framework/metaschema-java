/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.util.Locale;

class FnDefaultLanguageTest {

  @Test
  void testDefaultLanguageReturnsSystemLocaleByDefault() {
    DynamicContext context = new DynamicContext();
    IStringItem result = ObjectUtils.notNull(IMetapathExpression.compile("fn:default-language()")
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, context));

    String expectedLanguage = Locale.getDefault().getLanguage();
    assertEquals(expectedLanguage, result.asString(),
        "The default language should match the JVM's default locale language when not configured.");
  }

  @Test
  void testDefaultLanguageReturnsConfiguredValue() {
    StaticContext staticContext = StaticContext.builder()
        .defaultLanguage("fr")
        .build();
    DynamicContext context = new DynamicContext(staticContext);

    IStringItem result = ObjectUtils.notNull(IMetapathExpression.compile("fn:default-language()")
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, context));

    assertEquals("fr", result.asString(),
        "The default language should be 'fr' as configured.");
  }
}
