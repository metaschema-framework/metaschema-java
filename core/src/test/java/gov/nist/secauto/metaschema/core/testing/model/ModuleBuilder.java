/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import static org.mockito.Mockito.doReturn;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.model.mocking.AbstractMockitoFactory;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

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
    doReturn(source.getSource() != null ? source.getSource().toString() : shortName)
        .when(module).getLocationHint();

    // Module QName
    IEnhancedQName qname = IEnhancedQName.of(namespace, shortName);
    doReturn(qname).when(module).getQName();

    // Empty collections for now (will be populated in later commits)
    doReturn(CollectionUtil.emptyList()).when(module).getImportedModules();
    doReturn(null).when(module).getImportedModuleByShortName(org.mockito.ArgumentMatchers.anyString());

    // Name and remarks
    doReturn(null).when(module).getName();
    doReturn(null).when(module).getRemarks();

    return module;
  }
}
