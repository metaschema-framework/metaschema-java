/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import java.io.IOException;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IContainerFlagSupport;
import dev.metaschema.core.model.IFeatureDefinitionInstanceInlined;
import dev.metaschema.core.model.IModelElementVisitor;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.info.IFeatureScalarItemValueHandler;
import dev.metaschema.databind.model.info.IItemReadHandler;
import dev.metaschema.databind.model.info.IItemWriteHandler;

/**
 * Represents a bound field instance that contains scalar (simple) data, such as
 * a string or number value.
 */
public interface IBoundInstanceModelFieldScalar
    extends IBoundInstanceModelField<Object>,
    IBoundDefinitionModelField<Object>, IFeatureScalarItemValueHandler,
    IFeatureDefinitionInstanceInlined<IBoundDefinitionModelField<Object>, IBoundInstanceModelFieldScalar> {

  // integrate above
  @Override
  default IBoundDefinitionModelField<Object> getDefinition() {
    return IFeatureDefinitionInstanceInlined.super.getDefinition();
  }

  @Override
  default boolean isInline() {
    return IFeatureDefinitionInstanceInlined.super.isInline();
  }

  @Override
  default IBoundInstanceModelFieldScalar getInlineInstance() {
    return IFeatureDefinitionInstanceInlined.super.getInlineInstance();
  }

  @Override
  IBoundDefinitionModelAssembly getContainingDefinition();

  @Override
  default IContainerFlagSupport<IBoundInstanceFlag> getFlagContainer() {
    return IContainerFlagSupport.empty();
  }

  @Override
  default IBoundInstanceFlag getJsonKey() {
    // no flags
    return null;
  }

  @Override
  default IBoundInstanceFlag getItemJsonKey(Object item) {
    // no flags, no JSON key
    return null;
  }

  @Override
  default Object getFieldValue(Object item) {
    // the item is the field value
    return item;
  }

  @Override
  default String getJsonValueKeyName() {
    // no bound value, no value key name
    return null;
  }

  @Override
  default IBoundInstanceFlag getJsonValueKeyFlagInstance() {
    // no bound value, no value key name
    return null;
  }

  @Override
  default Object readItem(IBoundObject parent, IItemReadHandler handler) throws IOException {
    return handler.readItemField(ObjectUtils.requireNonNull(parent, "parent"), this);
  }

  @Override
  default void writeItem(Object item, IItemWriteHandler handler) throws IOException {
    handler.writeItemField(item, this);
  }

  @Override
  default <CONTEXT, RESULT> RESULT accept(IModelElementVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return IBoundInstanceModelField.super.accept(visitor, context);
  }
}
