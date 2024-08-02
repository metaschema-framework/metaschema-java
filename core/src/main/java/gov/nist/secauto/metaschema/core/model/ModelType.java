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
  ASSEMBLY("assembly"),
  FIELD("field"),
  FLAG("flag"),
  CHOICE("choice"),
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
