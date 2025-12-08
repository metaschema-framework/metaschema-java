/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.model.mocking.AbstractMockitoFactory;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A builder for creating mock {@link IModule} instances for testing purposes.
 */
final class ModuleBuilder
    extends AbstractMockitoFactory
    implements IModuleBuilder {

  private String namespace;
  private String shortName;
  private String version;
  private ISource source;
  private final List<IFlagBuilder> flagBuilders = new ArrayList<>();
  private final List<IFieldBuilder> fieldBuilders = new ArrayList<>();
  private final List<IAssemblyBuilder> assemblyBuilders = new ArrayList<>();

  ModuleBuilder() {
    // package-private constructor
  }

  @Override
  @NonNull
  public IModuleBuilder reset() {
    this.namespace = null;
    this.shortName = null;
    this.version = null;
    this.source = null;
    this.flagBuilders.clear();
    this.fieldBuilders.clear();
    this.assemblyBuilders.clear();
    return this;
  }

  @Override
  @NonNull
  public IModuleBuilder namespace(@NonNull String namespace) {
    this.namespace = namespace;
    return this;
  }

  @Override
  @NonNull
  public IModuleBuilder shortName(@NonNull String shortName) {
    this.shortName = shortName;
    return this;
  }

  @Override
  @NonNull
  public IModuleBuilder version(@NonNull String version) {
    this.version = version;
    return this;
  }

  @Override
  @NonNull
  public IModuleBuilder source(@NonNull ISource source) {
    this.source = source;
    return this;
  }

  @Override
  @NonNull
  public IModuleBuilder flag(@Nullable IFlagBuilder flag) {
    if (flag != null) {
      this.flagBuilders.add(flag);
    }
    return this;
  }

  @Override
  @NonNull
  public IModuleBuilder field(@Nullable IFieldBuilder field) {
    if (field != null) {
      this.fieldBuilders.add(field);
    }
    return this;
  }

  @Override
  @NonNull
  public IModuleBuilder assembly(@Nullable IAssemblyBuilder assembly) {
    if (assembly != null) {
      this.assemblyBuilders.add(assembly);
    }
    return this;
  }

  /**
   * Validate that required fields are set.
   */
  private void validate() {
    ObjectUtils.requireNonNull(namespace, "namespace");
    ObjectUtils.requireNonNull(shortName, "shortName");
    ObjectUtils.requireNonNull(version, "version");
    ObjectUtils.requireNonNull(source, "source");
  }

  @Override
  @NonNull
  public IModule toModule() {
    validate();

    IModule module = mock(IModule.class);

    // Basic metadata
    URI namespaceUri = URI.create(ObjectUtils.notNull(namespace));
    doReturn(namespaceUri).when(module).getXmlNamespace();
    doReturn(namespaceUri).when(module).getJsonBaseUri();
    doReturn(shortName).when(module).getShortName();
    doReturn(version).when(module).getVersion();
    doReturn(source).when(module).getSource();

    // Location information
    doReturn(source.getSource()).when(module).getLocation();
    doReturn(source.getSource() != null ? ObjectUtils.notNull(source.getSource()).toString() : shortName)
        .when(module).getLocationHint();

    // Module QName
    IEnhancedQName qname = IEnhancedQName.of(namespace, shortName);
    doReturn(qname).when(module).getQName();

    // Imported modules
    doReturn(CollectionUtil.emptyList()).when(module).getImportedModules();
    doReturn(null).when(module).getImportedModuleByShortName(org.mockito.ArgumentMatchers.anyString());

    // Name and remarks
    doReturn(null).when(module).getName();
    doReturn(null).when(module).getRemarks();

    // Build definitions from accumulated builders
    buildDefinitions(module);

    return module;
  }

  /**
   * Build all accumulated definitions and wire them to the module.
   *
   * @param module
   *          the module to wire definitions to
   */
  private void buildDefinitions(@NonNull IModule module) {
    String moduleNamespace = ObjectUtils.notNull(namespace);
    ISource moduleSource = ObjectUtils.notNull(source);

    // Build flag definitions
    List<IFlagDefinition> flagDefs = new ArrayList<>();
    for (IFlagBuilder builder : flagBuilders) {
      IFlagDefinition def = builder
          .namespace(moduleNamespace)
          .source(moduleSource)
          .toDefinition(module);
      flagDefs.add(def);
    }
    doReturn(CollectionUtil.unmodifiableList(flagDefs)).when(module).getFlagDefinitions();
    // Set up lookup by QName - extract qname first to avoid nested stubbing issues
    for (IFlagDefinition def : flagDefs) {
      IEnhancedQName qname = def.getDefinitionQName();
      doReturn(def).when(module).getFlagDefinitionByName(eq(qname));
    }

    // Build field definitions
    List<IFieldDefinition> fieldDefs = new ArrayList<>();
    for (IFieldBuilder builder : fieldBuilders) {
      IFieldDefinition def = builder
          .namespace(moduleNamespace)
          .source(moduleSource)
          .toDefinition(module);
      fieldDefs.add(def);
    }
    doReturn(CollectionUtil.unmodifiableList(fieldDefs)).when(module).getFieldDefinitions();
    // Set up lookup by index position - extract index first to avoid nested
    // stubbing issues
    for (IFieldDefinition def : fieldDefs) {
      Integer index = def.getDefinitionQName().getIndexPosition();
      doReturn(def).when(module).getFieldDefinitionByName(eq(index));
    }

    // Build assembly definitions
    List<IAssemblyDefinition> assemblyDefs = new ArrayList<>();
    for (IAssemblyBuilder builder : assemblyBuilders) {
      IAssemblyDefinition def = builder
          .namespace(moduleNamespace)
          .source(moduleSource)
          .toDefinition(module);
      assemblyDefs.add(def);
    }
    doReturn(CollectionUtil.unmodifiableList(assemblyDefs)).when(module).getAssemblyDefinitions();
    // Set up lookup by index position - extract index first to avoid nested
    // stubbing issues
    for (IAssemblyDefinition def : assemblyDefs) {
      Integer index = def.getDefinitionQName().getIndexPosition();
      doReturn(def).when(module).getAssemblyDefinitionByName(eq(index));
    }
  }
}
