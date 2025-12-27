/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.IModelConstraintsBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.ITargetedConstraintBase;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaAssembly(
    name = "assembly-constraints",
    moduleClass = MetaschemaModelModule.class)
public class AssemblyConstraints implements IBoundObject, IModelConstraintsBase {
  private final IMetaschemaData __metaschemaData;

  @BoundAssembly(
      formalName = "Constraint Let Expression",
      useName = "let",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "lets", inJson = JsonGroupAsBehavior.LIST))
  private List<ConstraintLetExpression> _lets;

  @BoundChoiceGroup(
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "rules", inJson = JsonGroupAsBehavior.LIST),
      assemblies = {
          @BoundGroupedAssembly(formalName = "Allowed Values Constraint", useName = "allowed-values",
              binding = TargetedAllowedValuesConstraint.class),
          @BoundGroupedAssembly(formalName = "Expect Condition Constraint", useName = "expect",
              binding = TargetedExpectConstraint.class),
          @BoundGroupedAssembly(formalName = "Targeted Index Has Key Constraint", useName = "index-has-key",
              binding = TargetedIndexHasKeyConstraint.class),
          @BoundGroupedAssembly(formalName = "Value Matches Constraint", useName = "matches",
              binding = TargetedMatchesConstraint.class),
          @BoundGroupedAssembly(formalName = "Targeted Unique Constraint", useName = "is-unique",
              binding = TargetedIsUniqueConstraint.class),
          @BoundGroupedAssembly(formalName = "Targeted Index Constraint", useName = "index",
              binding = TargetedIndexConstraint.class),
          @BoundGroupedAssembly(formalName = "Targeted Cardinality Constraint", useName = "has-cardinality",
              binding = TargetedHasCardinalityConstraint.class),
          @BoundGroupedAssembly(formalName = "Report Condition Constraint", useName = "report",
              binding = TargetedReportConstraint.class)
      })
  private List<? extends ITargetedConstraintBase> _rules;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyConstraints}
   * instance with no metadata.
   */
  public AssemblyConstraints() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyConstraints}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public AssemblyConstraints(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the constraint Let Expression.
   *
   * @return the let value
   */
  @NonNull
  public List<ConstraintLetExpression> getLets() {
    if (_lets == null) {
      _lets = new LinkedList<>();
    }
    return _lets;
  }

  /**
   * Set the constraint Let Expression.
   *
   * @param value
   *          the let value to set
   */
  public void setLets(@NonNull List<ConstraintLetExpression> value) {
    _lets = value;
  }

  /**
   * Add a new {@link ConstraintLetExpression} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addLet(ConstraintLetExpression item) {
    ConstraintLetExpression value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_lets == null) {
      _lets = new LinkedList<>();
    }
    return _lets.add(value);
  }

  /**
   * Remove the first matching {@link ConstraintLetExpression} item from the
   * underlying collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeLet(ConstraintLetExpression item) {
    ConstraintLetExpression value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _lets != null && _lets.remove(value);
  }

  /**
   * Get the {@code rules} choice group items.
   *
   * <p>
   * Items in this collection implement {@link ITargetedConstraintBase}.
   *
   * @return the rules items
   */
  @NonNull
  public List<? extends ITargetedConstraintBase> getRules() {
    if (_rules == null) {
      _rules = new LinkedList<>();
    }
    return _rules;
  }

  /**
   * Set the {@code rules} choice group items.
   *
   * <p>
   * Items in this collection must implement {@link ITargetedConstraintBase}.
   *
   * @param value
   *          the rules items to set
   */
  public void setRules(@NonNull List<? extends ITargetedConstraintBase> value) {
    _rules = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
