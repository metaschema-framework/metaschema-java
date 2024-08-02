/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.AbstractInlineFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.constraint.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IValueConstrained;
import gov.nist.secauto.metaschema.core.model.constraint.ValueConstraintSet;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModel;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.JsonFieldValueKeyFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.JsonKey;
import gov.nist.secauto.metaschema.databind.model.annotations.ModelUtil;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;

import java.lang.reflect.Field;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implements a Metaschema module inline flag instance/definition bound to a
 * Java field.
 */
// TODO: implement getProperties()
public class InstanceFlagInline
    extends AbstractInlineFlagDefinition<IBoundDefinitionModel<IBoundObject>, IBoundDefinitionFlag, IBoundInstanceFlag>
    // extends AbstractBoundInstanceJavaField<BoundFlag, IBoundDefinitionModel>
    implements IBoundInstanceFlag {
  @NonNull
  private final Field javaField;
  @NonNull
  private final BoundFlag annotation;
  @NonNull
  private final IDataTypeAdapter<?> javaTypeAdapter;
  @Nullable
  private final Object defaultValue;
  @NonNull
  private final Lazy<IValueConstrained> constraints;

  /**
   * Construct a new inline field instance/definition.
   *
   * @param javaField
   *          the Java field bound to this instance
   * @param containingDefinition
   *          the definition containing this instance
   */
  public InstanceFlagInline(
      @NonNull Field javaField,
      @NonNull IBoundDefinitionModel<IBoundObject> containingDefinition) {
    super(containingDefinition);
    this.javaField = javaField;
    this.annotation = ModelUtil.getAnnotation(javaField, BoundFlag.class);
    Class<? extends IDataTypeAdapter<?>> adapterClass = ObjectUtils.notNull(getAnnotation().typeAdapter());
    this.javaTypeAdapter = ModelUtil.getDataTypeAdapter(
        adapterClass,
        containingDefinition.getBindingContext());
    this.defaultValue = ModelUtil.resolveDefaultValue(getAnnotation().defaultValue(), this.javaTypeAdapter);

    IModule module = containingDefinition.getContainingModule();

    this.constraints = ObjectUtils.notNull(Lazy.lazy(() -> {
      IValueConstrained retval = new ValueConstraintSet();
      ValueConstraints valueAnnotation = getAnnotation().valueConstraints();
      ConstraintSupport.parse(valueAnnotation, ISource.modelSource(module), retval);
      return retval;
    }));
  }

  /**
   * Get the bound Java field.
   *
   * @return the bound Java field
   */
  @Override
  @NonNull
  public Field getField() {
    return javaField;
  }

  /**
   * Get the binding Java annotation.
   *
   * @return the binding Java annotation
   */
  @NonNull
  private BoundFlag getAnnotation() {
    return annotation;
  }

  @Override
  public IBoundModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }

  // ------------------------------------------
  // - Start annotation driven code - CPD-OFF -
  // ------------------------------------------

  @SuppressWarnings("null")
  @Override
  @NonNull
  public IValueConstrained getConstraintSupport() {
    return constraints.get();
  }

  @Override
  public boolean isRequired() {
    return getAnnotation().required();
  }

  @Override
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Override
  public boolean isJsonKey() {
    return getField().isAnnotationPresent(JsonKey.class);
  }

  @Override
  public boolean isJsonValueKey() {
    return getField().isAnnotationPresent(JsonFieldValueKeyFlag.class);
  }

  @Override
  public IDataTypeAdapter<?> getJavaTypeAdapter() {
    return javaTypeAdapter;
  }

  @Override
  @Nullable
  public String getFormalName() {
    return ModelUtil.resolveNoneOrValue(getAnnotation().formalName());
  }

  @Override
  @Nullable
  public MarkupLine getDescription() {
    return ModelUtil.resolveToMarkupLine(getAnnotation().description());
  }

  @Override
  public String getName() {
    return ObjectUtils.notNull(
        Optional.ofNullable(ModelUtil.resolveNoneOrValue(getAnnotation().name())).orElse(getField().getName()));
  }

  @Override
  public Integer getUseIndex() {
    return getAnnotation().useIndex();
  }

  @Override
  @Nullable
  public MarkupMultiline getRemarks() {
    return ModelUtil.resolveToMarkupMultiline(getAnnotation().remarks());
  }

  // ----------------------------------------
  // - End annotation driven code - CPD-OFF -
  // ----------------------------------------
}
