/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import java.math.BigDecimal;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDecimalMapKey extends IMapKey {
  /**
   * Get this key's value as a decimal.
   *
   * @return the equivalent decimal value
   */
  @NonNull
  BigDecimal asDecimal();
}
