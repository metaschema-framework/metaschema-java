/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.test;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaField;

@SuppressWarnings("PMD")
@MetaschemaField(
    name = "simple-field-value-key",
    moduleClass = TestMetaschema.class)
public class ValueKeyField implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @BoundFieldValue(valueKeyName = "a-value")
  private String _value;

  public ValueKeyField() {
    this(null);
  }

  public ValueKeyField(IMetaschemaData metaschemaData) {
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
