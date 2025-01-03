/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.StaticContext.EQNameResolver;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a variety of methods to check the dynamic type of a definition-based
 * node item.
 */
public final class DynamicTypeSupport {

  /**
   * Checks that the provided actual item matches the provided expected type name.
   *
   * @param actual
   *          the item to check
   * @param expected
   *          the expected definition or atomic type name
   * @param staticContext
   *          used to resolve definition names and lookup atomic type names
   * @return {@code true} if the expected name matches either the node's
   *         definition name or the node's atomic type, or {@code false} otherwise
   */
  public static boolean derivesFrom(
      @NonNull IFlagNodeItem actual,
      @NonNull String expected,
      @NonNull StaticContext staticContext) {
    IFlagDefinition definition = actual.getDefinition();
    return compareDefinition(definition, expected, staticContext::parseFlagName)
        || compareAtomicTypes(expected, definition.getJavaTypeAdapter().getItemType(), staticContext);
  }

  /**
   * Checks that the provided actual item matches the provided expected type name.
   *
   * @param actual
   *          the item to check
   * @param expected
   *          the expected definition or atomic type name
   * @param staticContext
   *          used to resolve definition names and lookup atomic type names
   * @return {@code true} if the expected name matches either the node's
   *         definition name or the node's atomic type, or {@code false} otherwise
   */
  public static boolean derivesFrom(
      @NonNull IFieldNodeItem actual,
      @NonNull String expected,
      @NonNull StaticContext staticContext) {
    IFieldDefinition definition = actual.getDefinition();
    return compareDefinition(definition, expected, staticContext::parseModelName)
        || compareAtomicTypes(expected, definition.getJavaTypeAdapter().getItemType(), staticContext);
  }

  /**
   * Checks that the provided actual item matches the provided expected type name.
   *
   * @param actual
   *          the item to check
   * @param expected
   *          the expected definition name
   * @param staticContext
   *          used to resolve definition names
   * @return {@code true} if the expected name matches the node's definition name,
   *         or {@code false} otherwise
   */
  public static boolean derivesFrom(
      @NonNull IAssemblyNodeItem actual,
      @NonNull String expected,
      @NonNull StaticContext staticContext) {
    try {
      IEnhancedQName expectedName = staticContext.parseModelName(expected);
      IAssemblyDefinition definition = actual.getDefinition();
      return definition.getDefinitionQName().equals(expectedName); // AT is ET
    } catch (StaticMetapathException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("The expected type '%s' is not known to the type system.", expected),
          ex);
    }
  }

  private static boolean compareDefinition(
      @NonNull IDefinition definition,
      @NonNull String expected,
      @NonNull EQNameResolver nameResolver) {
    boolean retval;
    try {
      IEnhancedQName expectedName = nameResolver.resolve(expected);
      retval = definition.getDefinitionQName().equals(expectedName); // AT is ET
    } catch (@SuppressWarnings("unused") StaticMetapathException ex) {
      // fail the definition name test
      retval = false;
    }
    return retval;
  }

  private static boolean compareAtomicTypes(
      @NonNull String expected,
      @NonNull IAtomicOrUnionType<?> actualType,
      @NonNull StaticContext staticContext) {
    // lookup the expected type
    IAtomicOrUnionType<?> expectedType;
    try {
      expectedType = staticContext.lookupAtomicType(expected);
    } catch (StaticMetapathException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("The expected type '%s' is not known to the type system.", expected),
          ex);
    }

    return actualType.equals(expectedType) // ET is the base type of AT
        || expectedType.isMemberType(actualType) // ET is a pure union type of which AT is a member
        || expectedType.isSubType(actualType); // AT is derived from ET
  }

  private DynamicTypeSupport() {
    // disable construction
  }

}
