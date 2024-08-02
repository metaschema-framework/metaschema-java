/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

/**
 * Provides methods used during deserialization to perform additional actions
 * before and after data is loaded into a bound object.
 * <p>
 * Methods with these method signatures will be called if defined on a bound
 * object regardless of if this interface is implemented by the object.
 */
public interface IDeserializationHandler {

  /**
   * A method called just before the object data is read and added to the object.
   *
   * @param parent
   *          the Java object containing this object
   */
  void beforeDeserialize(Object parent);

  /**
   * A method called just after the object's data is read and added to the object.
   *
   * @param parent
   *          the Java object containing this object
   */
  void afterDeserialize(Object parent);

}
