/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.model.IModuleLoader;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext.IModuleLoaderStrategy;
import gov.nist.secauto.metaschema.databind.SimpleModuleLoaderStrategy;
import gov.nist.secauto.metaschema.databind.codegen.DefaultModuleBindingGenerator;
import gov.nist.secauto.metaschema.databind.io.DeserializationFeature;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports loading a Metaschema module from a specified resource.
 * <p>
 * Metaschema modules loaded this way are automatically registered with the
 * {@link IBindingContext}.
 * <p>
 * Use of this Metaschema module loader requires that the associated binding
 * context is initialized using a {@link IModuleLoaderStrategy} that supports
 * dynamic bound module loading. This can be accomplished using the
 * {@link SimpleModuleLoaderStrategy} initialized using the
 * {@link DefaultModuleBindingGenerator}.
 *
 */
public interface IBindingModuleLoader
    extends IModuleLoader<IBindingMetaschemaModule>, IMutableConfiguration<DeserializationFeature<?>> {
  /**
   * Get the associated binding context.
   *
   * @return the binding context
   */
  @NonNull
  IBindingContext getBindingContext();

  /**
   * Enable entity resolution within a loaded Metaschema module resource.
   */
  default void allowEntityResolution() {
    enableFeature(DeserializationFeature.DESERIALIZE_XML_ALLOW_ENTITY_RESOLUTION);
  }
}
