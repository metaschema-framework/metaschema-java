/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

/**
 * Represents an instance that has no value.
 * <p>
 * This interface marks instances that do not produce a value when queried,
 * always returning {@code null} from {@link #getValue(Object)}.
 */
// REFACTOR: rename to IFeatureValuelessInstance
public interface IFeatureValueless extends IInstanceAbsolute {
  @Override
  default Object getValue(Object parent) {
    // no value
    return null;
  }
}
