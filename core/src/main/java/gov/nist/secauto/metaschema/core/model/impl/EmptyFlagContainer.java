/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.impl;

import gov.nist.secauto.metaschema.core.model.IContainerFlagSupport;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class EmptyFlagContainer<FI extends IFlagInstance> implements IContainerFlagSupport<FI> {
  @NonNull
  public static final EmptyFlagContainer<?> EMPTY = new EmptyFlagContainer<>();

  @Override
  public Map<QName, FI> getFlagInstanceMap() {
    return CollectionUtil.emptyMap();
  }

  @Override
  public FI getJsonKeyFlagInstance() {
    // no JSON key
    return null;
  }
}
