/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../metaschema/metaschema-bindings.yaml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.config.binding;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.AbstractBoundModule;
import dev.metaschema.databind.model.IBoundModule;
import dev.metaschema.databind.model.annotations.MetaschemaModule;
import java.net.URI;
import java.util.List;

/**
 * Metaschema Binding Configuration
 * <p>
 * This module defines the binding configuration format used to customize Java
 * code generation from Metaschema modules. It allows specifying package names,
 * class names, interface implementations, base classes, and collection types
 * for generated binding classes.
 * </p>
 */
@MetaschemaModule(
    assemblies = MetaschemaBindings.class,
    remarks = "This module defines the binding configuration format used to customize\n"
        + "Java code generation from Metaschema modules. It allows specifying\n"
        + "package names, class names, interface implementations, base classes,\n"
        + "and collection types for generated binding classes.")
public final class MetaschemaBindingsModule
    extends AbstractBoundModule {
  private static final MarkupLine NAME = MarkupLine.fromMarkdown("Metaschema Binding Configuration");

  private static final String SHORT_NAME = "metaschema-bindings";

  private static final String VERSION = "1.0.0";

  private static final URI XML_NAMESPACE = URI.create("https://csrc.nist.gov/ns/metaschema-binding/1.0");

  private static final URI JSON_BASE_URI = URI.create("https://csrc.nist.gov/ns/metaschema-binding/1.0");

  private static final MarkupMultiline REMARKS
      = MarkupMultiline.fromMarkdown("This module defines the binding configuration format used to customize\n"
          + "Java code generation from Metaschema modules. It allows specifying\n"
          + "package names, class names, interface implementations, base classes,\n"
          + "and collection types for generated binding classes.");

  /**
   * Construct a new module instance.
   *
   * @param importedModules
   *          modules imported by this module
   * @param bindingContext
   *          the binding context to associate with this module
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
    return REMARKS;
  }
}
