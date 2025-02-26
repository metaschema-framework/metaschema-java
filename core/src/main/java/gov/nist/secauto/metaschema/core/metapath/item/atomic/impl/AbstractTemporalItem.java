/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.AbstractAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITemporalItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for all items derived from {@link ITemporalItem}.
 *
 * @param <TYPE>
 *          the Java type of the wrapped value
 */
public abstract class AbstractTemporalItem<TYPE>
    extends AbstractAnyAtomicItem<TYPE>
    implements ITemporalItem {
  /**
   * Construct a new temporal item.
   *
   * @param value
   *          the wrapped value
   */
  protected AbstractTemporalItem(@NonNull TYPE value) {
    super(value);
  }

  @Override
  protected String getValueSignature() {
    return "'" + asString() + "'";
  }
}
