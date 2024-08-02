/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Selections of serialization formats.
 */
public enum Format {
  /**
   * The <a href="https://www.w3.org/XML/">Extensible Markup Language</a> format.
   */
  XML(".xml", Set.of()),
  /**
   * The <a href="https://www.json.org/">JavaScript Object Notation</a> format.
   */
  JSON(".json", Set.of()),
  /**
   * The <a href="https://yaml.org/">YAML Ain't Markup Language</a> format.
   */
  YAML(".yaml", Set.of(".yml"));

  private static final List<String> NAMES;

  @NonNull
  private final String defaultExtension;
  @NonNull
  private final Set<String> recognizedExtensions;

  static {
    NAMES = Arrays.stream(values())
        .map(format -> format.name().toLowerCase(Locale.ROOT))
        .collect(Collectors.toUnmodifiableList());
  }

  /**
   * Get a list of all format names in lowercase.
   *
   * @return the list of names
   */
  @SuppressFBWarnings(value = "MS_EXPOSE_REP", justification = "Exposes names provided by the enum")
  public static List<String> names() {
    return NAMES;
  }

  Format(@NonNull String defaultExtension, Set<String> otherExtensions) {
    this.defaultExtension = defaultExtension;

    Set<String> recognizedExtensions = new HashSet<>();
    recognizedExtensions.add(defaultExtension);
    recognizedExtensions.addAll(otherExtensions);

    this.recognizedExtensions = CollectionUtil.unmodifiableSet(recognizedExtensions);
  }

  /**
   * Get the default extension to use for the format.
   *
   * @return the default extension
   */
  @NonNull
  public Set<String> getRecognizedExtensions() {
    return recognizedExtensions;
  }

  /**
   * Get the default extension to use for the format.
   *
   * @return the default extension
   */
  @NonNull
  public String getDefaultExtension() {
    return defaultExtension;
  }
}
