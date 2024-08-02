/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.testing.model;

import gov.nist.secauto.metaschema.core.datatype.adapter.IntegerAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;

import java.math.BigInteger;

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

  public RootAssemblyWithFlags() {
    this(null);
  }

  public RootAssemblyWithFlags(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  protected String getDefaultFlag() {
    return defaultFlag;
  }

  protected void setDefaultFlag(String defaultFlag) {
    this.defaultFlag = defaultFlag;
  }

  public BigInteger getNumber() {
    return number;
  }

  public void setNumber(BigInteger number) {
    this.number = number;
  }
}
