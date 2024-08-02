/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.Collection;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IModelDefinition extends IDefinition, IContainer {
  @Override
  default boolean hasChildren() {
    return !getFlagInstances().isEmpty();
  }

  /**
   * Retrieves a flag instance, by the flag's effective name, that is defined on
   * the containing definition.
   *
   * @param name
   *          the flag's name
   * @return the matching flag instance, or {@code null} if there is no flag
   *         matching the specified name
   */
  @Nullable
  IFlagInstance getFlagInstanceByName(@NonNull QName name);

  /**
   * Retrieves the flag instances for all flags defined on the containing
   * definition.
   *
   * @return the flags
   */
  @NonNull
  Collection<? extends IFlagInstance> getFlagInstances();

  /**
   * Retrieves the flag instance to use as as the property name for the containing
   * object in JSON who's value will be the object containing the flag.
   *
   * @return the flag instance if a JSON key is configured, or {@code null}
   *         otherwise
   */
  // TODO: remove once moved to the instance side
  // TODO: Reconsider using toFlagName in favor of getReferencedDefinitionQName
  @Nullable
  IFlagInstance getJsonKey();
}
