/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

public abstract class AbstractMapKey implements IMapKey {
  @Override
  public String toString() {
    return getKey().toSignature();
  }
}
