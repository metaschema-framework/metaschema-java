/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IFieldDefinition;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a field definition bound to Java data.
 * <p>
 * Classes and interfaces implementing this interface can be:
 * <ul>
 * <li>"scalar", meaning they have only a scalar field value, or
 * <li>"complex", meaning they allow flags and are bound to a Java class.
 * </ul>
 *
 * @param <ITEM>
 *          the Java type for associated bound objects
 */
public interface IBoundDefinitionModelField<ITEM>
    extends IFieldDefinition, IBoundDefinitionModel<ITEM> {

  @Override
  default IBoundInstanceModelField<ITEM> getInlineInstance() {
    // never inline
    return null;
  }

  @Override
  @Nullable
  IBoundInstanceFlag getJsonValueKeyFlagInstance();
}
