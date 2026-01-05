/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.test;

import dev.metaschema.core.datatype.adapter.BooleanAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.JsonKey;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;

@MetaschemaAssembly(name = "flagged-bound-assembly", moduleClass = TestMetaschema.class)
public class FlaggedBoundAssembly implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @JsonKey
  @BoundFlag(name = "assembly-required-flag", required = true)
  private String id; // NOPMD

  @BoundFlag(name = "assembly-other-flag", typeAdapter = BooleanAdapter.class)
  private String other; // NOPMD

  public FlaggedBoundAssembly() {
    this(null);
  }

  public FlaggedBoundAssembly(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }
}
