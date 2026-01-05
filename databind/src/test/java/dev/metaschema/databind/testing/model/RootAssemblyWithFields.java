/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.testing.model;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.model.XmlGroupAsBehavior;
import dev.metaschema.databind.model.annotations.BoundField;
import dev.metaschema.databind.model.annotations.BoundFieldValue;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.GroupAs;
import dev.metaschema.databind.model.annotations.JsonFieldValueKeyFlag;
import dev.metaschema.databind.model.annotations.JsonKey;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import dev.metaschema.databind.model.annotations.MetaschemaField;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A test assembly class containing various field binding configurations for
 * testing purposes.
 */
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

  /**
   * Constructs a new instance with no Metaschema data.
   */
  public RootAssemblyWithFields() {
    this(null);
  }

  /**
   * Constructs a new instance with the specified Metaschema data.
   *
   * @param metaschemaData
   *          the Metaschema data associated with this instance, or {@code null}
   */
  public RootAssemblyWithFields(@Nullable IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }

  /**
   * Gets the default field value.
   *
   * @return the default field value, or {@code null} if not set
   */
  @Nullable
  public String getField1() {
    return defaultField;
  }

  /**
   * Gets the second field value list.
   *
   * @return the list of field2 values, or {@code null} if not set
   */
  @Nullable
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "this is a data holder")
  public List<String> getField2() {
    return _field2;
  }

  /**
   * Gets the value key field.
   *
   * @return the value key field, or {@code null} if not set
   */
  @Nullable
  public ValueKeyField getField3() {
    return field3;
  }

  /**
   * Sets the value key field.
   *
   * @param field3
   *          the value key field to set, or {@code null} to clear
   */
  public void setField3(@Nullable ValueKeyField field3) {
    this.field3 = field3;
  }

  /**
   * Gets the default value key field.
   *
   * @return the default value key field, or {@code null} if not set
   */
  @Nullable
  public DefaultValueKeyField getField4() {
    return field4;
  }

  /**
   * Sets the default value key field.
   *
   * @param field4
   *          the default value key field to set, or {@code null} to clear
   */
  public void setField4(@Nullable DefaultValueKeyField field4) {
    this.field4 = field4;
  }

  /**
   * Gets the flag value key field.
   *
   * @return the flag value key field, or {@code null} if not set
   */
  @Nullable
  public FlagValueKeyField getField5() {
    return field5;
  }

  /**
   * Sets the flag value key field.
   *
   * @param field5
   *          the flag value key field to set, or {@code null} to clear
   */
  public void setField5(@Nullable FlagValueKeyField field5) {
    this.field5 = field5;
  }

  /**
   * Gets the JSON key fields map.
   *
   * @return the map of JSON key fields, or {@code null} if not set
   */
  @Nullable
  public Map<String, JsonKeyField> getField6() {
    return field6;
  }

  /**
   * Sets the JSON key fields map.
   *
   * @param field6
   *          the map of JSON key fields to set, or {@code null} to clear
   */
  public void setField6(@Nullable Map<String, JsonKeyField> field6) {
    this.field6 = field6;
  }

  /**
   * A test field class with a named value key.
   */
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

    /**
     * Constructs a new instance with no Metaschema data.
     */
    public ValueKeyField() {
      this(null);
    }

    /**
     * Constructs a new instance with the specified Metaschema data.
     *
     * @param metaschemaData
     *          the Metaschema data associated with this instance, or {@code null}
     */
    public ValueKeyField(@Nullable IMetaschemaData metaschemaData) {
      this.metaschemaData = metaschemaData;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }

    /**
     * Gets the flag value.
     *
     * @return the flag value, or {@code null} if not set
     */
    @Nullable
    public String getFlag() {
      return flag;
    }

    /**
     * Gets the field value.
     *
     * @return the field value, or {@code null} if not set
     */
    @Nullable
    public String getValue() {
      return _value;
    }
  }

  /**
   * A test field class with a default value key.
   */
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

    /**
     * Constructs a new instance with no Metaschema data.
     */
    public DefaultValueKeyField() {
      this(null);
    }

    /**
     * Constructs a new instance with the specified Metaschema data.
     *
     * @param metaschemaData
     *          the Metaschema data associated with this instance, or {@code null}
     */
    public DefaultValueKeyField(@Nullable IMetaschemaData metaschemaData) {
      this.metaschemaData = metaschemaData;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }

    /**
     * Gets the flag value.
     *
     * @return the flag value, or {@code null} if not set
     */
    @Nullable
    public String getFlag() {
      return flag;
    }

    /**
     * Gets the field value.
     *
     * @return the field value, or {@code null} if not set
     */
    @Nullable
    public String getValue() {
      return _value;
    }
  }

  /**
   * A test field class with a flag-based value key.
   */
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

    /**
     * Constructs a new instance with no Metaschema data.
     */
    public FlagValueKeyField() {
      this(null);
    }

    /**
     * Constructs a new instance with the specified Metaschema data.
     *
     * @param metaschemaData
     *          the Metaschema data associated with this instance, or {@code null}
     */
    public FlagValueKeyField(@Nullable IMetaschemaData metaschemaData) {
      this.metaschemaData = metaschemaData;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }

    /**
     * Gets the flag value.
     *
     * @return the flag value, or {@code null} if not set
     */
    @Nullable
    public String getFlag() {
      return flag;
    }

    /**
     * Gets the field value.
     *
     * @return the field value, or {@code null} if not set
     */
    @Nullable
    public String getValue() {
      return _value;
    }
  }

  /**
   * A test field class with JSON key support.
   */
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

    /**
     * Constructs a new instance with no Metaschema data.
     */
    public JsonKeyField() {
      this(null);
    }

    /**
     * Constructs a new instance with the specified Metaschema data.
     *
     * @param metaschemaData
     *          the Metaschema data associated with this instance, or {@code null}
     */
    public JsonKeyField(@Nullable IMetaschemaData metaschemaData) {
      this.metaschemaData = metaschemaData;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }

    /**
     * Gets the JSON key.
     *
     * @return the JSON key, or {@code null} if not set
     */
    @Nullable
    public String getKey() {
      return key;
    }

    /**
     * Gets the value key flag.
     *
     * @return the value key flag, or {@code null} if not set
     */
    @Nullable
    public String getValueKey() {
      return valueKey;
    }

    /**
     * Gets the field value.
     *
     * @return the field value, or {@code null} if not set
     */
    @Nullable
    public String getValue() {
      return _value;
    }
  }
}
