/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.test;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;

@MetaschemaAssembly(name = "empty-bound-assembly", rootName = "root", moduleClass = TestMetaschema.class)
public class EmptyBoundAssembly implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  public EmptyBoundAssembly() {
    this(null);
  }

  public EmptyBoundAssembly(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }
}
