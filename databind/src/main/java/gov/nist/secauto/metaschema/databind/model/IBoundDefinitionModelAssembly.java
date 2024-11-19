/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IFeatureContainerModelAssembly;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  default List<IChoiceInstance> getChoiceInstances() {
    // not supported
    return CollectionUtil.emptyList();
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
