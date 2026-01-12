/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo.def;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IContainerModelAbsolute;
import dev.metaschema.core.model.IInstance;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.INamedModelInstanceAbsolute;
import dev.metaschema.core.util.CustomCollectors;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.typeinfo.IInstanceTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.IModelInstanceTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.INamedModelInstanceTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.IPropertyTypeInfo;
import dev.metaschema.databind.codegen.typeinfo.ITypeResolver;
import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

class AssemblyDefinitionTypeInfoImpl
    extends AbstractModelDefinitionTypeInfo<IAssemblyDefinition>
    implements IAssemblyDefinitionTypeInfo {
  private static final Logger LOGGER = LogManager.getLogger(AssemblyDefinitionTypeInfoImpl.class);

  @NonNull
  private final Lazy<Map<String, IPropertyTypeInfo>> propertyNameToTypeInfoMap;
  @NonNull
  private final Lazy<Map<IInstance, IInstanceTypeInfo>> instanceToTypeInfoMap;

  private int choiceCounter;

  public AssemblyDefinitionTypeInfoImpl(@NonNull IAssemblyDefinition definition, @NonNull ITypeResolver typeResolver) {
    super(definition, typeResolver);
    this.instanceToTypeInfoMap = ObjectUtils.notNull(Lazy.of(() -> Stream.concat(
        getFlagInstanceTypeInfos().stream(),
        processModel(definition))
        .collect(CustomCollectors.toMap(
            IInstanceTypeInfo::getInstance,
            CustomCollectors.identity(),
            (key, v1, v2) -> {
              if (LOGGER.isErrorEnabled()) {
                LOGGER.error(String.format("Unexpected duplicate property name '%s'", key));
              }
              return ObjectUtils.notNull(v2);
            },
            LinkedHashMap::new))));
    this.propertyNameToTypeInfoMap = ObjectUtils.notNull(Lazy.of(() -> getInstanceTypeInfoMap().values().stream()
        .collect(Collectors.toMap(
            IInstanceTypeInfo::getPropertyName,
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

  private Stream<? extends IModelInstanceTypeInfo> processModel(
      @NonNull IContainerModelAbsolute model) {
    return processModel(model, null);
  }

  /**
   * Process a model container, optionally associating named instances with a
   * choice ID.
   *
   * @param model
   *          the model to process
   * @param choiceId
   *          the choice ID to associate with named instances, or {@code null}
   * @return a stream of model instance type info objects
   */
  @NonNull
  private Stream<? extends IModelInstanceTypeInfo> processModel(
      @NonNull IContainerModelAbsolute model,
      @edu.umd.cs.findbugs.annotations.Nullable String choiceId) {
    Stream<IModelInstanceTypeInfo> modelInstances = Stream.empty();
    // create model instances for the model
    for (IModelInstanceAbsolute instance : model.getModelInstances()) {
      assert instance != null;

      if (instance instanceof IChoiceGroupInstance) {
        modelInstances = Stream.concat(
            modelInstances,
            Stream.of(getTypeResolver().getTypeInfo((IChoiceGroupInstance) instance, this)));
      } else if (instance instanceof IChoiceInstance) {
        // Generate a unique choice ID for this choice instance
        String newChoiceId = "choice-" + (++choiceCounter);
        modelInstances = Stream.concat(
            modelInstances,
            processModel((IChoiceInstance) instance, newChoiceId));
      } else if (instance instanceof INamedModelInstanceAbsolute) {
        // else the instance is an object model instance with a name
        INamedModelInstanceTypeInfo typeInfo = getTypeResolver()
            .getTypeInfo((INamedModelInstanceAbsolute) instance, this);

        // Set the choice ID if this instance is part of a choice
        if (choiceId != null) {
          typeInfo.setChoiceId(choiceId);
        }

        modelInstances = Stream.concat(
            modelInstances,
            Stream.of(typeInfo));
      }
    }
    return ObjectUtils.notNull(modelInstances);
  }
}
