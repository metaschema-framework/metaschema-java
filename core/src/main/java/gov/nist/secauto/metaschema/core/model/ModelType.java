/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An enumeration that identifies the type of a Metaschema construct.
 */
public enum ModelType {
  /**
   * Represents an assembly definition.
   */
  ASSEMBLY("assembly"),
  /**
   * Represents a field definition.
   */
  FIELD("field"),
  /**
   * Represents a flag definition.
   */
  FLAG("flag"),
  /**
   * Represents a choice between multiple definitions.
   */
  CHOICE("choice"),
  /**
   * Represents a grouped choice construct.
   */
  CHOICE_GROUP("choice-group");

  private final String name;

  ModelType(@NonNull String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
