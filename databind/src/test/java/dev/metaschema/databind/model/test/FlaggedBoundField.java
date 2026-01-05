/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.test;

import dev.metaschema.core.datatype.adapter.BooleanAdapter;
import dev.metaschema.core.datatype.adapter.TokenAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.databind.model.annotations.BoundFieldValue;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.JsonKey;
import dev.metaschema.databind.model.annotations.MetaschemaField;

@MetaschemaField(name = "flagged-bound-field", moduleClass = TestMetaschema.class)
public class FlaggedBoundField implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @JsonKey
  @BoundFlag(name = "field-required-flag", typeAdapter = TokenAdapter.class, required = true)
  private String id; // NOPMD - intentional

  @BoundFlag(name = "field-other-flag", typeAdapter = BooleanAdapter.class)
  private String other; // NOPMD - intentional

  @BoundFieldValue(valueKeyName = "field-value")
  private String _value; // NOPMD - intentional

  public FlaggedBoundField() {
    this(null);
  }

  public FlaggedBoundField(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }
}
