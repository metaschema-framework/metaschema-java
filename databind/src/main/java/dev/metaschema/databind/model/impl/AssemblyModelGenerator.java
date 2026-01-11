/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.metaschema.core.model.DefaultAssemblyModelBuilder;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IContainerModelAssemblySupport;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.IBoundInstanceModel;
import dev.metaschema.databind.model.IBoundInstanceModelAssembly;
import dev.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import dev.metaschema.databind.model.IBoundInstanceModelField;
import dev.metaschema.databind.model.IBoundInstanceModelNamed;
import dev.metaschema.databind.model.annotations.BoundAssembly;
import dev.metaschema.databind.model.annotations.BoundChoice;
import dev.metaschema.databind.model.annotations.BoundChoiceGroup;
import dev.metaschema.databind.model.annotations.BoundField;
import dev.metaschema.databind.model.annotations.Ignore;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Generates assembly model containers for annotation-based bindings.
 */
final class AssemblyModelGenerator {

  /**
   * A custom builder that allows adding choice instances without adding them to
   * the model instances list.
   * <p>
   * For annotation-based bindings, the choice alternatives (fields/assemblies)
   * are already in the model instances list with their {@code @BoundChoice}
   * annotations. The {@link BoundInstanceModelChoice} wrapper is metadata that
   * groups them, but should not be iterated during reading.
   *
   * @param <MI>
   *          the model instance type
   * @param <NMI>
   *          the named model instance type
   * @param <FI>
   *          the field instance type
   * @param <AI>
   *          the assembly instance type
   * @param <CI>
   *          the choice instance type
   * @param <CGI>
   *          the choice group instance type
   */
  private static final class BoundAssemblyModelBuilder<
      MI extends IBoundInstanceModel<?>,
      NMI extends IBoundInstanceModelNamed<?>,
      FI extends IBoundInstanceModelField<?>,
      AI extends IBoundInstanceModelAssembly,
      CI extends IChoiceInstance,
      CGI extends IBoundInstanceModelChoiceGroup>
      extends DefaultAssemblyModelBuilder<MI, NMI, FI, AI, CI, CGI> {

    /**
     * Append a choice instance to the choice instances list only, without adding it
     * to the model instances list.
     * <p>
     * This is used for annotation-based bindings where choice alternatives are
     * already in the model instances list as regular field/assembly instances.
     *
     * @param instance
     *          the choice instance to append
     */
    void appendChoiceOnly(@NonNull CI instance) {
      getChoiceInstances().add(instance);
    }
  }

  @NonNull
  public static IContainerModelAssemblySupport<
      IBoundInstanceModel<?>,
      IBoundInstanceModelNamed<?>,
      IBoundInstanceModelField<?>,
      IBoundInstanceModelAssembly,
      IChoiceInstance,
      IBoundInstanceModelChoiceGroup> of(@NonNull DefinitionAssembly containingDefinition) {
    BoundAssemblyModelBuilder<IBoundInstanceModel<?>,
        IBoundInstanceModelNamed<?>,
        IBoundInstanceModelField<?>,
        IBoundInstanceModelAssembly,
        IChoiceInstance,
        IBoundInstanceModelChoiceGroup> builder = new BoundAssemblyModelBuilder<>();

    List<IBoundInstanceModel<?>> modelInstances = CollectionUtil.unmodifiableList(ObjectUtils.notNull(
        getModelInstanceStream(containingDefinition, containingDefinition.getBoundClass())
            .collect(Collectors.toUnmodifiableList())));

    // Group named instances by @BoundChoice annotation
    Map<String, List<ChoiceInstanceEntry>> choiceGroups = groupByChoiceId(modelInstances);

    // Validate that choice instances are adjacent
    validateChoiceAdjacency(choiceGroups, containingDefinition.getBoundClass());

    // Create choice instances
    Map<String, BoundInstanceModelChoice> choiceInstances = new LinkedHashMap<>();
    for (Map.Entry<String, List<ChoiceInstanceEntry>> entry : choiceGroups.entrySet()) {
      String choiceId = entry.getKey();
      List<IBoundInstanceModelNamed<?>> instances = ObjectUtils.notNull(entry.getValue().stream()
          .map(ChoiceInstanceEntry::getInstance)
          .collect(Collectors.toList()));
      choiceInstances.put(choiceId, new BoundInstanceModelChoice(
          ObjectUtils.notNull(choiceId), containingDefinition, instances));
    }

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

    // Append choice instances to the builder (only to choice list, not model
    // instances)
    for (BoundInstanceModelChoice choice : choiceInstances.values()) {
      assert choice != null;
      builder.appendChoiceOnly(choice);
    }

    return builder.buildAssembly();
  }

  /**
   * Groups named model instances by their {@code @BoundChoice} choiceId.
   *
   * @param modelInstances
   *          the list of model instances
   * @return a map of choiceId to list of instances with their positions
   */
  @NonNull
  private static Map<String, List<ChoiceInstanceEntry>> groupByChoiceId(
      @NonNull List<IBoundInstanceModel<?>> modelInstances) {
    Map<String, List<ChoiceInstanceEntry>> choiceGroups = new LinkedHashMap<>();

    int index = 0;
    for (IBoundInstanceModel<?> instance : modelInstances) {
      if (instance instanceof IBoundInstanceModelNamed) {
        IBoundInstanceModelNamed<?> named = (IBoundInstanceModelNamed<?>) instance;
        Field field = named.getField();
        BoundChoice annotation = field.getAnnotation(BoundChoice.class);
        if (annotation != null) {
          choiceGroups
              .computeIfAbsent(annotation.choiceId(), k -> new ArrayList<>())
              .add(new ChoiceInstanceEntry(index, named));
        }
      }
      index++;
    }

    return choiceGroups;
  }

  /**
   * Validates that all fields with the same choiceId are adjacent (consecutive)
   * in the class declaration.
   *
   * @param choiceGroups
   *          the grouped choice instances
   * @param boundClass
   *          the class being validated (for error messages)
   * @throws IllegalStateException
   *           if choice fields are not adjacent
   */
  private static void validateChoiceAdjacency(
      @NonNull Map<String, List<ChoiceInstanceEntry>> choiceGroups,
      @NonNull Class<?> boundClass) {
    for (Map.Entry<String, List<ChoiceInstanceEntry>> entry : choiceGroups.entrySet()) {
      String choiceId = entry.getKey();
      List<ChoiceInstanceEntry> instances = entry.getValue();

      if (instances.size() > 1) {
        // Check that indices are consecutive
        for (int i = 1; i < instances.size(); i++) {
          int prevIndex = instances.get(i - 1).getIndex();
          int currIndex = instances.get(i).getIndex();
          if (currIndex != prevIndex + 1) {
            throw new IllegalStateException(String.format(
                "Choice fields with choiceId '%s' are not adjacent in class '%s'. "
                    + "All fields in a choice must be declared consecutively.",
                choiceId,
                boundClass.getName()));
          }
        }
      }
    }
  }

  /**
   * An entry representing a named model instance with its position in the model.
   */
  private static final class ChoiceInstanceEntry {
    private final int index;
    @NonNull
    private final IBoundInstanceModelNamed<?> instance;

    ChoiceInstanceEntry(int index, @NonNull IBoundInstanceModelNamed<?> instance) {
      this.index = index;
      this.instance = instance;
    }

    int getIndex() {
      return index;
    }

    @NonNull
    IBoundInstanceModelNamed<?> getInstance() {
      return instance;
    }
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
