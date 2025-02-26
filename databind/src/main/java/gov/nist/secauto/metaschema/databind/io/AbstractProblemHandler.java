/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundProperty;

import java.io.IOException;
import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractProblemHandler implements IProblemHandler {

  @Override
  public void handleMissingInstances(
      IBoundDefinitionModelComplex parentDefinition,
      IBoundObject targetObject,
      Collection<? extends IBoundProperty<?>> unhandledInstances) throws IOException {
    applyDefaults(targetObject, unhandledInstances);
  }

  /**
   * A utility method for applying default values for the provided
   * {@code unhandledInstances}.
   *
   * @param targetObject
   *          the Java object to apply default values to
   * @param unhandledInstances
   *          the collection of unhandled instances to assign default values for
   * @throws IOException
   *           if an error occurred while determining the default value for an
   *           instance
   */
  protected static void applyDefaults(
      @NonNull Object targetObject,
      @NonNull Collection<? extends IBoundProperty<?>> unhandledInstances) throws IOException {
    for (IBoundProperty<?> instance : unhandledInstances) {
      assert instance != null;
      Object value = instance.getResolvedDefaultValue();
      if (value != null) {
        instance.setValue(targetObject, value);
      }
    }
  }
}
