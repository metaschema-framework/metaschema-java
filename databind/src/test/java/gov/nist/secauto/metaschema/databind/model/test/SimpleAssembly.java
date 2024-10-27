/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.test;

import gov.nist.secauto.metaschema.core.datatype.adapter.IntegerAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;

import java.math.BigInteger;

@SuppressWarnings("PMD")
@MetaschemaAssembly(name = "simple-assembly", rootName = "test", moduleClass = TestMetaschema.class)
public class SimpleAssembly implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @BoundFlag(name = "id")
  private String _id;

  @BoundFlag(name = "number", typeAdapter = IntegerAdapter.class)
  private BigInteger _number;

  public SimpleAssembly() {
    this(null);
  }

  public SimpleAssembly(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }

  public String getId() {
    return _id;
  }

  public BigInteger getNumber() {
    return _number;
  }
}
