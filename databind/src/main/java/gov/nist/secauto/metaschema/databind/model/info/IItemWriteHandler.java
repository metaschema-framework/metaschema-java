/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelFieldComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelFieldScalar;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedField;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IItemWriteHandler {
  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param instance
   *          the flag instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeItemFlag(
      @NonNull Object item,
      @NonNull IBoundInstanceFlag instance) throws IOException;

  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param instance
   *          the field instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeItemField(
      @NonNull Object item,
      @NonNull IBoundInstanceModelFieldScalar instance) throws IOException;

  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param instance
   *          the field instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeItemField(
      @NonNull IBoundObject item,
      @NonNull IBoundInstanceModelFieldComplex instance) throws IOException;

  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param instance
   *          the field instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeItemField(
      @NonNull IBoundObject item,
      @NonNull IBoundInstanceModelGroupedField instance) throws IOException;

  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param definition
   *          the field instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeItemField(
      @NonNull IBoundObject item,
      @NonNull IBoundDefinitionModelFieldComplex definition) throws IOException;

  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param fieldValue
   *          the field value instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeItemFieldValue(
      @NonNull Object item,
      @NonNull IBoundFieldValue fieldValue) throws IOException;

  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param instance
   *          the assembly instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeItemAssembly(
      @NonNull IBoundObject item,
      @NonNull IBoundInstanceModelAssembly instance) throws IOException;

  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param instance
   *          the assembly instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeItemAssembly(
      @NonNull IBoundObject item,
      @NonNull IBoundInstanceModelGroupedAssembly instance) throws IOException;

  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param definition
   *          the assembly instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeItemAssembly(
      @NonNull IBoundObject item,
      @NonNull IBoundDefinitionModelAssembly definition) throws IOException;

  /**
   * Write an item.
   *
   * @param item
   *          the Java object representing the item to write
   * @param instance
   *          the choice group instance
   * @throws IOException
   *           if an error occurred while parsing
   */
  void writeChoiceGroupItem(
      @NonNull IBoundObject item,
      @NonNull IBoundInstanceModelChoiceGroup instance) throws IOException;

}
