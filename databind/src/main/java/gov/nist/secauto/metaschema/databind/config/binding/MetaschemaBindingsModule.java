/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.config.binding;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.AbstractBoundModule;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaModule;
import java.net.URI;
import java.util.List;

/**
 * A bound module that provides the Metaschema binding configuration model.
 * <p>
 * This module enables parsing of binding configuration files that define how
 * Metaschema modules map to Java packages and classes.
 */
@MetaschemaModule(
    assemblies = MetaschemaBindings.class)
public final class MetaschemaBindingsModule
    extends AbstractBoundModule {
  private static final MarkupLine NAME = MarkupLine.fromMarkdown("Metaschema Binding Configuration");

  private static final String SHORT_NAME = "metaschema-bindings";

  private static final String VERSION = "1.0.0";

  private static final URI XML_NAMESPACE = URI.create("https://csrc.nist.gov/ns/metaschema-binding/1.0");

  private static final URI JSON_BASE_URI = URI.create("https://csrc.nist.gov/ns/metaschema-binding/1.0");

  /**
   * Constructs a new binding configuration module.
   *
   * @param importedModules
   *          the list of modules imported by this module
   * @param bindingContext
   *          the binding context for this module
   */
  public MetaschemaBindingsModule(List<? extends IBoundModule> importedModules,
      IBindingContext bindingContext) {
    super(importedModules, bindingContext);
  }

  @Override
  public MarkupLine getName() {
    return NAME;
  }

  @Override
  public String getShortName() {
    return SHORT_NAME;
  }

  @Override
  public String getVersion() {
    return VERSION;
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
  public MarkupMultiline getRemarks() {
    return null;
  }
}
