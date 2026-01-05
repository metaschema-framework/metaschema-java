/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.model.metaschema.binding;

import dev.metaschema.core.datatype.adapter.TokenAdapter;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.datatype.markup.MarkupMultilineAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.annotations.AllowedValue;
import dev.metaschema.databind.model.annotations.AllowedValues;
import dev.metaschema.databind.model.annotations.BoundFieldValue;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.MetaschemaField;
import dev.metaschema.databind.model.annotations.ValueConstraints;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Any explanatory or helpful information to be provided about the remarks
 * parent.
 */
@MetaschemaField(
    formalName = "Remarks",
    description = "Any explanatory or helpful information to be provided about the remarks parent.",
    name = "remarks",
    moduleClass = MetaschemaModelModule.class)
public class Remarks implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * Mark as &lsquo;XML&rsquo; for XML-only or &lsquo;JSON&rsquo; for JSON-only
   * remarks.
   */
  @BoundFlag(
      formalName = "Remark Class",
      description = "Mark as 'XML' for XML-only or 'JSON' for JSON-only remarks.",
      name = "class",
      defaultValue = "ALL",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
          values = { @AllowedValue(value = "XML", description = "The remark applies to only XML representations."),
              @AllowedValue(value = "JSON", description = "The remark applies to only JSON and YAML representations."),
              @AllowedValue(value = "ALL", description = "The remark applies to all representations.") })))
  private String _clazz;

  /**
   * The field value.
   */
  @BoundFieldValue(
      valueKeyName = "remark",
      typeAdapter = MarkupMultilineAdapter.class)
  private MarkupMultiline _remark;

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.Remarks} instance
   * with no metadata.
   */
  public Remarks() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.Remarks} instance
   * with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public Remarks(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the remark Class.
   *
   * <p>
   * Mark as &lsquo;XML&rsquo; for XML-only or &lsquo;JSON&rsquo; for JSON-only
   * remarks.
   *
   * @return the class value, or {@code null} if not set
   */
  @Nullable
  public String getClazz() {
    return _clazz;
  }

  /**
   * Set the remark Class.
   *
   * <p>
   * Mark as &lsquo;XML&rsquo; for XML-only or &lsquo;JSON&rsquo; for JSON-only
   * remarks.
   *
   * @param value
   *          the class value to set, or {@code null} to clear
   */
  public void setClazz(@Nullable String value) {
    _clazz = value;
  }

  /**
   * Get the field value.
   *
   * @return the value
   */
  @Nullable
  public MarkupMultiline getRemark() {
    return _remark;
  }

  /**
   * Set the field value.
   *
   * @param value
   *          the value to set
   */
  public void setRemark(@Nullable MarkupMultiline value) {
    _remark = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
