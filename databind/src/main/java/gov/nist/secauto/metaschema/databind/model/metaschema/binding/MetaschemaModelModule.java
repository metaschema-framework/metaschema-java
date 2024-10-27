/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import gov.nist.secauto.metaschema.core.MetaschemaConstants;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.AbstractBoundModule;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaModule;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

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
        FlagIndexHasKey.class,
        FlagMatches.class,
        TargetedAllowedValuesConstraint.class,
        TargetedMatchesConstraint.class,
        TargetedExpectConstraint.class,
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
  @NonNull
  private static final MarkupLine NAME = MarkupLine.fromMarkdown("Metaschema Model");

  @NonNull
  private static final String SHORT_NAME = "metaschema-model";

  @NonNull
  private static final String VERSION = "1.0.0-M2";

  @NonNull
  private static final URI XML_NAMESPACE = MetaschemaConstants.METASCHEMA_NAMESPACE_URI;

  @NonNull
  private static final URI JSON_BASE_URI = MetaschemaConstants.METASCHEMA_NAMESPACE_URI;

  @NonNull
  private static final Map<String, String> NAMESPACE_BINDINGS;

  static {
    @SuppressWarnings("PMD.UseConcurrentHashMap") Map<String, String> bindings = new LinkedHashMap<>();

    NAMESPACE_BINDINGS = bindings;
  }

  public MetaschemaModelModule(
      @NonNull List<? extends IBoundModule> importedModules,
      @NonNull IBindingContext bindingContext) {
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

  @Override
  public Map<String, String> getNamespaceBindings() {
    return NAMESPACE_BINDINGS;
  }
}
