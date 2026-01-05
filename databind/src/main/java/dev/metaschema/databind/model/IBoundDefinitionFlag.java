/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import dev.metaschema.core.model.IFlagDefinition;

/**
 * Represents a flag definition/instance bound to Java field.
 */
public interface IBoundDefinitionFlag
    extends IFlagDefinition, IBoundModelObject<Object>, IBoundDefinition {
  // no additional methods
}
