/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.model.AbstractAssemblyInstance;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.IBoundInstanceFlag;
import dev.metaschema.databind.model.IBoundInstanceModelAssembly;
import dev.metaschema.databind.model.IBoundModule;
import dev.metaschema.databind.model.IBoundProperty;
import dev.metaschema.databind.model.IGroupAs;
import dev.metaschema.databind.model.annotations.BoundAssembly;
import dev.metaschema.databind.model.annotations.GroupAs;
import dev.metaschema.databind.model.annotations.ModelUtil;
import dev.metaschema.databind.model.info.IModelInstanceCollectionInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implements a Metaschema module assembly instance bound to a Java field,
 * supported by a bound definition class.
 */
/**
 * Implementation of an assembly instance bound to a Java field.
 * <p>
 * This class handles the binding of a field referencing an assembly definition
 * to the containing assembly's model.
 */
public final class InstanceModelAssemblyComplex
    extends AbstractAssemblyInstance<
        IBoundDefinitionModelAssembly,
        IBoundDefinitionModelAssembly,
        IBoundInstanceModelAssembly,
        IBoundDefinitionModelAssembly>
    implements IBoundInstanceModelAssembly, IFeatureInstanceModelGroupAs {
  @NonNull
  private final Field javaField;
  @NonNull
  private final BoundAssembly annotation;
  @NonNull
  private final Lazy<IModelInstanceCollectionInfo<IBoundObject>> collectionInfo;
  @NonNull
  private final IBoundDefinitionModelAssembly definition;
  @NonNull
  private final IGroupAs groupAs;
  @NonNull
  private final Lazy<Map<String, IBoundProperty<?>>> jsonProperties;
  @NonNull
  private final Lazy<Map<IAttributable.Key, Set<String>>> properties;

  /**
   * Construct a new field instance bound to a Java field, supported by a bound
   * definition class.
   *
   * @param javaField
   *          the Java field bound to this instance
   * @param definition
   *          the assembly definition this instance is bound to
   * @param containingDefinition
   *          the definition containing this instance
   * @return the instance
   */
  @NonNull
  public static InstanceModelAssemblyComplex newInstance(
      @NonNull Field javaField,
      @NonNull IBoundDefinitionModelAssembly definition,
      @NonNull IBoundDefinitionModelAssembly containingDefinition) {
    BoundAssembly annotation = ModelUtil.getAnnotation(javaField, BoundAssembly.class);
    IGroupAs groupAs = ModelUtil.resolveDefaultGroupAs(
        annotation.groupAs(),
        containingDefinition.getContainingModule());
    if (annotation.maxOccurs() == -1 || annotation.maxOccurs() > 1) {
      if (IGroupAs.SINGLETON_GROUP_AS.equals(groupAs)) {
        throw new IllegalStateException(String.format("Field '%s' on class '%s' is missing the '%s' annotation.",
            javaField.getName(),
            containingDefinition.getBoundClass().getName(),
            GroupAs.class.getName()));
      }
    } else if (!IGroupAs.SINGLETON_GROUP_AS.equals(groupAs)) {
      // max is 1 and a groupAs is set
      throw new IllegalStateException(
          String.format(
              "Field '%s' on class '%s' has the '%s' annotation, but maxOccurs=1. A groupAs must not be specfied.",
              javaField.getName(),
              containingDefinition.getBoundClass().getName(),
              GroupAs.class.getName()));
    }
    return new InstanceModelAssemblyComplex(javaField, annotation, groupAs, definition, containingDefinition);
  }

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  private InstanceModelAssemblyComplex(
      @NonNull Field javaField,
      @NonNull BoundAssembly annotation,
      @NonNull IGroupAs groupAs,
      @NonNull IBoundDefinitionModelAssembly definition,
      @NonNull IBoundDefinitionModelAssembly containingDefinition) {
    super(containingDefinition);
    FieldSupport.bindField(javaField);
    this.javaField = javaField;
    this.annotation = annotation;
    this.groupAs = groupAs;
    this.collectionInfo = ObjectUtils.notNull(Lazy.of(() -> IModelInstanceCollectionInfo.of(this)));
    this.definition = definition;
    this.jsonProperties = ObjectUtils.notNull(Lazy.of(() -> {
      IBoundInstanceFlag jsonKey = getEffectiveJsonKey();
      Predicate<IBoundInstanceFlag> flagFilter = jsonKey == null ? null : flag -> !jsonKey.equals(flag);
      return getDefinition().getJsonProperties(flagFilter);
    }));
    this.properties = ObjectUtils.notNull(
        Lazy.of(() -> CollectionUtil.unmodifiableMap(ObjectUtils.notNull(
            Arrays.stream(annotation.properties())
                .map(ModelUtil::toPropertyEntry)
                .collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new))))));
  }

  // ------------------------------------------
  // - Start annotation driven code - CPD-OFF -
  // ------------------------------------------

  @Override
  public Field getField() {
    return javaField;
  }

  /**
   * Get the binding Java annotation.
   *
   * @return the binding Java annotation
   */
  @NonNull
  public BoundAssembly getAnnotation() {
    return annotation;
  }

  @SuppressWarnings("null")
  @Override
  public IModelInstanceCollectionInfo<IBoundObject> getCollectionInfo() {
    return collectionInfo.get();
  }

  @Override
  public Map<String, IBoundProperty<?>> getJsonProperties() {
    return ObjectUtils.notNull(jsonProperties.get());
  }

  @Override
  public IBoundDefinitionModelAssembly getDefinition() {
    return definition;
  }

  @Override
  public IBoundModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }

  @Override
  public IGroupAs getGroupAs() {
    return groupAs;
  }

  @Override
  public String getFormalName() {
    return ModelUtil.resolveNoneOrValue(getAnnotation().formalName());
  }

  @Override
  public MarkupLine getDescription() {
    return ModelUtil.resolveToMarkupLine(getAnnotation().description());
  }

  @Override
  public String getUseName() {
    return ModelUtil.resolveNoneOrValue(getAnnotation().useName());
  }

  @Override
  public Integer getUseIndex() {
    int value = getAnnotation().useIndex();
    return value == Integer.MIN_VALUE ? null : value;
  }

  @Override
  public int getMinOccurs() {
    return getAnnotation().minOccurs();
  }

  @Override
  public int getMaxOccurs() {
    return getAnnotation().maxOccurs();
  }

  @Override
  public Map<Key, Set<String>> getProperties() {
    return ObjectUtils.notNull(properties.get());
  }

  @Override
  public MarkupMultiline getRemarks() {
    return ModelUtil.resolveToMarkupMultiline(getAnnotation().remarks());
  }
}
