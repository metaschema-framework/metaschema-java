/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IFeatureContainerFlag;
import gov.nist.secauto.metaschema.databind.IBindingContext;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a field or assembly instance bound to Java data.
 *
 * @param <ITEM>
 *          the Java type for associated bound objects
 */
public interface IBoundDefinitionModel<ITEM>
    extends IBoundModelObject<ITEM>, IFeatureContainerFlag<IBoundInstanceFlag>, IBoundDefinition {
  /**
   * Get the binding context used for the definition.
   *
   * @return the binding context
   */
  @NonNull
  IBindingContext getBindingContext();

  @Override
  IBoundInstanceModelNamed<ITEM> getInlineInstance();

  //
  // @Override
  // IBoundInstanceFlag getJsonKeyFlagInstance();
}
