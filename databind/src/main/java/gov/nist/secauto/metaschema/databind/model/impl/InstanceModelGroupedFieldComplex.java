/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.AbstractFieldInstance;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedField;
import gov.nist.secauto.metaschema.databind.model.IBoundProperty;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedField;
import gov.nist.secauto.metaschema.databind.model.annotations.ModelUtil;

import java.util.Map;
import java.util.function.Predicate;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public class InstanceModelGroupedFieldComplex
    extends AbstractFieldInstance<
        IBoundInstanceModelChoiceGroup,
        IBoundDefinitionModelFieldComplex,
        IBoundInstanceModelGroupedField,
        IBoundDefinitionModelAssembly>

    // extends AbstractBoundInstanceModelGroupedNamed<BoundGroupedField>
    implements IBoundInstanceModelGroupedField {
  @NonNull
  private final BoundGroupedField annotation;
  @NonNull
  private final DefinitionField definition;
  @NonNull
  private final Lazy<Map<String, IBoundProperty<?>>> jsonProperties;

  public InstanceModelGroupedFieldComplex(
      @NonNull BoundGroupedField annotation,
      @NonNull DefinitionField definition,
      @NonNull IBoundInstanceModelChoiceGroup container) {
    super(container);
    this.annotation = annotation;
    this.definition = definition;
    this.jsonProperties = ObjectUtils.notNull(Lazy.lazy(() -> {
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
