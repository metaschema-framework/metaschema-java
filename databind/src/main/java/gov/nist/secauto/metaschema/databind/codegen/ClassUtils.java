/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import org.apache.xmlbeans.impl.common.NameUtil;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A variety of utility methods for normalizing Java class related names.
 */
public final class ClassUtils {
  private static final Map<String, String> JAVA_NAME_MAPPER = Map.ofEntries(
      Map.entry("Class", "Clazz"));

  private ClassUtils() {
    // disable construction
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java
   * property name.
   *
   * @param name
   *          the name of an information element definition
   * @return a Java property name
   */
  @SuppressWarnings("null")
  @NonNull
  public static String toPropertyName(@NonNull String name) {
    String property = NameUtil.upperCamelCase(name);
    return JAVA_NAME_MAPPER.getOrDefault(property, property);
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java
   * variable name.
   *
   * @param name
   *          the name of an information element definition
   * @return a Java variable name
   */
  @SuppressWarnings("null")
  @NonNull
  public static String toVariableName(@NonNull String name) {
    return NameUtil.lowerCamelCase(name);
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java class
   * name.
   *
   * @param name
   *          the name of an information element definition
   * @return a Java variable name
   */
  @SuppressWarnings("null")
  @NonNull
  public static String toClassName(@NonNull String name) {
    return NameUtil.upperCamelCase(name, false);
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java package
   * name.
   *
   * @param name
   *          the name of an information element definition
   * @return a Java variable name
   */
  @SuppressWarnings("null")
  @NonNull
  public static String toPackageName(@NonNull String name) {
    return NameUtil.getPackageFromNamespace(name, false);
  }
}
