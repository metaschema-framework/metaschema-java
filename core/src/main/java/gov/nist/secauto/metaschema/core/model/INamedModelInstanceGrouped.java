/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents an arbitrary grouping of Metaschema model instances.
 */
public interface INamedModelInstanceGrouped extends INamedModelInstance {
  @Override
  IChoiceGroupInstance getParentContainer();

  @Override
  default IAssemblyDefinition getContainingDefinition() {
    return getParentContainer().getContainingDefinition();
  }

  /**
   * Get the discriminator JSON property name to use to identify the type of a
   * given instance object.
   *
   * @return the discriminator property name or {@code null} if the effective name
   *         should be used instead
   */
  @Nullable
  String getDiscriminatorValue();

  /**
   * Get the effective discriminator JSON property name to use to identify the
   * type of a given instance object.
   *
   * @return the discriminator property name
   */
  @NonNull
  default String getEffectiveDisciminatorValue() {
    String retval = getDiscriminatorValue();
    if (retval == null) {
      retval = getEffectiveName();
    }
    return retval;
  }

  @Override
  @Nullable
  default IFlagInstance getEffectiveJsonKey() {
    return JsonGroupAsBehavior.KEYED.equals(getParentContainer().getJsonGroupAsBehavior())
        ? ObjectUtils.requireNonNull(getJsonKey())
        : null;
  }

  @Override
  @Nullable
  default IFlagInstance getJsonKey() {
    String name = getParentContainer().getJsonKeyFlagInstanceName();
    return name == null
        ? null
        : ObjectUtils.requireNonNull(getDefinition().getFlagInstanceByName(getContainingModule().toFlagQName(name)));
  }

  @Override
  default int getMinOccurs() {
    return getParentContainer().getMinOccurs();
  }

  @Override
  default int getMaxOccurs() {
    return getParentContainer().getMaxOccurs();
  }

}
