/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.test;

import dev.metaschema.core.model.IAnyContent;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.databind.model.annotations.BoundAny;
import dev.metaschema.databind.model.annotations.BoundField;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import edu.umd.cs.findbugs.annotations.Nullable;

@SuppressWarnings("PMD")
@MetaschemaAssembly(name = "any-assembly", rootName = "any-assembly", moduleClass = TestMetaschema.class)
public class AnyAssembly implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @BoundField(useName = "known-field")
  private String knownField;

  @BoundAny
  @Nullable
  private IAnyContent any;

  public AnyAssembly() {
    this(null);
  }

  public AnyAssembly(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }

  public String getKnownField() {
    return knownField;
  }

  public void setKnownField(String knownField) {
    this.knownField = knownField;
  }

  @Nullable
  public IAnyContent getAny() {
    return any;
  }

  public void setAny(@Nullable IAnyContent any) {
    this.any = any;
  }
}
