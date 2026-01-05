/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.function.impl;

import dev.metaschema.core.metapath.impl.AbstractMapKey;
import dev.metaschema.core.metapath.item.function.IMapKey;
import dev.metaschema.core.metapath.item.function.IStringMapKey;

/**
 * An implementation of a {@link IMapKey} that uses a string-based value.
 */
public abstract class AbstractStringMapKey
    extends AbstractMapKey
    implements IStringMapKey {

  @Override
  protected int generateHashCode() {
    return asString().hashCode();
  }
}
