/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.model.metaschema.binding;

import dev.metaschema.core.datatype.adapter.StringAdapter;
import dev.metaschema.core.datatype.adapter.TokenAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.annotations.BoundField;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Constraint Let Expression.
 */
@MetaschemaAssembly(
    formalName = "Constraint Let Expression",
    name = "constraint-let-expression",
    moduleClass = MetaschemaModelModule.class)
public class ConstraintLetExpression implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Let Variable Name",
      name = "var",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _var;

  @BoundFlag(
      formalName = "Let Value Metapath Expression",
      name = "expression",
      required = true,
      typeAdapter = StringAdapter.class)
  private String _expression;

  /**
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
   */
  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks")
  private Remarks _remarks;

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.ConstraintLetExpression}
   * instance with no metadata.
   */
  public ConstraintLetExpression() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.ConstraintLetExpression}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public ConstraintLetExpression(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the let Variable Name.
   *
   * @return the var value
   */
  @NonNull
  public String getVar() {
    return _var;
  }

  /**
   * Set the let Variable Name.
   *
   * @param value
   *          the var value to set
   */
  public void setVar(@NonNull String value) {
    _var = value;
  }

  /**
   * Get the let Value Metapath Expression.
   *
   * @return the expression value
   */
  @NonNull
  public String getExpression() {
    return _expression;
  }

  /**
   * Set the let Value Metapath Expression.
   *
   * @param value
   *          the expression value to set
   */
  public void setExpression(@NonNull String value) {
    _expression = value;
  }

  /**
   * Get the remarks.
   *
   * <p>
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
   *
   * @return the remarks value, or {@code null} if not set
   */
  @Nullable
  public Remarks getRemarks() {
    return _remarks;
  }

  /**
   * Set the remarks.
   *
   * <p>
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
   *
   * @param value
   *          the remarks value to set, or {@code null} to clear
   */
  public void setRemarks(@Nullable Remarks value) {
    _remarks = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
