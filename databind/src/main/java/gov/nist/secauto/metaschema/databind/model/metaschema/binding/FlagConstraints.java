/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.IConstraintBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.IValueConstraintsBase;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A binding class for the {@code flag-constraints} definition.
 */
@MetaschemaAssembly(
    name = "flag-constraints",
    moduleClass = MetaschemaModelModule.class)
public class FlagConstraints implements IValueConstraintsBase {
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
              binding = FlagAllowedValues.class),
          @BoundGroupedAssembly(formalName = "Expect Condition Constraint", useName = "expect",
              binding = FlagExpect.class),
          @BoundGroupedAssembly(formalName = "Index Has Key Constraint", useName = "index-has-key",
              binding = FlagIndexHasKey.class),
          @BoundGroupedAssembly(formalName = "Value Matches Constraint", useName = "matches",
              binding = FlagMatches.class),
          @BoundGroupedAssembly(formalName = "Report Condition Constraint", useName = "report",
              binding = FlagReport.class)
      })
  private List<? extends IConstraintBase> _rules;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.FlagConstraints}
   * instance with no metadata.
   */
  public FlagConstraints() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.FlagConstraints}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public FlagConstraints(IMetaschemaData data) {
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
  @Override
  public List<ConstraintLetExpression> getLets() {
    if (_lets == null) {
      _lets = new LinkedList<>();
    }
    return ObjectUtils.notNull(_lets);
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
   * Items in this collection implement {@link IConstraintBase}.
   *
   * @return the rules items
   */
  @NonNull
  @Override
  public List<? extends IConstraintBase> getRules() {
    if (_rules == null) {
      _rules = new LinkedList<>();
    }
    return ObjectUtils.notNull(_rules);
  }

  /**
   * Set the {@code rules} choice group items.
   *
   * <p>
   * Items in this collection must implement {@link IConstraintBase}.
   *
   * @param value
   *          the rules items to set
   */
  public void setRules(@NonNull List<? extends IConstraintBase> value) {
    _rules = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
