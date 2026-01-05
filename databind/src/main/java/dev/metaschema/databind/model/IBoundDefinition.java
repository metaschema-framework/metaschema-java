/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import dev.metaschema.core.model.IDefinition;

/**
 * A Metaschema definition (flag, field, or assembly) bound to Java data.
 * <p>
 * This interface combines the bound model element capabilities with the core
 * Metaschema definition interface.
 */
public interface IBoundDefinition extends IBoundModelElement, IDefinition {
  // no additional methods
}
