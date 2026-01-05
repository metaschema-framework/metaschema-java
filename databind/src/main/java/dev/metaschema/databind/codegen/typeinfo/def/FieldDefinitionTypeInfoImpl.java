/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo.def;

import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IInstance;
import dev.metaschema.core.util.CustomCollectors;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.typeinfo.IFieldValueTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.IFlagInstanceTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.IInstanceTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.IPropertyTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.ITypeResolver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

class FieldDefinitionTypeInfoImpl
    extends AbstractModelDefinitionTypeInfo<IFieldDefinition>
    implements IFieldDefinitionTypeInfo {
  private static final Logger LOGGER = LogManager.getLogger(FieldDefinitionTypeInfoImpl.class);

  @NonNull
  private final Lazy<Map<String, IPropertyTypeInfo>> propertyNameToTypeInfoMap;
  @NonNull
  private final Lazy<Map<IInstance, IInstanceTypeInfo>> instanceToTypeInfoMap;

  public FieldDefinitionTypeInfoImpl(@NonNull IFieldDefinition definition, @NonNull ITypeResolver typeResolver) {
    super(definition, typeResolver);
    this.instanceToTypeInfoMap = ObjectUtils.notNull(Lazy.of(() -> getFlagInstanceTypeInfos().stream()
        .collect(CustomCollectors.toMap(
            IFlagInstanceTypeInfo::getInstance,
            CustomCollectors.identity(),
            (key, v1, v2) -> {
              if (LOGGER.isErrorEnabled()) {
                LOGGER.error(String.format("Unexpected duplicate property name '%s'", key));
              }
              return ObjectUtils.notNull(v2);
            },
            LinkedHashMap::new))));
    this.propertyNameToTypeInfoMap = ObjectUtils.notNull(Lazy.of(() -> Stream.concat(
        getInstanceTypeInfoMap().values().stream(),
        Stream.of((IPropertyTypeInfo) IFieldValueTypeInfo.newTypeInfo(this)))
        .collect(Collectors.toMap(
            IPropertyTypeInfo::getPropertyName,
            Function.identity(),
            (v1, v2) -> v2,
            LinkedHashMap::new))));
  }

  @Override
  protected Map<String, IPropertyTypeInfo> getPropertyTypeInfoMap() {
    return ObjectUtils.notNull(propertyNameToTypeInfoMap.get());
  }

  @Override
  protected Map<IInstance, IInstanceTypeInfo> getInstanceTypeInfoMap() {
    return ObjectUtils.notNull(instanceToTypeInfoMap.get());
  }
}
