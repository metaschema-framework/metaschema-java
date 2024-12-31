/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A collection of utilities supporting the static and dynamic evaluation of
 * Metapath expressions.
 *
 * @since 1.0.0
 */
public final class ExpressionUtils {
  private ExpressionUtils() {
    // disable
  }

  /**
   * Analyze an expression to determine its static result type.
   *
   * @param <RESULT_TYPE>
   *          the Java type of the analysis result
   * @param baseType
   *          the Java classs for the base type to use for analysis
   * @param expressions
   *          the expressions to analyze
   * @return the static result type
   */
  @NonNull
  public static <RESULT_TYPE> Class<? extends RESULT_TYPE> analyzeStaticResultType(
      @NonNull Class<RESULT_TYPE> baseType,
      @NonNull List<IExpression> expressions) {

    Class<? extends RESULT_TYPE> retval;
    if (expressions.isEmpty()) {
      // no expressions, so use the base type
      retval = baseType;
    } else {
      List<Class<?>> expressionClasses = ObjectUtils.notNull(expressions.stream()
          .map(IExpression::getStaticResultType).collect(Collectors.toList()));

      // check if the expression classes, are derived from the base type
      if (checkDerivedFrom(baseType, expressionClasses)) {
        retval = findCommonBase(baseType, expressionClasses);
      } else {
        retval = baseType;
      }
    }
    return retval;
  }

  @NonNull
  private static <RESULT_TYPE> Class<? extends RESULT_TYPE> findCommonBase(
      @NonNull Class<RESULT_TYPE> baseType,
      @NonNull List<Class<?>> expressionClasses) {
    Class<? extends RESULT_TYPE> retval;
    if (expressionClasses.size() == 1) {
      @SuppressWarnings("unchecked")
      Class<? extends RESULT_TYPE> result
          = (Class<? extends RESULT_TYPE>) expressionClasses.iterator().next();
      assert result != null;
      retval = result;
    } else {
      @SuppressWarnings("unchecked")
      Class<? extends RESULT_TYPE> first
          = (Class<? extends RESULT_TYPE>) expressionClasses.iterator().next();
      assert first != null;
      if (baseType.equals(first)) {
        // the first type is the same as the base, which is the least common type
        retval = baseType;
      } else {
        // search for the least common type
        Class<?> leastCommon = getCommonBaseClass(
            baseType,
            first,
            ObjectUtils.notNull(expressionClasses.subList(1, expressionClasses.size())));
        @SuppressWarnings("unchecked")
        Class<? extends RESULT_TYPE> newBase
            = (Class<? extends RESULT_TYPE>) leastCommon;
        if (newBase != null) {
          retval = newBase;
        } else {
          retval = baseType;
        }
      }
    }
    return retval;
  }

  @Nullable
  private static Class<?> getCommonBaseClass(
      @NonNull Class<?> baseType,
      @NonNull Class<?> first,
      @NonNull List<Class<?>> expressionClasses) {
    boolean match = checkDerivedFrom(first, expressionClasses);

    Class<?> retval = null;
    if (match) {
      retval = first;
    } else {
      for (Class<?> clazz : first.getInterfaces()) {
        assert clazz != null;
        // ensure the new interface is a sublass of the baseType
        if (baseType.isAssignableFrom(clazz)) {
          Class<?> newBase = getCommonBaseClass(baseType, clazz, expressionClasses);
          if (newBase != null) {
            retval = newBase;
            break;
          }
        }
      }
    }
    return retval;
  }

  private static boolean checkDerivedFrom(
      @NonNull Class<?> baseType,
      @NonNull List<Class<?>> expressionClasses) {
    boolean retval = true;
    for (Class<?> clazz : expressionClasses) {
      if (!baseType.isAssignableFrom(clazz)) {
        retval = false;
        break;
      }
    }
    return retval;
  }

}
