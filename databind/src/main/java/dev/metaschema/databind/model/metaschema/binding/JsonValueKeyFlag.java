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
 * Flag Used as the Field Value's JSON Property Name.
 */
@MetaschemaAssembly(
    formalName = "Flag Used as the Field Value's JSON Property Name",
    name = "json-value-key-flag",
    moduleClass = MetaschemaModelModule.class)
public class JsonValueKeyFlag implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Flag Reference",
      name = "flag-ref",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _flagRef;

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.JsonValueKeyFlag}
   * instance with no metadata.
   */
  public JsonValueKeyFlag() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.JsonValueKeyFlag}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public JsonValueKeyFlag(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the flag Reference.
   *
   * @return the flag-ref value
   */
  @NonNull
  public String getFlagRef() {
    return _flagRef;
  }

  /**
   * Set the flag Reference.
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
