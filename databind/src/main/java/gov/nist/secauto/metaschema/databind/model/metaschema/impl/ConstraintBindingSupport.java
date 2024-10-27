/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.AbstractConfigurableMessageConstraintBuilder;
import gov.nist.secauto.metaschema.core.model.constraint.AbstractConstraintBuilder;
import gov.nist.secauto.metaschema.core.model.constraint.AbstractKeyConstraintBuilder;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.ICardinalityConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IIndexConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IKeyField;
import gov.nist.secauto.metaschema.core.model.constraint.ILet;
import gov.nist.secauto.metaschema.core.model.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IModelConstrained;
import gov.nist.secauto.metaschema.core.model.constraint.IUniqueConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IValueConstrained;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.metaschema.IConfigurableMessageConstraintBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.IConstraintBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.IModelConstraintsBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.IValueConstraintsBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.IValueTargetedConstraintsBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.ConstraintValueEnum;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.FlagAllowedValues;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.FlagExpect;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.FlagIndexHasKey;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.FlagMatches;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.KeyConstraintField;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.Property;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.Remarks;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedAllowedValuesConstraint;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedExpectConstraint;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedHasCardinalityConstraint;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedIndexConstraint;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedIsUniqueConstraint;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedMatchesConstraint;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Supports parsing constraints declared within a bound object.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public final class ConstraintBindingSupport {
  private ConstraintBindingSupport() {
    // disable construction
  }

  /**
   * Parse a constraint set.
   *
   * @param constraintSet
   *          the parsed constraint set
   * @param constraints
   *          the constraint definitions to parse
   * @param source
   *          the source of the constraints
   */
  public static void parse(
      @NonNull IValueConstrained constraintSet,
      @NonNull IValueConstraintsBase constraints,
      @NonNull ISource source) {
    parseLet(constraintSet, constraints, source);

    // parse rules
    for (IConstraintBase ruleObj : constraints.getRules()) {
      if (ruleObj instanceof FlagAllowedValues) {
        IAllowedValuesConstraint constraint = newAllowedValues((FlagAllowedValues) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof FlagExpect) {
        IExpectConstraint constraint = newExpect((FlagExpect) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof FlagIndexHasKey) {
        IIndexHasKeyConstraint constraint = newIndexHasKey((FlagIndexHasKey) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof FlagMatches) {
        IMatchesConstraint constraint = newMatches((FlagMatches) ruleObj, source);
        constraintSet.addConstraint(constraint);
      }
    }
  }

  /**
   * Parse a constraint set.
   *
   * @param constraintSet
   *          the parsed constraint set
   * @param constraints
   *          the constraint definitions to parse
   * @param source
   *          the source of the constraints
   */
  public static void parse(
      @NonNull IValueConstrained constraintSet,
      @NonNull IValueTargetedConstraintsBase constraints,
      @NonNull ISource source) {
    parseLet(constraintSet, constraints, source);

    // parse rules
    for (IConstraintBase ruleObj : constraints.getRules()) {
      if (ruleObj instanceof TargetedAllowedValuesConstraint) {
        IAllowedValuesConstraint constraint = newAllowedValues((TargetedAllowedValuesConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof TargetedExpectConstraint) {
        IExpectConstraint constraint = newExpect((TargetedExpectConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof TargetedIndexHasKeyConstraint) {
        IIndexHasKeyConstraint constraint = newIndexHasKey((TargetedIndexHasKeyConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof TargetedMatchesConstraint) {
        IMatchesConstraint constraint = newMatches((TargetedMatchesConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      }
    }
  }

  /**
   * Parse a constraint set.
   *
   * @param constraintSet
   *          the parsed constraint set
   * @param constraints
   *          the constraint definitions to parse
   * @param source
   *          the source of the constraints
   */
  public static void parse(
      @NonNull IModelConstrained constraintSet,
      @NonNull IModelConstraintsBase constraints,
      @NonNull ISource source) {
    parseLet(constraintSet, constraints, source);

    // parse rules
    for (IConstraintBase ruleObj : constraints.getRules()) {
      if (ruleObj instanceof TargetedAllowedValuesConstraint) {
        IAllowedValuesConstraint constraint = newAllowedValues((TargetedAllowedValuesConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof TargetedExpectConstraint) {
        IExpectConstraint constraint = newExpect((TargetedExpectConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof TargetedIndexHasKeyConstraint) {
        IIndexHasKeyConstraint constraint = newIndexHasKey((TargetedIndexHasKeyConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof TargetedMatchesConstraint) {
        IMatchesConstraint constraint = newMatches((TargetedMatchesConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof TargetedIndexConstraint) {
        IIndexConstraint constraint = newIndex((TargetedIndexConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof TargetedHasCardinalityConstraint) {
        ICardinalityConstraint constraint = newHasCardinality((TargetedHasCardinalityConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      } else if (ruleObj instanceof TargetedIsUniqueConstraint) {
        IUniqueConstraint constraint = newUnique((TargetedIsUniqueConstraint) ruleObj, source);
        constraintSet.addConstraint(constraint);
      }
    }
  }

  /**
   * Parse the let clause in a constraint set.
   *
   * @param constraintSet
   *          the parsed constraint set
   * @param constraints
   *          the constraint definitions to parse
   * @param source
   *          the source of the constraint
   */
  public static void parseLet(
      @NonNull IValueConstrained constraintSet,
      @NonNull IValueConstraintsBase constraints,
      @NonNull ISource source) {
    // parse let expressions
    constraints.getLets().stream()
        .map(letObj -> {
          MarkupMultiline remarks = null;
          Remarks remarkObj = letObj.getRemarks();
          if (remarkObj != null) {
            remarks = remarkObj.getRemark();
          }

          return ILet.of(
              ObjectUtils.requireNonNull(new QName(letObj.getVar())),
              ObjectUtils.requireNonNull(letObj.getExpression()),
              source,
              remarks);
        })
        .forEachOrdered(constraintSet::addLetExpression);
  }

  @NonNull
  private static IAllowedValuesConstraint newAllowedValues(
      @NonNull FlagAllowedValues obj,
      @NonNull ISource source) {
    IAllowedValuesConstraint.Builder builder = IAllowedValuesConstraint.builder()
        .allowsOther(ModelSupport.yesOrNo(obj.getAllowOther()))
        .extensible(extensible(obj.getExtensible()));
    applyCommonValues(obj, null, source, builder);

    for (ConstraintValueEnum value : ObjectUtils.requireNonNull(obj.getEnums())) {
      builder.allowedValue(ObjectUtils.requireNonNull(value));
    }
    return builder.build();
  }

  @NonNull
  private static IAllowedValuesConstraint newAllowedValues(
      @NonNull TargetedAllowedValuesConstraint obj,
      @NonNull ISource source) {
    IAllowedValuesConstraint.Builder builder = IAllowedValuesConstraint.builder()
        .allowsOther(ModelSupport.yesOrNo(obj.getAllowOther()))
        .extensible(extensible(ObjectUtils.requireNonNull(obj.getExtensible())));
    applyCommonValues(obj, obj.getTarget(), source, builder);

    for (ConstraintValueEnum value : ObjectUtils.requireNonNull(obj.getEnums())) {
      builder.allowedValue(ObjectUtils.requireNonNull(value));
    }
    return builder.build();
  }

  @NonNull
  private static IExpectConstraint newExpect(
      @NonNull FlagExpect obj,
      @NonNull ISource source) {
    IExpectConstraint.Builder builder = IExpectConstraint.builder()
        .test(target(ObjectUtils.requireNonNull(obj.getTest())));
    applyConfigurableCommonValues(obj, null, source, builder);

    String message = obj.getMessage();
    if (message != null) {
      builder.message(message);
    }

    return builder.build();
  }

  @NonNull
  private static IExpectConstraint newExpect(
      @NonNull TargetedExpectConstraint obj,
      @NonNull ISource source) {
    IExpectConstraint.Builder builder = IExpectConstraint.builder()
        .test(target(ObjectUtils.requireNonNull(obj.getTest())));
    applyConfigurableCommonValues(obj, obj.getTarget(), source, builder);

    return builder.build();
  }

  @NonNull
  private static <T extends AbstractKeyConstraintBuilder<T, ?>> T handleKeyConstraints(
      @NonNull List<KeyConstraintField> keys,
      @NonNull T builder,
      @NonNull ISource source) {
    for (KeyConstraintField value : keys) {
      assert value != null;

      IKeyField keyField = IKeyField.of(
          target(ObjectUtils.requireNonNull(value.getTarget())),
          pattern(value.getPattern()),
          ModelSupport.remarks(value.getRemarks()),
          source);
      builder.keyField(keyField);
    }
    return builder;
  }

  @NonNull
  private static IIndexHasKeyConstraint newIndexHasKey(
      @NonNull FlagIndexHasKey obj,
      @NonNull ISource source) {
    IIndexHasKeyConstraint.Builder builder = IIndexHasKeyConstraint.builder(ObjectUtils.requireNonNull(obj.getName()));
    applyConfigurableCommonValues(obj, null, source, builder);
    handleKeyConstraints(ObjectUtils.requireNonNull(obj.getKeyFields()), builder, source);
    return builder.build();
  }

  @NonNull
  private static IIndexHasKeyConstraint newIndexHasKey(
      @NonNull TargetedIndexHasKeyConstraint obj,
      @NonNull ISource source) {
    IIndexHasKeyConstraint.Builder builder = IIndexHasKeyConstraint.builder(ObjectUtils.requireNonNull(obj.getName()));
    applyConfigurableCommonValues(obj, obj.getTarget(), source, builder);
    handleKeyConstraints(ObjectUtils.requireNonNull(obj.getKeyFields()), builder, source);
    return builder.build();
  }

  @NonNull
  private static IMatchesConstraint newMatches(
      @NonNull FlagMatches obj,
      @NonNull ISource source) {
    IMatchesConstraint.Builder builder = IMatchesConstraint.builder();
    applyConfigurableCommonValues(obj, null, source, builder);

    Pattern regex = pattern(obj.getRegex());
    if (regex != null) {
      builder.regex(regex);
    }

    String dataType = obj.getDatatype();
    if (dataType != null) {
      IDataTypeAdapter<?> javaTypeAdapter = ModelSupport.dataType(obj.getDatatype());
      builder.datatype(javaTypeAdapter);
    }

    return builder.build();
  }

  @NonNull
  private static IMatchesConstraint newMatches(
      @NonNull TargetedMatchesConstraint obj,
      @NonNull ISource source) {
    IMatchesConstraint.Builder builder = IMatchesConstraint.builder();
    applyConfigurableCommonValues(obj, obj.getTarget(), source, builder);

    Pattern regex = pattern(obj.getRegex());
    if (regex != null) {
      builder.regex(regex);
    }

    String dataType = obj.getDatatype();
    if (dataType != null) {
      IDataTypeAdapter<?> javaTypeAdapter = ModelSupport.dataType(obj.getDatatype());
      builder.datatype(javaTypeAdapter);
    }

    return builder.build();
  }

  @NonNull
  private static IIndexConstraint newIndex(
      @NonNull TargetedIndexConstraint obj,
      @NonNull ISource source) {
    IIndexConstraint.Builder builder = IIndexConstraint.builder(ObjectUtils.requireNonNull(obj.getName()));
    applyConfigurableCommonValues(obj, obj.getTarget(), source, builder);
    handleKeyConstraints(ObjectUtils.requireNonNull(obj.getKeyFields()), builder, source);

    return builder.build();
  }

  @NonNull
  private static ICardinalityConstraint newHasCardinality(
      @NonNull TargetedHasCardinalityConstraint obj,
      @NonNull ISource source) {
    ICardinalityConstraint.Builder builder = ICardinalityConstraint.builder();
    applyConfigurableCommonValues(obj, obj.getTarget(), source, builder);

    BigInteger minOccurs = obj.getMinOccurs();
    if (minOccurs != null) {
      builder.minOccurs(minOccurs.intValueExact());
    }
    String maxOccurs = obj.getMaxOccurs();
    if (maxOccurs != null) {
      int occurance = ModelSupport.maxOccurs(maxOccurs);
      builder.maxOccurs(occurance);
    }

    return builder.build();
  }

  @NonNull
  private static IUniqueConstraint newUnique(
      @NonNull TargetedIsUniqueConstraint obj,
      @NonNull ISource source) {
    IUniqueConstraint.Builder builder = IUniqueConstraint.builder();
    applyConfigurableCommonValues(obj, obj.getTarget(), source, builder);
    handleKeyConstraints(ObjectUtils.requireNonNull(obj.getKeyFields()), builder, source);

    return builder.build();
  }

  @NonNull
  private static <T extends AbstractConfigurableMessageConstraintBuilder<T, ?>> T applyConfigurableCommonValues(
      @NonNull IConfigurableMessageConstraintBase constraint,
      @Nullable String target,
      @NonNull ISource source,
      @NonNull T builder) {
    applyCommonValues(constraint, target, source, builder);

    String message = constraint.getMessage();
    if (message != null) {
      builder.message(message);
    }
    return builder;
  }

  @NonNull
  private static <T extends AbstractConstraintBuilder<T, ?>> T applyCommonValues(
      @NonNull IConstraintBase constraint,
      @Nullable String target,
      @NonNull ISource source,
      @NonNull T builder) {

    String id = constraint.getId();

    if (id != null) {
      builder.identifier(id);
    }

    String formalName = constraint.getFormalName();
    if (formalName != null) {
      builder.formalName(formalName);
    }

    MarkupLine description = constraint.getDescription();
    if (description != null) {
      builder.description(description);
    }

    List<Property> props = ObjectUtils.requireNonNull(constraint.getProps());
    builder.properties(ModelSupport.parseProperties(props));

    Remarks remarks = constraint.getRemarks();
    if (remarks != null) {
      builder.remarks(ObjectUtils.notNull(remarks.getRemark()));
    }

    builder.target(target(target));
    builder.level(level(constraint.getLevel()));
    builder.source(source);
    return builder;
  }

  @NonNull
  private static String target(@Nullable String target) {
    return target == null
        ? IConstraint.DEFAULT_TARGET_METAPATH
        : target;
  }

  @NonNull
  private static IConstraint.Level level(@Nullable String level) {
    IConstraint.Level retval = IConstraint.DEFAULT_LEVEL;
    if (level != null) {
      switch (level) {
      case "CRITICAL":
        retval = IConstraint.Level.CRITICAL;
        break;
      case "ERROR":
        retval = IConstraint.Level.ERROR;
        break;
      case "WARNING":
        retval = IConstraint.Level.WARNING;
        break;
      case "INFORMATIONAL":
        retval = IConstraint.Level.INFORMATIONAL;
        break;
      case "DEBUG":
        retval = IConstraint.Level.DEBUG;
        break;
      default:
        throw new UnsupportedOperationException(level);
      }
    }
    return retval;
  }

  @NonNull
  private static IAllowedValuesConstraint.Extensible extensible(@Nullable String extensible) {
    IAllowedValuesConstraint.Extensible retval = IAllowedValuesConstraint.EXTENSIBLE_DEFAULT;
    if (extensible != null) {
      switch (extensible) {
      case "model":
        retval = IAllowedValuesConstraint.Extensible.MODEL;
        break;
      case "external":
        retval = IAllowedValuesConstraint.Extensible.EXTERNAL;
        break;
      case "none":
        retval = IAllowedValuesConstraint.Extensible.NONE;
        break;
      default:
        throw new UnsupportedOperationException(extensible);
      }
    }
    return retval;
  }

  @Nullable
  private static Pattern pattern(@Nullable String pattern) {
    return pattern == null ? null : Pattern.compile(pattern);
  }

}
