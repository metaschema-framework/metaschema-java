/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

// REFACTOR: is this needed/used?
public final class BindingConstants {
  public static final String METASCHEMA_ASSEMBLY_REFERENCE_NAME = "assembly";
  public static final String METASCHEMA_ASSEMBLY_INLINE_DEFINTION_NAME = "define-assembly";
  public static final String METASCHEMA_FIELD_REFERENCE_NAME = "field";
  public static final String METASCHEMA_FIELD_INLINE_DEFINTION_NAME = "define-field";
  public static final String METASCHEMA_CHOICE_NAME = "choice";
  public static final String METASCHEMA_CHOICE_GROUP_NAME = "choice-group";

  private BindingConstants() {
    // disable construction
  }
}
