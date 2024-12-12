/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a Metaschema module-based model builder for producing assembly
 * definitions and instances.
 */
public interface IAssemblyBuilder extends IModelBuilder<IAssemblyBuilder> {

  /**
   * Create a new builder using the provided mocking context.
   *
   * @return the new builder
   */
  @NonNull
  static IAssemblyBuilder builder() {
    return new AssemblyBuilder().reset();
  }

  /**
   * Apply the provided root namespace for use by this builder.
   *
   * @param name
   *          the namespace to use
   * @return this builder
   */
  @NonNull
  IAssemblyBuilder rootNamespace(@NonNull String name);

  /**
   * Apply the provided root namespace for use by this builder.
   *
   * @param name
   *          the namespace to use
   * @return this builder
   */
  @NonNull
  default IAssemblyBuilder rootNamespace(@NonNull URI name) {
    return rootNamespace(ObjectUtils.notNull(name.toASCIIString()));
  }

  /**
   * Apply the provided root name for use by this builder.
   *
   * @param name
   *          the name to use
   * @return this builder
   */
  @NonNull
  IAssemblyBuilder rootName(@NonNull String name);

  /**
   * Apply the provided root qualified name for use by this builder.
   *
   * @param qname
   *          the qualified name to use
   * @return this builder
   */
  @NonNull
  IAssemblyBuilder rootQName(@NonNull IEnhancedQName qname);

  /**
   * Use the provided model instances for built fields.
   *
   * @param modelInstances
   *          the model instances to use
   * @return this builder
   */
  IAssemblyBuilder modelInstances(@Nullable List<? extends IModelBuilder<?>> modelInstances);

  @Override
  @NonNull
  IAssemblyInstanceAbsolute toInstance(@NonNull IAssemblyDefinition parent);

  /**
   * Build a mocked assembly instance, using the provided definition, as a child
   * of the provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @param definition
   *          the definition to base the instance on
   * @return the new mocked instance
   */
  @NonNull
  IAssemblyInstanceAbsolute toInstance(@NonNull IAssemblyDefinition parent, @NonNull IAssemblyDefinition definition);

  /**
   * Build a mocked assembly definition.
   *
   * @return the new mocked definition
   */
  @NonNull
  IAssemblyDefinition toDefinition();
}
