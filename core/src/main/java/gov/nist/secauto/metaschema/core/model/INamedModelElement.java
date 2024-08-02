/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A marker interface for Metaschema constructs that can be members of a
 * Metaschema module's model that have a name and other identifying
 * characteristics.
 */
public interface INamedModelElement extends IDescribable, IModelElement, IJsonNamed, INamed {
  @Override
  @NonNull
  default String getJsonName() {
    return getEffectiveName();
  }
}
