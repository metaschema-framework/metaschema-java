/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.json;

import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.metaschema.core.model.IAnyContent;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * JSON/YAML-specific implementation of {@link IAnyContent} that stores captured
 * unmodeled content as a Jackson {@link ObjectNode}.
 */
public class JsonAnyContent implements IAnyContent {
  @NonNull
  private final ObjectNode properties;

  /**
   * Construct a new instance with the provided captured properties.
   *
   * @param properties
   *          the captured JSON properties, must not be null
   */
  public JsonAnyContent(@NonNull ObjectNode properties) {
    this.properties = properties;
  }

  @Override
  public boolean isEmpty() {
    return properties.isEmpty();
  }

  /**
   * Get the captured JSON properties.
   *
   * @return the captured ObjectNode
   */
  @NonNull
  public ObjectNode getProperties() {
    return properties;
  }
}
