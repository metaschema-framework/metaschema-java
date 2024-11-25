/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.impl;

import gov.nist.secauto.metaschema.core.model.IContainerFlagSupport;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents an flag container with no flags.
 *
 * @param <FI>
 *          the Java type of the flags supported by the container
 */
public final class EmptyFlagContainer<FI extends IFlagInstance> implements IContainerFlagSupport<FI> {
  /**
   * The singleton instance.
   */
  @NonNull
  public static final EmptyFlagContainer<?> EMPTY = new EmptyFlagContainer<>();

  private EmptyFlagContainer() {
    // diable construction
  }

  @Override
  public Map<Integer, FI> getFlagInstanceMap() {
    return CollectionUtil.emptyMap();
  }

  @Override
  public FI getJsonKeyFlagInstance() {
    // no JSON key
    return null;
  }
}
