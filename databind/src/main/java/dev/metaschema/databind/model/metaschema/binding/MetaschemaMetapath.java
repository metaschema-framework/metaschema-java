/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.model.metaschema.binding;

import dev.metaschema.core.datatype.adapter.StringAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A Metapath expression identifying the model node that the constraints will be
 * applied to.
 */
@MetaschemaAssembly(
    description = "A Metapath expression identifying the model node that the constraints will be applied to.",
    name = "metaschema-metapath",
    moduleClass = MetaschemaModelModule.class)
public class MetaschemaMetapath implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      name = "target",
      required = true,
      typeAdapter = StringAdapter.class)
  private String _target;

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaMetapath}
   * instance with no metadata.
   */
  public MetaschemaMetapath() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaMetapath}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public MetaschemaMetapath(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the {@code target} property.
   *
   * @return the target value
   */
  @NonNull
  public String getTarget() {
    return _target;
  }

  /**
   * Set the {@code target} property.
   *
   * @param value
   *          the target value to set
   */
  public void setTarget(@NonNull String value) {
    _target = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
