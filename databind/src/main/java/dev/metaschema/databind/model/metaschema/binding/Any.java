/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.model.metaschema.binding;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;

/**
 * Any Additional Content.
 */
@MetaschemaAssembly(
    formalName = "Any Additional Content",
    name = "any",
    moduleClass = MetaschemaModelModule.class)
public class Any implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * Constructs a new {@code dev.metaschema.databind.model.metaschema.binding.Any}
   * instance with no metadata.
   */
  public Any() {
    this(null);
  }

  /**
   * Constructs a new {@code dev.metaschema.databind.model.metaschema.binding.Any}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public Any(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
