/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.impl;

import gov.nist.secauto.metaschema.core.model.IContainerFlagSupport;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class DefaultContainerFlagSupport<FI extends IFlagInstance> implements IContainerFlagSupport<FI> {
  @NonNull
  private final Map<QName, FI> instances;
  @Nullable
  private final FI jsonKey;

  /**
   * Construct a new flag container using the provided flag instances.
   *
   * @param instances
   *          a collection of flag instances
   * @param jsonKey
   *          the JSON key flag instance or {@code null} if no JSON key is
   *          configured
   */
  public DefaultContainerFlagSupport(
      @NonNull Map<QName, FI> instances,
      @Nullable FI jsonKey) {
    this.instances = instances;
    this.jsonKey = jsonKey;
  }

  @Override
  public Map<QName, FI> getFlagInstanceMap() {
    return instances;
  }

  /**
   * Retrieves the flag instance to use as as the property name for the containing
   * object in JSON who's value will be the object containing the flag.
   *
   * @return the flag instance if a JSON key is configured, or {@code null}
   *         otherwise
   */
  @Override
  public FI getJsonKeyFlagInstance() {
    return jsonKey;
  }
}
