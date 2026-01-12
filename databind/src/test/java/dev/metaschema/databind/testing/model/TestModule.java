/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.testing.model;

import java.net.URI;
import java.util.List;
import java.util.Map;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.AbstractBoundModule;
import dev.metaschema.databind.model.IBoundModule;
import dev.metaschema.databind.model.annotations.MetaschemaModule;
import edu.umd.cs.findbugs.annotations.NonNull;

@MetaschemaModule(
    assemblies = {
        RootAssemblyWithFlags.class,
        RootAssemblyWithFields.class
    })
public class TestModule
    extends AbstractBoundModule {
  @NonNull
  private static final URI XML_NAMESPACE
      = ObjectUtils.requireNonNull(URI.create("https://csrc.nist.gov/ns/test/xml"));

  @NonNull
  private static final URI JSON_BASE_URI
      = ObjectUtils.requireNonNull(URI.create("https://csrc.nist.gov/ns/test/json"));

  /**
   * Construct a new test module.
   *
   * @param importedModules
   *          the other modules imported by this module.
   * @param bindingContext
   *          the Metaschema binding context
   */
  public TestModule(
      @NonNull List<? extends IBoundModule> importedModules,
      @NonNull IBindingContext bindingContext) {
    super(importedModules, bindingContext);
  }

  @Override
  public MarkupLine getName() {
    return MarkupLine.fromMarkdown("Test Module");
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public MarkupMultiline getRemarks() {
    return null;
  }

  @Override
  public String getShortName() {
    return "test-metaschema";
  }

  @Override
  public URI getXmlNamespace() {
    return XML_NAMESPACE;
  }

  @Override
  public URI getJsonBaseUri() {
    return JSON_BASE_URI;
  }

  @Override
  public Map<String, String> getNamespaceBindings() {
    return CollectionUtil.emptyMap();
  }
}
