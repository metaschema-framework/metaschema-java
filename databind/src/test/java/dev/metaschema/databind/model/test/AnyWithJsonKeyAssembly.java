/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.test;

import java.util.Map;

import dev.metaschema.core.model.IAnyContent;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.model.XmlGroupAsBehavior;
import dev.metaschema.databind.model.annotations.BoundAny;
import dev.metaschema.databind.model.annotations.BoundField;
import dev.metaschema.databind.model.annotations.BoundFieldValue;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.GroupAs;
import dev.metaschema.databind.model.annotations.JsonKey;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import dev.metaschema.databind.model.annotations.MetaschemaField;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A test assembly combining {@code @BoundAny} with JSON-key-based fields to
 * verify that json-key-matched properties are correctly resolved and not
 * captured as "any" content.
 */
@SuppressWarnings("PMD")
@MetaschemaAssembly(
    name = "any-json-key-assembly",
    rootName = "any-json-key-assembly",
    moduleClass = TestMetaschema.class)
public class AnyWithJsonKeyAssembly implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @BoundField(useName = "known-field")
  private String knownField;

  @BoundField(
      maxOccurs = -1,
      groupAs = @GroupAs(name = "keyed-fields",
          inXml = XmlGroupAsBehavior.UNGROUPED,
          inJson = JsonGroupAsBehavior.KEYED))
  private Map<String, KeyedField> keyedField;

  @BoundAny
  @Nullable
  private IAnyContent any;

  /**
   * Constructs a new instance with no Metaschema data.
   */
  public AnyWithJsonKeyAssembly() {
    this(null);
  }

  /**
   * Constructs a new instance with the specified Metaschema data.
   *
   * @param metaschemaData
   *          the Metaschema data associated with this instance, or {@code null}
   */
  public AnyWithJsonKeyAssembly(@Nullable IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }

  /**
   * Gets the known field value.
   *
   * @return the known field value, or {@code null} if not set
   */
  @Nullable
  public String getKnownField() {
    return knownField;
  }

  /**
   * Sets the known field value.
   *
   * @param knownField
   *          the value to set, or {@code null} to clear
   */
  public void setKnownField(@Nullable String knownField) {
    this.knownField = knownField;
  }

  /**
   * Gets the keyed field map.
   *
   * @return the map of keyed fields, or {@code null} if not set
   */
  @Nullable
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "this is a data holder")
  public Map<String, KeyedField> getKeyedField() {
    return keyedField;
  }

  /**
   * Sets the keyed field map.
   *
   * @param keyedField
   *          the map to set, or {@code null} to clear
   */
  public void setKeyedField(@Nullable Map<String, KeyedField> keyedField) {
    this.keyedField = keyedField;
  }

  /**
   * Gets the any content.
   *
   * @return the any content, or {@code null} if not set
   */
  @Nullable
  public IAnyContent getAny() {
    return any;
  }

  /**
   * Sets the any content.
   *
   * @param any
   *          the any content to set, or {@code null} to clear
   */
  public void setAny(@Nullable IAnyContent any) {
    this.any = any;
  }

  /**
   * A simple field with a JSON key flag, used to test keyed field serialization.
   */
  @SuppressWarnings("PMD")
  @MetaschemaField(
      name = "keyed-field",
      moduleClass = TestMetaschema.class)
  public static class KeyedField implements IBoundObject {
    private final IMetaschemaData metaschemaData;

    @BoundFlag
    @JsonKey
    private String id;

    @BoundFieldValue
    private String _value;

    /**
     * Constructs a new instance with no Metaschema data.
     */
    public KeyedField() {
      this(null);
    }

    /**
     * Constructs a new instance with the specified Metaschema data.
     *
     * @param metaschemaData
     *          the Metaschema data associated with this instance, or {@code null}
     */
    public KeyedField(@Nullable IMetaschemaData metaschemaData) {
      this.metaschemaData = metaschemaData;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }

    /**
     * Gets the key identifier.
     *
     * @return the key, or {@code null} if not set
     */
    @Nullable
    public String getId() {
      return id;
    }

    /**
     * Sets the key identifier.
     *
     * @param id
     *          the key to set, or {@code null} to clear
     */
    public void setId(@Nullable String id) {
      this.id = id;
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

    /**
     * Sets the field value.
     *
     * @param value
     *          the value to set, or {@code null} to clear
     */
    public void setValue(@Nullable String value) {
      this._value = value;
    }
  }
}
