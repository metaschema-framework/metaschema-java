/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
    String property = upperCamelCase(name);
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
  @NonNull
  public static String toVariableName(@NonNull String name) {
    return lowerCamelCase(name);
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java class
   * name.
   *
   * @param name
   *          the name of an information element definition
   * @return a Java variable name
   */
  @NonNull
  public static String toClassName(@NonNull String name) {
    return upperCamelCase(name);
  }

  /**
   * Transforms the provided name into a string suitable for use as a Java package
   * name.
   *
   * @param namespace
   *          a namespace URI to convert to a package name
   * @return a Java package name
   */
  @NonNull
  public static String toPackageName(@NonNull String namespace) {
    return getPackageFromNamespace(namespace);
  }

  /**
   * Converts a string to UpperCamelCase.
   * <p>
   * Splits on common separators (hyphen, underscore, period, whitespace) and
   * capitalizes the first letter of each word.
   *
   * @param name
   *          the name to convert
   * @return the name in UpperCamelCase
   */
  @NonNull
  private static String upperCamelCase(@NonNull String name) {
    List<String> words = splitWords(name);
    StringBuilder sb = new StringBuilder();
    for (String word : words) {
      if (!word.isEmpty()) {
        sb.append(Character.toUpperCase(word.charAt(0)));
        if (word.length() > 1) {
          sb.append(word.substring(1).toLowerCase(Locale.ROOT));
        }
      }
    }
    return sb.length() > 0 ? ObjectUtils.notNull(sb.toString()) : name;
  }

  /**
   * Converts a string to lowerCamelCase.
   * <p>
   * Splits on common separators (hyphen, underscore, period, whitespace) and
   * capitalizes the first letter of each word except the first.
   *
   * @param name
   *          the name to convert
   * @return the name in lowerCamelCase
   */
  @NonNull
  private static String lowerCamelCase(@NonNull String name) {
    List<String> words = splitWords(name);
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String word : words) {
      if (!word.isEmpty()) {
        if (first) {
          sb.append(word.toLowerCase(Locale.ROOT));
          first = false;
        } else {
          sb.append(Character.toUpperCase(word.charAt(0)));
          if (word.length() > 1) {
            sb.append(word.substring(1).toLowerCase(Locale.ROOT));
          }
        }
      }
    }
    return sb.length() > 0 ? ObjectUtils.notNull(sb.toString()) : name;
  }

  /**
   * Splits a name into words based on common separators.
   * <p>
   * Handles hyphens, underscores, periods, and whitespace as word separators.
   * Also splits on camelCase boundaries.
   *
   * @param name
   *          the name to split
   * @return a list of words
   */
  @NonNull
  private static List<String> splitWords(@NonNull String name) {
    List<String> words = new ArrayList<>();
    StringBuilder currentWord = new StringBuilder();

    for (int i = 0; i < name.length(); i++) {
      char ch = name.charAt(i);
      if (ch == '-' || ch == '_' || ch == '.' || Character.isWhitespace(ch)) {
        // Separator found, end current word
        if (currentWord.length() > 0) {
          words.add(currentWord.toString());
          currentWord = new StringBuilder();
        }
      } else if (Character.isUpperCase(ch) && currentWord.length() > 0
          && Character.isLowerCase(currentWord.charAt(currentWord.length() - 1))) {
        // CamelCase boundary
        words.add(currentWord.toString());
        currentWord = new StringBuilder();
        currentWord.append(ch);
      } else {
        currentWord.append(ch);
      }
    }
    if (currentWord.length() > 0) {
      words.add(currentWord.toString());
    }
    return words;
  }

  /**
   * Converts a namespace URI to a Java package name.
   * <p>
   * Based on the standard URI to package name algorithm:
   * <ol>
   * <li>Extract the host and reverse it (e.g., "csrc.nist.gov" becomes
   * "gov.nist.csrc")</li>
   * <li>Append the path segments, replacing separators with dots</li>
   * <li>Remove or escape invalid package name characters</li>
   * </ol>
   *
   * @param namespace
   *          the namespace URI
   * @return a valid Java package name
   */
  @NonNull
  private static String getPackageFromNamespace(@NonNull String namespace) {
    try {
      URI uri = new URI(namespace);
      StringBuilder sb = new StringBuilder();

      // Process the host (reverse the domain name)
      String host = uri.getHost();
      if (host != null && !host.isEmpty()) {
        String[] hostParts = host.split("\\.");
        for (int i = hostParts.length - 1; i >= 0; i--) {
          if (sb.length() > 0) {
            sb.append('.');
          }
          sb.append(normalizePackagePart(ObjectUtils.notNull(hostParts[i])));
        }
      }

      // Process the path
      String path = uri.getPath();
      if (path != null && !path.isEmpty()) {
        String[] pathParts = path.split("/");
        for (String part : pathParts) {
          if (!part.isEmpty()) {
            if (sb.length() > 0) {
              sb.append('.');
            }
            sb.append(normalizePackagePart(part));
          }
        }
      }

      return sb.length() > 0 ? ObjectUtils.notNull(sb.toString()) : "generated";
    } catch (@SuppressWarnings("unused") URISyntaxException ex) {
      // Fall back to a simple approach if URI parsing fails
      return normalizePackagePart(ObjectUtils.notNull(namespace.replaceAll("[^a-zA-Z0-9]", "_")));
    }
  }

  /**
   * Normalizes a string to be a valid Java package name part.
   * <p>
   * Replaces or removes invalid characters and ensures the result is a valid
   * identifier.
   *
   * @param part
   *          the package name part to normalize
   * @return a valid package name part
   */
  @NonNull
  private static String normalizePackagePart(@NonNull String part) {
    if (part.isEmpty()) {
      return "_";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < part.length(); i++) {
      char ch = part.charAt(i);
      if (i == 0) {
        if (Character.isJavaIdentifierStart(ch)) {
          sb.append(Character.toLowerCase(ch));
        } else if (Character.isDigit(ch)) {
          sb.append('_');
          sb.append(ch);
        } else {
          sb.append('_');
        }
      } else {
        if (Character.isJavaIdentifierPart(ch)) {
          sb.append(Character.toLowerCase(ch));
        } else if (ch == '-' || ch == '.') {
          // Common separators become underscores
          sb.append('_');
        }
        // else skip invalid characters
      }
    }

    String result = ObjectUtils.notNull(sb.toString());
    // Handle Java reserved words
    if (isJavaReservedWord(result)) {
      return "_" + result;
    }
    return result.isEmpty() ? "_" : result;
  }

  /**
   * Java reserved words and contextual keywords that cannot be used as
   * identifiers.
   * <p>
   * Includes both traditional reserved words and contextual keywords introduced
   * in later Java versions (var, yield, record, sealed, permits) for forward
   * compatibility.
   */
  private static final Set<String> JAVA_RESERVED_WORDS = Set.of(
      "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
      "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
      "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
      "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
      "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
      "volatile", "while", "true", "false", "null",
      // Contextual keywords (Java 9+)
      "module", "var", "yield", "record", "sealed", "permits");

  /**
   * Checks if the given string is a Java reserved word.
   *
   * @param word
   *          the word to check
   * @return {@code true} if the word is a Java reserved word
   */
  private static boolean isJavaReservedWord(@NonNull String word) {
    return JAVA_RESERVED_WORDS.contains(word);
  }
}
