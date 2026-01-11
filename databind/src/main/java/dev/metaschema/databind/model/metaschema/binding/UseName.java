/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.model.metaschema.binding;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigInteger;

import dev.metaschema.core.datatype.adapter.NonNegativeIntegerAdapter;
import dev.metaschema.core.datatype.adapter.TokenAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.annotations.BoundFieldValue;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.MetaschemaField;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Allows the name of the definition to be overridden.
 */
@MetaschemaField(
    formalName = "Use Name",
    description = "Allows the name of the definition to be overridden.",
    name = "use-name",
    moduleClass = MetaschemaModelModule.class)
public class UseName implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * Used for binary formats instead of the textual name.
   */
  @BoundFlag(
      formalName = "Numeric Index",
      description = "Used for binary formats instead of the textual name.",
      name = "index",
      typeAdapter = NonNegativeIntegerAdapter.class)
  private BigInteger _index;

  /**
   * The field value.
   */
  @BoundFieldValue(
      valueKeyName = "name",
      typeAdapter = TokenAdapter.class)
  private String _name;

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.UseName} instance
   * with no metadata.
   */
  public UseName() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.UseName} instance
   * with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public UseName(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the numeric Index.
   *
   * <p>
   * Used for binary formats instead of the textual name.
   *
   * @return the index value, or {@code null} if not set
   */
  @Nullable
  public BigInteger getIndex() {
    return _index;
  }

  /**
   * Set the numeric Index.
   *
   * <p>
   * Used for binary formats instead of the textual name.
   *
   * @param value
   *          the index value to set, or {@code null} to clear
   */
  public void setIndex(@Nullable BigInteger value) {
    _index = value;
  }

  /**
   * Get the field value.
   *
   * @return the value
   */
  @Nullable
  public String getName() {
    return _name;
  }

  /**
   * Set the field value.
   *
   * @param value
   *          the value to set
   */
  public void setName(@Nullable String value) {
    _name = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
