/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.binding.metaschema;

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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({
    "PMD.DataClass",
    "PMD.ExcessivePublicCount",
    "PMD.FieldNamingConventions"
})
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
      assemblies = {
          @BoundGroupedAssembly(formalName = "Allowed Values Constraint", useName = "allowed-values",
              binding = FlagAllowedValues.class),
          @BoundGroupedAssembly(formalName = "Expect Condition Constraint", useName = "expect",
              binding = FlagExpect.class),
          @BoundGroupedAssembly(formalName = "Index Has Key Constraint", useName = "index-has-key",
              binding = FlagIndexHasKey.class),
          @BoundGroupedAssembly(formalName = "Value Matches Constraint", useName = "matches",
              binding = FlagMatches.class)
      },
      groupAs = @GroupAs(name = "rules", inJson = JsonGroupAsBehavior.LIST))
  private List<? extends IConstraintBase> _rules;

  public FlagConstraints() {
    this(null);
  }

  public FlagConstraints(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  @Override
  public List<ConstraintLetExpression> getLets() {
    return _lets;
  }

  public void setLets(List<ConstraintLetExpression> value) {
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

  @Override
  public List<? extends IConstraintBase> getRules() {
    return _rules;
  }

  public void setRules(List<? extends IConstraintBase> value) {
    _rules = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
