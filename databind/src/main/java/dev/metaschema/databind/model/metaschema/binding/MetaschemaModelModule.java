/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.model.metaschema.binding;

import java.net.URI;
import java.util.List;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.AbstractBoundModule;
import dev.metaschema.databind.model.IBoundModule;
import dev.metaschema.databind.model.annotations.MetaschemaModule;

/**
 * Metaschema Model
 */
@MetaschemaModule(
    fields = {
        UseName.class,
        Remarks.class,
        ConstraintValueEnum.class
    },
    assemblies = {
        METASCHEMA.class,
        MetapathNamespace.class,
        InlineDefineAssembly.class,
        InlineDefineField.class,
        InlineDefineFlag.class,
        Any.class,
        AssemblyReference.class,
        FieldReference.class,
        FlagReference.class,
        AssemblyModel.class,
        JsonValueKeyFlag.class,
        GroupingAs.class,
        Example.class,
        Property.class,
        JsonKey.class,
        AssemblyConstraints.class,
        FieldConstraints.class,
        FlagConstraints.class,
        ConstraintLetExpression.class,
        FlagAllowedValues.class,
        FlagExpect.class,
        FlagReport.class,
        FlagIndexHasKey.class,
        FlagMatches.class,
        TargetedAllowedValuesConstraint.class,
        TargetedMatchesConstraint.class,
        TargetedExpectConstraint.class,
        TargetedReportConstraint.class,
        TargetedIndexHasKeyConstraint.class,
        KeyConstraintField.class,
        TargetedIsUniqueConstraint.class,
        TargetedIndexConstraint.class,
        TargetedHasCardinalityConstraint.class,
        MetaschemaModuleConstraints.class,
        MetaschemaMetaConstraints.class,
        MetaschemaMetapath.class,
        MetapathContext.class
    })
public final class MetaschemaModelModule
    extends AbstractBoundModule {
  private static final MarkupLine NAME = MarkupLine.fromMarkdown("Metaschema Model");

  private static final String SHORT_NAME = "metaschema-model";

  private static final String VERSION = "1.0.0-rc.1";

  private static final URI XML_NAMESPACE = URI.create("http://csrc.nist.gov/ns/oscal/metaschema/1.0");

  private static final URI JSON_BASE_URI = URI.create("http://csrc.nist.gov/ns/oscal/metaschema/1.0");

  /**
   * Construct a new module instance.
   *
   * @param importedModules
   *          modules imported by this module
   * @param bindingContext
   *          the binding context to associate with this module
   */
  public MetaschemaModelModule(List<? extends IBoundModule> importedModules,
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
