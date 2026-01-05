/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.testing.model;

import dev.metaschema.core.datatype.adapter.IntegerAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;

import java.math.BigInteger;

/**
 * A test assembly class containing various flag binding configurations for
 * testing purposes.
 */
@MetaschemaAssembly(
    name = "assembly-with-flags",
    rootName = "root-assembly-with-flags",
    moduleClass = TestModule.class)
public class RootAssemblyWithFlags implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @BoundFlag(name = "id", required = true)
  private String id;

  @BoundFlag
  private String defaultFlag;

  @BoundFlag(
      description = "a number",
      formalName = "number flag",
      name = "number",
      typeAdapter = IntegerAdapter.class,
      defaultValue = "1",
      remarks = "a remark")
  private BigInteger number;

  /**
   * Constructs a new instance with no Metaschema data.
   */
  public RootAssemblyWithFlags() {
    this(null);
  }

  /**
   * Constructs a new instance with the specified Metaschema data.
   *
   * @param metaschemaData
   *          the Metaschema data associated with this instance, or {@code null}
   */
  public RootAssemblyWithFlags(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }

  /**
   * Gets the id flag value.
   *
   * @return the id value
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id flag value.
   *
   * @param id
   *          the id value to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the default flag value.
   *
   * @return the default flag value
   */
  protected String getDefaultFlag() {
    return defaultFlag;
  }

  /**
   * Sets the default flag value.
   *
   * @param defaultFlag
   *          the default flag value to set
   */
  protected void setDefaultFlag(String defaultFlag) {
    this.defaultFlag = defaultFlag;
  }

  /**
   * Gets the number flag value.
   *
   * @return the number value
   */
  public BigInteger getNumber() {
    return number;
  }

  /**
   * Sets the number flag value.
   *
   * @param number
   *          the number value to set
   */
  public void setNumber(BigInteger number) {
    this.number = number;
  }
}
