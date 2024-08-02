/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public interface IFieldInstanceAbsolute extends IFieldInstance, INamedModelInstanceAbsolute {

  @Override
  default boolean isEffectiveValueWrappedInXml() {
    return isInXmlWrapped() || !getDefinition().getJavaTypeAdapter().isUnrappedValueAllowedInXml();
  }
}
