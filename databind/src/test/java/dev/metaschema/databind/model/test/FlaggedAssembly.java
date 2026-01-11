/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.test;

import java.math.BigInteger;

import dev.metaschema.core.datatype.adapter.IntegerAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;

// Used
@MetaschemaAssembly(name = "flagged-assembly", rootName = "flagged-assembly", moduleClass = TestMetaschema.class)
public class FlaggedAssembly implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @BoundFlag(name = "id")
  private String id;
  @BoundFlag(name = "number", typeAdapter = IntegerAdapter.class)
  private BigInteger number;

  public FlaggedAssembly() {
    this(null);
  }

  public FlaggedAssembly(IMetaschemaData metaschemaData) {
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

  public BigInteger getNumber() {
    return number;
  }

  public void setNumber(BigInteger number) {
    this.number = number;
  }

}
