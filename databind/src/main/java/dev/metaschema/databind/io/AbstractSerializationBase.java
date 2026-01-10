/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import dev.metaschema.core.configuration.DefaultConfiguration;
import dev.metaschema.core.configuration.IConfigurationFeature;
import dev.metaschema.core.configuration.IMutableConfiguration;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Abstract base class for serializers and deserializers that provides common
 * configuration management functionality.
 * <p>
 * This class maintains a reference to the bound assembly definition and manages
 * configuration features that control serialization/deserialization behavior.
 *
 * @param <T>
 *          the type of configuration feature this class manages
 */
abstract class AbstractSerializationBase<T extends IConfigurationFeature<?>>
    implements IMutableConfiguration<T> {
  @NonNull
  private final IBoundDefinitionModelAssembly definition;
  @NonNull
  private final DefaultConfiguration<T> configuration;

  /**
   * Construct a new serialization base with the provided definition.
   *
   * @param definition
   *          the bound assembly definition describing the data structure
   */
  protected AbstractSerializationBase(@NonNull IBoundDefinitionModelAssembly definition) {
    this.definition = definition;
    this.configuration = new DefaultConfiguration<>();
  }

  /**
   * Retrieve the binding context associated with the serializer.
   *
   * @return the binding context
   */
  @NonNull
  protected IBindingContext getBindingContext() {
    return getDefinition().getBindingContext();
  }

  /**
   * Retrieve the bound class information associated with the assembly that the
   * serializer/deserializer will write/read data from.
   *
   * @return the class binding for the Module assembly
   */
  @NonNull
  protected IBoundDefinitionModelAssembly getDefinition() {
    return definition;
  }

  /**
   * Callback method invoked when the configuration has been changed.
   * <p>
   * Subclasses can override this method to handle configuration changes, such as
   * resetting cached factory instances.
   *
   * @param config
   *          the updated configuration
   */
  @SuppressWarnings("unused")
  protected void configurationChanged(@NonNull IMutableConfiguration<T> config) {
    // do nothing by default. Methods can override this to deal with factory caching
  }

  /**
   * Get the current configuration of the serializer/deserializer.
   *
   * @return the configuration
   */
  @NonNull
  protected IMutableConfiguration<T> getConfiguration() {
    return configuration;
  }

  @Override
  public boolean isFeatureEnabled(T feature) {
    return configuration.isFeatureEnabled(feature);
  }

  @Override
  public Map<T, Object> getFeatureValues() {
    return configuration.getFeatureValues();
  }

  @Override
  public <V> V get(T feature) {
    return configuration.get(feature);
  }

}
