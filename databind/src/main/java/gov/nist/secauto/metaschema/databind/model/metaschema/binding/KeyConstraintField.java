/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.
package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaAssembly(
    formalName = "Key Constraint",
    name = "key-constraint-field",
    moduleClass = MetaschemaModelModule.class
)
public class KeyConstraintField implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Key Field Value Target",
      name = "target",
      required = true,
      typeAdapter = StringAdapter.class
  )
  private String _target;

  @BoundFlag(
      formalName = "Key Field Value Pattern",
      name = "pattern",
      typeAdapter = StringAdapter.class
  )
  private String _pattern;

  /**
   * Any explanatory or helpful information to be provided about the remarks parent.
   */
  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks"
  )
  private Remarks _remarks;

  /**
   * Constructs a new {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.KeyConstraintField} instance with no metadata.
   */
  public KeyConstraintField() {
    this(null);
  }

  /**
   * Constructs a new {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.KeyConstraintField} instance with the specified metadata.
   *
   * @param data
   *           the metaschema data, or {@code null} if none
   */
  public KeyConstraintField(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the key Field Value Target.
   *
   * @return the target value
   */
  @NonNull
  public String getTarget() {
    return _target;
  }

  /**
   * Set the key Field Value Target.
   *
   * @param value
   *           the target value to set
   */
  public void setTarget(@NonNull String value) {
    _target = value;
  }

  /**
   * Get the key Field Value Pattern.
   *
   * @return the pattern value, or {@code null} if not set
   */
  @Nullable
  public String getPattern() {
    return _pattern;
  }

  /**
   * Set the key Field Value Pattern.
   *
   * @param value
   *           the pattern value to set
   */
  public void setPattern(@Nullable String value) {
    _pattern = value;
  }

  /**
   * Get the remarks.
   *
   * <p>
   * Any explanatory or helpful information to be provided about the remarks parent.
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
   * Any explanatory or helpful information to be provided about the remarks parent.
   *
   * @param value
   *           the remarks value to set
   */
  public void setRemarks(@Nullable Remarks value) {
    _remarks = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
