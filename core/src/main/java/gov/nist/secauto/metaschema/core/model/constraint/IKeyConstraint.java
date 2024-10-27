/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A common interface used for constraints oriented around key-based indexes.
 */
public interface IKeyConstraint extends IConfigurableMessageConstraint {
  /**
   * Retrieve the list of keys to use in creating and looking up an entry in a
   * given index.
   *
   * @return one or more keys
   */
  @NonNull
  List<? extends IKeyField> getKeyFields();
}
