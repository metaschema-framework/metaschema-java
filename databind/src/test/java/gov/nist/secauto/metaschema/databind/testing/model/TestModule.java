/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.testing.model;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.AbstractBoundModule;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaModule;

import java.net.URI;
import java.util.List;
import java.util.Map;

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
