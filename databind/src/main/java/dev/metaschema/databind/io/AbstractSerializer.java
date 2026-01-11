/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import dev.metaschema.core.configuration.IConfiguration;
import dev.metaschema.core.configuration.IMutableConfiguration;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The base class of all format-specific serializers.
 *
 * @param <CLASS>
 *          the bound class to serialize from
 */
public abstract class AbstractSerializer<CLASS extends IBoundObject>
    extends AbstractSerializationBase<SerializationFeature<?>>
    implements ISerializer<CLASS> {

  /**
   * Construct a new serializer.
   *
   * @param definition
   *          the bound class information for the Java type this serializer is
   *          operating on
   */
  public AbstractSerializer(@NonNull IBoundDefinitionModelAssembly definition) {
    super(definition);
  }

  @Override
  public ISerializer<CLASS> enableFeature(SerializationFeature<?> feature) {
    return set(feature, true);
  }

  @Override
  public ISerializer<CLASS> disableFeature(SerializationFeature<?> feature) {
    return set(feature, false);
  }

  @Override
  public ISerializer<CLASS> applyConfiguration(
      @NonNull IConfiguration<SerializationFeature<?>> other) {
    IMutableConfiguration<SerializationFeature<?>> config = getConfiguration();
    config.applyConfiguration(other);
    configurationChanged(config);
    return this;
  }

  @Override
  public ISerializer<CLASS> set(SerializationFeature<?> feature, Object value) {
    IMutableConfiguration<SerializationFeature<?>> config = getConfiguration();
    config.set(feature, value);
    configurationChanged(config);
    return this;
  }
}
