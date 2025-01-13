/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.metapath.impl.AbstractStringMapKey;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for untyped atomic items.
 *
 * @param <TYPE>
 *          the Java type of the wrapped value
 */
public abstract class AbstractUntypedAtomicItem<TYPE>
    extends AbstractAnyAtomicItem<TYPE>
    implements IUntypedAtomicItem {

  /**
   * Construct a new untyped atomic valued item.
   *
   * @param value
   *          the value
   */
  protected AbstractUntypedAtomicItem(@NonNull TYPE value) {
    super(value);
  }

  @Override
  public IMapKey asMapKey() {
    return new MapKey();
  }

  private final class MapKey
      extends AbstractStringMapKey {

    @Override
    public IUntypedAtomicItem getKey() {
      return AbstractUntypedAtomicItem.this;
    }

    @Override
    public String asString() {
      return getKey().asString();
    }
  }
}
