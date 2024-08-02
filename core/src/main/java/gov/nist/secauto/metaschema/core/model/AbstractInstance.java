/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractInstance<P extends IContainer> implements IInstance {
  @NonNull
  private final P parent;

  /**
   * Construct a new instance.
   *
   * @param parent
   *          the parent containing the instance.
   */
  protected AbstractInstance(@NonNull P parent) {
    this.parent = parent;
  }

  @Override
  @NonNull
  public final P getParentContainer() {
    return parent;
  }
}
