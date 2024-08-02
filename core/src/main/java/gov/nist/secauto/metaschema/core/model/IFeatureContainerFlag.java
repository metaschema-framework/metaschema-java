/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a definition that may contain flags.
 *
 * @param <FI>
 *          the flag instance Java type
 */
public interface IFeatureContainerFlag<FI extends IFlagInstance> extends IModelDefinition {
  /**
   * Lazy initialize the flag instances associated with this definition.
   *
   * @return the flag container
   */
  @NonNull
  IContainerFlagSupport<FI> getFlagContainer();

  @Override
  @Nullable
  default FI getFlagInstanceByName(QName name) {
    return getFlagContainer().getFlagInstanceMap().get(name);
  }

  @Override
  @NonNull
  default Collection<? extends FI> getFlagInstances() {
    return ObjectUtils.notNull(getFlagContainer().getFlagInstanceMap().values());
  }

  @Override
  default FI getJsonKey() {
    return getFlagContainer().getJsonKeyFlagInstance();
  }
}
