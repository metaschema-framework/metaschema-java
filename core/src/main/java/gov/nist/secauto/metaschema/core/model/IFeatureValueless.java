/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

// REFACTOR: rename to IFeatureValuelessInstance
public interface IFeatureValueless extends IInstanceAbsolute {
  @Override
  default Object getValue(Object parent) {
    // no value
    return null;
  }
}
