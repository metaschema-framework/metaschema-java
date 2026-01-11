/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.model.AbstractFieldInstance;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import dev.metaschema.databind.model.IBoundInstanceFlag;
import dev.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedField;
import dev.metaschema.databind.model.IBoundProperty;
import dev.metaschema.databind.model.annotations.BoundGroupedField;
import dev.metaschema.databind.model.annotations.ModelUtil;
import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Represents an field model instance that is a member of a choice group
 * instance.
 */
/**
 * Implementation of a complex field instance within a choice group.
 * <p>
 * This class represents a complex field that is a member of a choice group,
 * allowing polymorphic model content.
 */
public class InstanceModelGroupedFieldComplex
    extends AbstractFieldInstance<
        IBoundInstanceModelChoiceGroup,
        IBoundDefinitionModelFieldComplex,
        IBoundInstanceModelGroupedField,
        IBoundDefinitionModelAssembly>
    implements IBoundInstanceModelGroupedField {
  @NonNull
  private final BoundGroupedField annotation;
  @NonNull
  private final DefinitionField definition;
  @NonNull
  private final Lazy<Map<String, IBoundProperty<?>>> jsonProperties;
  @NonNull
  private final Lazy<Map<IAttributable.Key, Set<String>>> properties;

  /**
   * Construct a new field model instance instance that is a member of a choice
   * group instance.
   *
   * @param annotation
   *          the Java annotation the instance is bound to
   * @param definition
   *          the assembly definition this instance is bound to
   * @param container
   *          the choice group instance containing the instance
   */
  public InstanceModelGroupedFieldComplex(
      @NonNull BoundGroupedField annotation,
      @NonNull DefinitionField definition,
      @NonNull IBoundInstanceModelChoiceGroup container) {
    super(container);
    this.annotation = annotation;
    this.definition = definition;
    this.jsonProperties = ObjectUtils.notNull(Lazy.of(() -> {
      Predicate<IBoundInstanceFlag> flagFilter = null;
      IBoundInstanceFlag jsonKey = getEffectiveJsonKey();
      if (jsonKey != null) {
        flagFilter = flag -> !jsonKey.equals(flag);
      }

      IBoundInstanceFlag jsonValueKey = getDefinition().getJsonValueKeyFlagInstance();
      if (jsonValueKey != null) {
        Predicate<IBoundInstanceFlag> jsonValueKeyFilter = flag -> !flag.equals(jsonValueKey);
        flagFilter = flagFilter == null ? jsonValueKeyFilter : flagFilter.and(jsonValueKeyFilter);
      }
      return getDefinition().getJsonProperties(flagFilter);
    }));
    this.properties = ObjectUtils.notNull(
        Lazy.of(() -> CollectionUtil.unmodifiableMap(ObjectUtils.notNull(
            Arrays.stream(annotation.properties())
                .map(ModelUtil::toPropertyEntry)
                .collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new))))));
  }

  private BoundGroupedField getAnnotation() {
    return annotation;
  }

  // ------------------------------------------
  // - Start annotation driven code - CPD-OFF -
  // ------------------------------------------

  @Override
  public Class<? extends IBoundObject> getBoundClass() {
    return getAnnotation().binding();
  }

  @Override
  public Map<String, IBoundProperty<?>> getJsonProperties() {
    return ObjectUtils.notNull(jsonProperties.get());
  }

  @Override
  public DefinitionField getDefinition() {
    return definition;
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
  public Map<Key, Set<String>> getProperties() {
    return ObjectUtils.notNull(properties.get());
  }

  @Override
  public MarkupMultiline getRemarks() {
    return ModelUtil.resolveToMarkupMultiline(getAnnotation().remarks());
  }

  @Override
  public String getDiscriminatorValue() {
    return ModelUtil.resolveNoneOrValue(getAnnotation().formalName());
  }

  @Override
  public String getUseName() {
    return ModelUtil.resolveNoneOrValue(getAnnotation().useName());
  }

  @Override
  public Integer getUseIndex() {
    return ModelUtil.resolveDefaultInteger(getAnnotation().useIndex());
  }

  // ----------------------------------------
  // - End annotation driven code - CPD-OFF -
  // ----------------------------------------
}
