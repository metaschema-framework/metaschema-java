/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IJsonNamed;
import gov.nist.secauto.metaschema.databind.io.BindingException;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IBoundProperty<ITEM> extends IBoundModelObject<ITEM>, IFeatureJavaField, IJsonNamed {
  /**
   * Copy this instance from one parent object to another.
   *
   * @param fromInstance
   *          the object to copy from
   * @param toInstance
   *          the object to copy to
   * @throws BindingException
   *           if an error occurred while processing the object bindings
   */
  void deepCopy(@NonNull IBoundObject fromInstance, @NonNull IBoundObject toInstance) throws BindingException;
}
