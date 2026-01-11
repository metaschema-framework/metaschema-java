/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IFeatureContainerModelAssembly;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.info.IItemReadHandler;
import dev.metaschema.databind.model.info.IItemWriteHandler;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents an assembly definition bound to a Java class.
 */
public interface IBoundDefinitionModelAssembly
    extends IBoundDefinitionModelComplex, IAssemblyDefinition,
    IFeatureContainerModelAssembly<
        IBoundInstanceModel<?>,
        IBoundInstanceModelNamed<?>,
        IBoundInstanceModelField<?>,
        IBoundInstanceModelAssembly,
        IChoiceInstance,
        IBoundInstanceModelChoiceGroup> { // , IBoundContainerModelAssembly

  // Assembly Definition Features
  // ============================
  @Override
  @NonNull
  default IBoundDefinitionModelAssembly getOwningDefinition() {
    return this;
  }

  @Override
  @NonNull
  default IBoundDefinitionModelAssembly getDefinition() {
    return this;
  }

  @Override
  @Nullable
  default IBoundInstanceModelAssembly getInlineInstance() {
    // never inline
    return null;
  }

  @Override
  @NonNull
  default Map<String, IBoundProperty<?>> getJsonProperties(@Nullable Predicate<IBoundInstanceFlag> flagFilter) {
    Stream<? extends IBoundInstanceFlag> flagStream = getFlagInstances().stream();

    if (flagFilter != null) {
      flagStream = flagStream.filter(flagFilter);
    }

    return ObjectUtils.notNull(Stream.concat(flagStream, getModelInstances().stream())
        .collect(Collectors.toUnmodifiableMap(IBoundProperty::getJsonName, Function.identity())));
  }

  @Override
  @NonNull
  default IBoundObject readItem(@Nullable IBoundObject parent, @NonNull IItemReadHandler handler) throws IOException {
    return handler.readItemAssembly(parent, this);
  }

  @Override
  default void writeItem(IBoundObject item, IItemWriteHandler handler) throws IOException {
    handler.writeItemAssembly(item, this);
  }

  @Override
  default boolean canHandleXmlQName(@NonNull IEnhancedQName qname) {
    return qname.equals(getRootQName());
  }
}
