/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testsupport.builder;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Marker interface for model references that need to be resolved lazily during
 * module construction. This allows building recursive structures where an
 * assembly can reference itself or other assemblies that haven't been built
 * yet.
 */
public interface IModelReference {
  /**
   * Get the name of the referenced definition.
   *
   * @return the local name of the referenced assembly or field
   */
  @NonNull
  String getReferencedName();
}
