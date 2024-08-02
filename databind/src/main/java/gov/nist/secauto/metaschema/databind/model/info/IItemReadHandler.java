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
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IItemReadHandler {
  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks
   * @param instance
   *          the flag instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  Object readItemFlag(
      @NonNull IBoundObject parent,
      @NonNull IBoundInstanceFlag instance) throws IOException;

  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks
   * @param instance
   *          the field instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  Object readItemField(
      @NonNull IBoundObject parent,
      @NonNull IBoundInstanceModelFieldScalar instance) throws IOException;

  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks
   * @param instance
   *          the field instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  IBoundObject readItemField(
      @NonNull IBoundObject parent,
      @NonNull IBoundInstanceModelFieldComplex instance) throws IOException;

  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks
   * @param instance
   *          the field instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  IBoundObject readItemField(
      @NonNull IBoundObject parent,
      @NonNull IBoundInstanceModelGroupedField instance) throws IOException;

  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks, or
   *          {@code null} if there is no parent
   * @param definition
   *          the field instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  IBoundObject readItemField(
      @Nullable IBoundObject parent,
      @NonNull IBoundDefinitionModelFieldComplex definition) throws IOException;

  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks
   * @param fieldValue
   *          the field value instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  Object readItemFieldValue(
      @NonNull IBoundObject parent,
      @NonNull IBoundFieldValue fieldValue) throws IOException;

  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks
   * @param instance
   *          the assembly instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  IBoundObject readItemAssembly(
      @NonNull IBoundObject parent,
      @NonNull IBoundInstanceModelAssembly instance) throws IOException;

  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks
   * @param instance
   *          the assembly instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  IBoundObject readItemAssembly(
      @NonNull IBoundObject parent,
      @NonNull IBoundInstanceModelGroupedAssembly instance) throws IOException;

  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks, or
   *          {@code null} if there is no parent
   * @param definition
   *          the assembly instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  IBoundObject readItemAssembly(
      @Nullable IBoundObject parent,
      @NonNull IBoundDefinitionModelAssembly definition) throws IOException;

  /**
   * Parse and return an item.
   *
   * @param parent
   *          the parent Java object to use for serialization callbacks
   * @param instance
   *          the choice group instance
   * @return the Java object representing the parsed item
   * @throws IOException
   *           if an error occurred while parsing
   */
  @NonNull
  IBoundObject readChoiceGroupItem(
      @NonNull IBoundObject parent,
      @NonNull IBoundInstanceModelChoiceGroup instance) throws IOException;
}
