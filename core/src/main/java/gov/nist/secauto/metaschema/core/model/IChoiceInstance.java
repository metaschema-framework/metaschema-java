/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.Locale;

/**
 * A marker interface for a choice of allowed instances in a Metachema.
 */
public interface IChoiceInstance extends IModelInstanceAbsolute, IContainerModelAbsolute {

  /**
   * Provides the Metaschema model type of "CHOICE".
   *
   * @return the model type
   */
  @Override
  default ModelType getModelType() {
    return ModelType.CHOICE;
  }

  @Override
  default IAssemblyDefinition getOwningDefinition() {
    return getParentContainer().getOwningDefinition();
  }

  @Override
  default int getMinOccurs() {
    return 1;
  }

  @Override
  default int getMaxOccurs() {
    return 1;
  }

  @Override
  default IEnhancedQName getEffectiveXmlGroupAsQName() {
    // never grouped
    return null;
  }

  @Override
  default boolean isEffectiveValueWrappedInXml() {
    throw new UnsupportedOperationException("not applicable");
  }

  @SuppressWarnings("null")
  @Override
  default String toCoordinates() {
    return String.format("%s-instance:%s:%s@%d",
        getModelType().toString().toLowerCase(Locale.ROOT),
        getContainingDefinition().getContainingModule().getShortName(),
        getContainingDefinition().getName(),
        hashCode());
  }
}
