/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a field definition bound to a Java class.
 * <p>
 * This definition is considered "complex", since it is bound to a Java class.
 */
public interface IBoundDefinitionModelFieldComplex
    extends IBoundDefinitionModelField<IBoundObject>, IBoundDefinitionModelComplex {

  // Complex Field Definition Features
  // =================================

  @Override
  @NonNull
  default IBoundDefinitionModelFieldComplex getDefinition() {
    return this;
  }

  @Override
  default Object getDefaultValue() {
    Object retval = null;
    IBoundDefinitionModelFieldComplex definition = getDefinition();
    IBoundFieldValue fieldValue = definition.getFieldValue();

    Object fieldValueDefault = fieldValue.getDefaultValue();
    if (fieldValueDefault != null) {
      retval = definition.newInstance(null);
      fieldValue.setValue(retval, fieldValueDefault);

      // since the field value is non-null, populate the flags
      for (IBoundInstanceFlag flag : definition.getFlagInstances()) {
        Object flagDefault = flag.getResolvedDefaultValue();
        if (flagDefault != null) {
          flag.setValue(retval, flagDefault);
        }
      }
    }
    return retval;
  }

  /**
   * Get the bound field value associated with this field.
   *
   * @return the field's value binding
   */
  @NonNull
  IBoundFieldValue getFieldValue();

  @Override
  @NonNull
  default Object getFieldValue(@NonNull Object item) {
    return ObjectUtils.requireNonNull(getFieldValue().getValue(item));
  }

  @Override
  @NonNull
  default String getJsonValueKeyName() {
    return getFieldValue().getJsonValueKeyName();
  }

  @Override
  @NonNull
  default IDataTypeAdapter<?> getJavaTypeAdapter() {
    return getFieldValue().getJavaTypeAdapter();
  }

  @SuppressWarnings("PMD.NullAssignment")
  @Override
  @NonNull
  default Map<String, IBoundProperty<?>> getJsonProperties(@Nullable Predicate<IBoundInstanceFlag> flagFilter) {
    Predicate<IBoundInstanceFlag> actualFlagFilter = flagFilter;

    IBoundFieldValue fieldValue = getFieldValue();
    IBoundInstanceFlag jsonValueKey = getDefinition().getJsonValueKeyFlagInstance();
    if (jsonValueKey != null) {
      Predicate<IBoundInstanceFlag> jsonValueKeyFilter = flag -> !flag.equals(jsonValueKey);
      actualFlagFilter = actualFlagFilter == null ? jsonValueKeyFilter : actualFlagFilter.and(jsonValueKeyFilter);
      // ensure the field value is omitted too!
      fieldValue = null;
    }

    Stream<? extends IBoundInstanceFlag> flagStream = getFlagInstances().stream();
    if (actualFlagFilter != null) {
      flagStream = flagStream.filter(actualFlagFilter);
    }

    if (fieldValue != null) {
      // determine if we use the field value or not
      Collection<? extends IBoundInstanceFlag> flagInstances = flagStream
          .collect(Collectors.toList());

      if (flagInstances.isEmpty()) {
        // no relevant flags, so this field should expect a scalar value
        fieldValue = null;
      }
      flagStream = flagInstances.stream();
    }

    Stream<? extends IBoundProperty<?>> resultStream = fieldValue == null
        ? flagStream
        : Stream.concat(flagStream, Stream.of(getFieldValue()));

    return ObjectUtils.notNull(resultStream
        .collect(Collectors.toUnmodifiableMap(IBoundProperty::getJsonName, Function.identity())));
  }

  @Override
  @NonNull
  default IBoundObject readItem(IBoundObject parent, IItemReadHandler handler) throws IOException {
    return handler.readItemField(parent, this);
  }

  @Override
  default void writeItem(IBoundObject item, IItemWriteHandler handler) throws IOException {
    handler.writeItemField(item, this);
  }

  @Override
  default boolean canHandleXmlQName(IEnhancedQName qname) {
    // not handled, since not root
    return false;
  }
}
