/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.model.DefaultAssemblyModelBuilder;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAssemblySupport;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

final class AssemblyModelGenerator {

  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  public static IContainerModelAssemblySupport<
      IBoundInstanceModel<?>,
      IBoundInstanceModelNamed<?>,
      IBoundInstanceModelField<?>,
      IBoundInstanceModelAssembly,
      IChoiceInstance,
      IBoundInstanceModelChoiceGroup> of(@NonNull DefinitionAssembly containingDefinition) {
    DefaultAssemblyModelBuilder<IBoundInstanceModel<?>,
        IBoundInstanceModelNamed<?>,
        IBoundInstanceModelField<?>,
        IBoundInstanceModelAssembly,
        IChoiceInstance,
        IBoundInstanceModelChoiceGroup> builder = new DefaultAssemblyModelBuilder<>();

    List<IBoundInstanceModel<?>> modelInstances = CollectionUtil.unmodifiableList(ObjectUtils.notNull(
        getModelInstanceStream(containingDefinition, containingDefinition.getBoundClass())
            .collect(Collectors.toUnmodifiableList())));

    for (IBoundInstanceModel<?> instance : modelInstances) {
      if (instance instanceof IBoundInstanceModelNamed) {
        IBoundInstanceModelNamed<?> named = (IBoundInstanceModelNamed<?>) instance;
        if (instance instanceof IBoundInstanceModelField) {
          builder.append((IBoundInstanceModelField<?>) named);
        } else if (instance instanceof IBoundInstanceModelAssembly) {
          builder.append((IBoundInstanceModelAssembly) named);
        }
      } else if (instance instanceof IBoundInstanceModelChoiceGroup) {
        IBoundInstanceModelChoiceGroup choiceGroup = (IBoundInstanceModelChoiceGroup) instance;
        builder.append(choiceGroup);
      }
    }
    return builder.buildAssembly();
  }

  private static IBoundInstanceModel<?> newBoundModelInstance(
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
  private static Stream<IBoundInstanceModel<?>> getModelInstanceStream(
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

  private AssemblyModelGenerator() {
    // disable construction
  }
}
