/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAssemblySupport;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelField;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelNamed;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.Ignore;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

class AssemblyModelContainerSupport
    implements IContainerModelAssemblySupport<
        IBoundInstanceModel<?>,
        IBoundInstanceModelNamed<?>,
        IBoundInstanceModelField<?>,
        IBoundInstanceModelAssembly,
        IChoiceInstance,
        IBoundInstanceModelChoiceGroup> {
  @NonNull
  private final List<IBoundInstanceModel<?>> modelInstances;
  @NonNull
  private final Map<IEnhancedQName, IBoundInstanceModelNamed<?>> namedModelInstances;
  @NonNull
  private final Map<IEnhancedQName, IBoundInstanceModelField<?>> fieldInstances;
  @NonNull
  private final Map<IEnhancedQName, IBoundInstanceModelAssembly> assemblyInstances;
  @NonNull
  private final Map<String, IBoundInstanceModelChoiceGroup> choiceGroupInstances;

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public AssemblyModelContainerSupport(
      @NonNull DefinitionAssembly containingDefinition) {
    this.modelInstances = CollectionUtil.unmodifiableList(ObjectUtils.notNull(
        getModelInstanceStream(containingDefinition, containingDefinition.getBoundClass())
            .collect(Collectors.toUnmodifiableList())));

    Map<IEnhancedQName, IBoundInstanceModelNamed<?>> namedModelInstances = new LinkedHashMap<>();
    Map<IEnhancedQName, IBoundInstanceModelField<?>> fieldInstances = new LinkedHashMap<>();
    Map<IEnhancedQName, IBoundInstanceModelAssembly> assemblyInstances = new LinkedHashMap<>();
    Map<String, IBoundInstanceModelChoiceGroup> choiceGroupInstances = new LinkedHashMap<>();
    for (IBoundInstanceModel<?> instance : this.modelInstances) {
      if (instance instanceof IBoundInstanceModelNamed) {
        IBoundInstanceModelNamed<?> named = (IBoundInstanceModelNamed<?>) instance;
        IEnhancedQName key = named.getQName();
        namedModelInstances.put(key, named);

        if (instance instanceof IBoundInstanceModelField) {
          fieldInstances.put(key, (IBoundInstanceModelField<?>) named);
        } else if (instance instanceof IBoundInstanceModelAssembly) {
          assemblyInstances.put(key, (IBoundInstanceModelAssembly) named);
        }
      } else if (instance instanceof IBoundInstanceModelChoiceGroup) {
        IBoundInstanceModelChoiceGroup choiceGroup = (IBoundInstanceModelChoiceGroup) instance;
        String key = ObjectUtils.requireNonNull(choiceGroup.getGroupAsName());
        choiceGroupInstances.put(key, choiceGroup);
      }
    }

    this.namedModelInstances = namedModelInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(namedModelInstances);
    this.fieldInstances = fieldInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(fieldInstances);
    this.assemblyInstances = assemblyInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(assemblyInstances);
    this.choiceGroupInstances = choiceGroupInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(choiceGroupInstances);
  }

  protected static IBoundInstanceModel<?> newBoundModelInstance(
      @NonNull Field field,
      @NonNull IBoundDefinitionModelAssembly definition) {
    IBoundInstanceModel<?> retval = null;
    if (field.isAnnotationPresent(BoundAssembly.class)) {
      retval = IBoundInstanceModelAssembly.newInstance(field, definition);
    } else if (field.isAnnotationPresent(BoundField.class)) {
      retval = IBoundInstanceModelField.newInstance(field, definition);
    } else if (field.isAnnotationPresent(BoundChoiceGroup.class)) {
      retval = IBoundInstanceModelChoiceGroup.newInstance(field, definition);
    }
    return retval;
  }

  @NonNull
  protected static Stream<IBoundInstanceModel<?>> getModelInstanceStream(
      @NonNull IBoundDefinitionModelAssembly definition,
      @NonNull Class<?> clazz) {

    Stream<IBoundInstanceModel<?>> superInstances;
    Class<?> superClass = clazz.getSuperclass();
    if (superClass == null) {
      superInstances = Stream.empty();
    } else {
      // get instances from superclass
      superInstances = getModelInstanceStream(definition, superClass);
    }

    return ObjectUtils.notNull(Stream.concat(superInstances, Arrays.stream(clazz.getDeclaredFields())
        // skip this field, since it is ignored
        .filter(field -> !field.isAnnotationPresent(Ignore.class))
        // skip fields that aren't a Module field or assembly instance
        .filter(field -> field.isAnnotationPresent(BoundField.class)
            || field.isAnnotationPresent(BoundAssembly.class)
            || field.isAnnotationPresent(BoundChoiceGroup.class))
        .map(field -> {
          assert field != null;

          IBoundInstanceModel<?> retval = newBoundModelInstance(field, definition);
          if (retval == null) {
            throw new IllegalStateException(
                String.format("The field '%s' on class '%s' is not bound", field.getName(), clazz.getName()));
          }
          return retval;
        })
        .filter(Objects::nonNull)));
  }

  @Override
  public Collection<IBoundInstanceModel<?>> getModelInstances() {
    return modelInstances;
  }

  @Override
  public Map<IEnhancedQName, IBoundInstanceModelNamed<?>> getNamedModelInstanceMap() {
    return namedModelInstances;
  }

  @Override
  public Map<IEnhancedQName, IBoundInstanceModelField<?>> getFieldInstanceMap() {
    return fieldInstances;
  }

  @Override
  public Map<IEnhancedQName, IBoundInstanceModelAssembly> getAssemblyInstanceMap() {
    return assemblyInstances;
  }

  @Override
  public List<IChoiceInstance> getChoiceInstances() {
    // not supported
    return CollectionUtil.emptyList();
  }

  @Override
  public Map<String, IBoundInstanceModelChoiceGroup> getChoiceGroupInstanceMap() {
    return choiceGroupInstances;
  }
}
