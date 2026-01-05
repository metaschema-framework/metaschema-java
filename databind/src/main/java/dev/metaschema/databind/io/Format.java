/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import dev.metaschema.core.metapath.format.IPathFormatter;
import dev.metaschema.core.metapath.format.PathFormatSelection;
import dev.metaschema.core.util.CollectionUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
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

  /**
   * Get the appropriate path formatter for this format.
   * <p>
   * Returns:
   * <ul>
   * <li>{@link IPathFormatter#XPATH_PATH_FORMATTER} for XML</li>
   * <li>{@link IPathFormatter#JSON_POINTER_PATH_FORMATTER} for JSON and YAML</li>
   * </ul>
   *
   * @return the path formatter appropriate for this format
   */
  @NonNull
  public IPathFormatter getPathFormatter() {
    return this == XML
        ? IPathFormatter.XPATH_PATH_FORMATTER
        : IPathFormatter.JSON_POINTER_PATH_FORMATTER;
  }

  /**
   * Resolve the path formatter based on the selection and document format.
   * <p>
   * When {@link PathFormatSelection#AUTO} is specified, the formatter is
   * determined by the document format. For explicit selections, the corresponding
   * formatter is returned regardless of document format.
   *
   * @param selection
   *          the path format selection
   * @param format
   *          the document format, used when selection is AUTO; may be null
   * @return the resolved path formatter
   */
  @NonNull
  public static IPathFormatter resolvePathFormatter(
      @NonNull PathFormatSelection selection,
      @Nullable Format format) {
    switch (selection) {
    case AUTO:
      return format != null ? format.getPathFormatter() : IPathFormatter.METAPATH_PATH_FORMATER;
    case METAPATH:
      return IPathFormatter.METAPATH_PATH_FORMATER;
    case XPATH:
      return IPathFormatter.XPATH_PATH_FORMATTER;
    case JSON_POINTER:
      return IPathFormatter.JSON_POINTER_PATH_FORMATTER;
    default:
      return IPathFormatter.METAPATH_PATH_FORMATER;
    }
  }
}
