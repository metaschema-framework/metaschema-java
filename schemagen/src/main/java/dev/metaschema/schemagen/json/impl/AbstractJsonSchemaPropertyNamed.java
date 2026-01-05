/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import dev.metaschema.core.model.IInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A JSON schema for a given Metaschema-based definition instance, that has a
 * distinct property name, which is part of a larger JSON schema.
 *
 * @param <I>
 *          the Java type of the Metaschema definition instance
 */
public abstract class AbstractJsonSchemaPropertyNamed<I extends IInstance>
    extends AbstractJsonSchemaProperty<I>
    implements IJsonSchemaPropertyNamed {
  @NonNull
  private final String name;

  /**
   * Construct a new JSON schema property based on a Metaschema definition
   * instance.
   *
   * @param instance
   *          the Metaschema definition instance
   * @param name
   *          the JSON schema property name
   */
  protected AbstractJsonSchemaPropertyNamed(@NonNull I instance, @NonNull String name) {
    super(instance);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
