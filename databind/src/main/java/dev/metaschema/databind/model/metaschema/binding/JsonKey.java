/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.model.metaschema.binding;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import dev.metaschema.core.datatype.adapter.TokenAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Used in JSON (and similar formats) to identify a flag that will be used as
 * the property name in an object hold a collection of sibling objects. Requires
 * that siblings must never share <code>json-key</code> values.
 */
@MetaschemaAssembly(
    formalName = "JSON Key",
    description = "Used in JSON (and similar formats) to identify a flag that will be used as the property name in an object hold a collection of sibling objects. Requires that siblings must never share `json-key` values.",
    name = "json-key",
    moduleClass = MetaschemaModelModule.class)
public class JsonKey implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * References the flag that will serve as the JSON key.
   */
  @BoundFlag(
      formalName = "JSON Key Flag Reference",
      description = "References the flag that will serve as the JSON key.",
      name = "flag-ref",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _flagRef;

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.JsonKey} instance
   * with no metadata.
   */
  public JsonKey() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.JsonKey} instance
   * with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public JsonKey(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the jSON Key Flag Reference.
   *
   * <p>
   * References the flag that will serve as the JSON key.
   *
   * @return the flag-ref value
   */
  @NonNull
  public String getFlagRef() {
    return _flagRef;
  }

  /**
   * Set the jSON Key Flag Reference.
   *
   * <p>
   * References the flag that will serve as the JSON key.
   *
   * @param value
   *          the flag-ref value to set
   */
  public void setFlagRef(@NonNull String value) {
    _flagRef = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
