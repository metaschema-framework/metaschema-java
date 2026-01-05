/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.test;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.databind.model.annotations.BoundFieldValue;
import dev.metaschema.databind.model.annotations.MetaschemaField;

@SuppressWarnings("PMD")
@MetaschemaField(
    name = "simple-field",
    moduleClass = TestMetaschema.class)
public class DefaultValueKeyField implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @BoundFieldValue
  private String _value;

  public DefaultValueKeyField() {
    this(null);
  }

  public DefaultValueKeyField(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }

  public String getValue() {
    return _value;
  }
}
