/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.info;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import dev.metaschema.databind.model.IBoundFieldValue;
import dev.metaschema.databind.model.IBoundInstanceFlag;
import dev.metaschema.databind.model.IBoundInstanceModelAssembly;
import dev.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import dev.metaschema.databind.model.IBoundInstanceModelFieldComplex;
import dev.metaschema.databind.model.IBoundInstanceModelFieldScalar;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedField;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Handler interface for reading bound items during deserialization.
 * <p>
 * Implementations of this interface handle the reading of different types of
 * model items (flags, fields, assemblies, choice groups).
 */
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
  @Nullable
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
  @Nullable
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
  @Nullable
  IBoundObject readChoiceGroupItem(
      @NonNull IBoundObject parent,
      @NonNull IBoundInstanceModelChoiceGroup instance) throws IOException;
}
