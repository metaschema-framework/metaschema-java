/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IFlagDefinition;

/**
 * Represents a flag definition/instance bound to Java field.
 */
public interface IBoundDefinitionFlag
    extends IFlagDefinition, IBoundModelObject<Object>, IBoundDefinition {
  // no additional methods
}
