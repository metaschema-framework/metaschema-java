/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.testsupport.builder;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.util.ObjectUtils;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A builder for creating mock {@link IModule} instances for testing purposes.
 */
public interface IModuleBuilder {

  /**
   * Create a new builder.
   *
   * @return the new builder
   */
  @NonNull
  static IModuleBuilder builder() {
    return new ModuleBuilder().reset();
  }

  /**
   * Reset the builder to its default state.
   *
   * @return this builder
   */
  @NonNull
  IModuleBuilder reset();

  /**
   * Set the XML namespace for the module.
   *
   * @param namespace
   *          the namespace URI string
   * @return this builder
   */
  @NonNull
  IModuleBuilder namespace(@NonNull String namespace);

  /**
   * Set the XML namespace for the module.
   *
   * @param namespace
   *          the namespace URI
   * @return this builder
   */
  @NonNull
  default IModuleBuilder namespace(@NonNull URI namespace) {
    return namespace(ObjectUtils.notNull(namespace.toASCIIString()));
  }

  /**
   * Set the short name for the module.
   *
   * @param shortName
   *          the short name
   * @return this builder
   */
  @NonNull
  IModuleBuilder shortName(@NonNull String shortName);

  /**
   * Set the version for the module.
   *
   * @param version
   *          the version string
   * @return this builder
   */
  @NonNull
  IModuleBuilder version(@NonNull String version);

  /**
   * Set the source information for the module.
   *
   * @param source
   *          the source information
   * @return this builder
   */
  @NonNull
  IModuleBuilder source(@NonNull ISource source);

  /**
   * Add a flag definition to the module.
   *
   * @param flag
   *          the flag builder to add
   * @return this builder
   */
  @NonNull
  IModuleBuilder flag(@Nullable IFlagBuilder flag);

  /**
   * Add a field definition to the module.
   *
   * @param field
   *          the field builder to add
   * @return this builder
   */
  @NonNull
  IModuleBuilder field(@Nullable IFieldBuilder field);

  /**
   * Add an assembly definition to the module.
   *
   * @param assembly
   *          the assembly builder to add
   * @return this builder
   */
  @NonNull
  IModuleBuilder assembly(@Nullable IAssemblyBuilder assembly);

  /**
   * Build the mock module.
   *
   * @return the new mock module
   */
  @NonNull
  IModule toModule();
}
