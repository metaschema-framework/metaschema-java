/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for introspecting Java classes bound to Metaschema definitions.
 * <p>
 * This class provides methods to analyze class hierarchies and extract
 * binding-related information from annotated classes.
 */
public final class ClassIntrospector {
  private ClassIntrospector() {
    // disable construction
  }

  /**
   * Finds all methods with the specified name and parameter types in the class
   * hierarchy.
   * <p>
   * This method searches the given class and all superclasses for methods
   * matching the specified name and parameter types.
   *
   * @param clazz
   *          the class to search
   * @param name
   *          the method name to find
   * @param parameterTypes
   *          the parameter types of the method
   * @return an unmodifiable list of matching methods found in the class
   *         hierarchy, or an empty list if no matches are found
   */
  @SuppressWarnings("PMD.EmptyCatchBlock")
  public static List<Method> getMatchingMethods(Class<?> clazz, String name, Class<?>... parameterTypes) {
    List<Method> retval = new LinkedList<>();
    Class<?> searchClass = clazz;
    do {
      try {
        Method method = searchClass.getDeclaredMethod(name, parameterTypes);
        retval.add(method);
      } catch (@SuppressWarnings("unused") NoSuchMethodException ex) {
        // do nothing, no matching method was found
      }
    } while ((searchClass = searchClass.getSuperclass()) != null);

    return retval.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(retval);
  }

  /**
   * Finds the first method with the specified name and parameter types in the
   * class hierarchy.
   * <p>
   * This method searches the given class and all superclasses, returning the
   * first method found that matches the specified name and parameter types.
   *
   * @param clazz
   *          the class to search
   * @param name
   *          the method name to find
   * @param parameterTypes
   *          the parameter types of the method
   * @return the first matching method found in the class hierarchy, or
   *         {@code null} if no match is found
   */
  @SuppressWarnings("PMD.EmptyCatchBlock")
  public static Method getMatchingMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
    Method retval = null;
    Class<?> searchClass = clazz;
    do {
      try {
        retval = searchClass.getDeclaredMethod(name, parameterTypes);
        // stop on first found method
        break;
      } catch (@SuppressWarnings("unused") NoSuchMethodException ex) {
        // do nothing, no matching method was found
      }
    } while ((searchClass = searchClass.getSuperclass()) != null);

    return retval;
  }
}
