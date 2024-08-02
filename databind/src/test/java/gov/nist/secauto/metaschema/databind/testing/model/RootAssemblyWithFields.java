/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.testing.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.JsonFieldValueKeyFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.JsonKey;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaField;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// Used
@SuppressWarnings({ "PMD", "checkstyle:MemberNameCheck" })
@MetaschemaAssembly(
    name = "assembly-with-fields",
    rootName = "root-assembly-with-fields",
    moduleClass = TestModule.class)
public class RootAssemblyWithFields implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @BoundField
  private String defaultField;

  @BoundField(useName = "field2",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "fields2",
          inXml = XmlGroupAsBehavior.GROUPED,
          inJson = JsonGroupAsBehavior.LIST))
  private List<String> _field2;

  @BoundField
  private ValueKeyField field3;

  @BoundField
  private DefaultValueKeyField field4;

  @BoundField
  private FlagValueKeyField field5;

  @BoundField(
      maxOccurs = -1,
      groupAs = @GroupAs(name = "fields6",
          inXml = XmlGroupAsBehavior.UNGROUPED,
          inJson = JsonGroupAsBehavior.KEYED))
  private Map<String, JsonKeyField> field6;

  public RootAssemblyWithFields() {
    this(null);
  }

  public RootAssemblyWithFields(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }

  public String getField1() {
    return defaultField;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "this is a data holder")
  public List<String> getField2() {
    return _field2;
  }

  public ValueKeyField getField3() {
    return field3;
  }

  public void setField3(ValueKeyField field3) {
    this.field3 = field3;
  }

  public DefaultValueKeyField getField4() {
    return field4;
  }

  public void setField4(DefaultValueKeyField field4) {
    this.field4 = field4;
  }

  public FlagValueKeyField getField5() {
    return field5;
  }

  public void setField5(FlagValueKeyField field5) {
    this.field5 = field5;
  }

  public Map<String, JsonKeyField> getField6() {
    return field6;
  }

  public void setField6(Map<String, JsonKeyField> field6) {
    this.field6 = field6;
  }

  @SuppressWarnings("PMD")
  @MetaschemaField(
      name = "field-value-key",
      moduleClass = TestModule.class)
  public static class ValueKeyField implements IBoundObject {
    private final IMetaschemaData metaschemaData;

    @BoundFlag
    private String flag;

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

    public String getFlag() {
      return flag;
    }

    public String getValue() {
      return _value;
    }
  }

  @SuppressWarnings("PMD")
  @MetaschemaField(
      name = "field-default-value-key",
      moduleClass = TestModule.class)
  public static class DefaultValueKeyField implements IBoundObject {
    private final IMetaschemaData metaschemaData;

    @BoundFlag
    private String flag;

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

    public String getFlag() {
      return flag;
    }

    public String getValue() {
      return _value;
    }
  }

  @SuppressWarnings("PMD")
  @MetaschemaField(
      name = "field-flag-value-key",
      moduleClass = TestModule.class)
  public static class FlagValueKeyField implements IBoundObject {
    private final IMetaschemaData metaschemaData;

    @BoundFlag
    @JsonFieldValueKeyFlag
    private String flag;

    @BoundFieldValue
    private String _value;

    public FlagValueKeyField() {
      this(null);
    }

    public FlagValueKeyField(IMetaschemaData metaschemaData) {
      this.metaschemaData = metaschemaData;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }

    public String getFlag() {
      return flag;
    }

    public String getValue() {
      return _value;
    }
  }

  @SuppressWarnings("PMD")
  @MetaschemaField(
      name = "field-json-key",
      moduleClass = TestModule.class)
  public static class JsonKeyField implements IBoundObject {
    private final IMetaschemaData metaschemaData;

    @BoundFlag
    @JsonKey
    private String key;

    @BoundFlag
    @JsonFieldValueKeyFlag
    private String valueKey;

    @BoundFieldValue
    private String _value;

    public JsonKeyField() {
      this(null);
    }

    public JsonKeyField(IMetaschemaData metaschemaData) {
      this.metaschemaData = metaschemaData;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }

    public String getKey() {
      return key;
    }

    public String getValueKey() {
      return valueKey;
    }

    public String getValue() {
      return _value;
    }
  }
}
