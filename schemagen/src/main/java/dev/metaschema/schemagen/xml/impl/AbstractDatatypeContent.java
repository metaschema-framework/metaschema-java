/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl;

import java.util.ArrayList;
import java.util.List;

import dev.metaschema.core.util.CollectionUtil;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Provides a common base implementation for datatype content used in XML schema
 * generation.
 * <p>
 * This class represents the schema content for a single datatype, including its
 * type name and any dependencies on other datatypes.
 */
public abstract class AbstractDatatypeContent implements IDatatypeContent {
  @NonNull
  private final String typeName;
  @NonNull
  private final List<String> dependencies;

  /**
   * Construct a new datatype content instance.
   *
   * @param typeName
   *          the name of the datatype
   * @param dependencies
   *          the list of datatype names this type depends on
   */
  public AbstractDatatypeContent(@NonNull String typeName, @NonNull List<String> dependencies) {
    this.typeName = typeName;
    this.dependencies = CollectionUtil.unmodifiableList(new ArrayList<>(dependencies));
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<String> getDependencies() {
    return dependencies;
  }
}
