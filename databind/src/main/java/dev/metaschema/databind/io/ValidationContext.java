/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import dev.metaschema.core.model.IResourceLocation;
import dev.metaschema.core.model.SimpleResourceLocation;
import dev.metaschema.core.util.ObjectUtils;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides contextual information for validation errors during parsing.
 * <p>
 * This class bundles together:
 * <ul>
 * <li>Source URI - the document being parsed</li>
 * <li>Location - line and column within the document</li>
 * <li>Path - the path to the current element in the document structure</li>
 * <li>Format - whether parsing XML, JSON, or YAML</li>
 * </ul>
 * <p>
 * This context is passed to problem handlers to enable rich, informative error
 * messages that help users locate and understand validation errors.
 */
public final class ValidationContext {
  @Nullable
  private final URI source;
  @NonNull
  private final IResourceLocation location;
  @NonNull
  private final String path;
  @NonNull
  private final Format format;

  /**
   * Construct a new validation context.
   *
   * @param source
   *          the source URI of the document being parsed, may be null
   * @param location
   *          the location within the document
   * @param path
   *          the path to the current element
   * @param format
   *          the format being parsed
   */
  private ValidationContext(
      @Nullable URI source,
      @NonNull IResourceLocation location,
      @NonNull String path,
      @NonNull Format format) {
    this.source = source;
    this.location = location;
    this.path = path;
    this.format = format;
  }

  /**
   * Create a new validation context.
   *
   * @param source
   *          the source URI, may be null
   * @param location
   *          the resource location, must not be null
   * @param path
   *          the current path, must not be null
   * @param format
   *          the format being parsed, must not be null
   * @return a new validation context
   */
  @NonNull
  public static ValidationContext of(
      @Nullable URI source,
      @NonNull IResourceLocation location,
      @NonNull String path,
      @NonNull Format format) {
    return new ValidationContext(source, location, path, format);
  }

  /**
   * Create a validation context with unknown location.
   *
   * @param source
   *          the source URI, may be null
   * @param path
   *          the current path
   * @param format
   *          the format being parsed
   * @return a new validation context with unknown location
   */
  @NonNull
  public static ValidationContext ofUnknownLocation(
      @Nullable URI source,
      @NonNull String path,
      @NonNull Format format) {
    return new ValidationContext(source, SimpleResourceLocation.UNKNOWN, path, format);
  }

  /**
   * Get the source URI of the document being parsed.
   *
   * @return the source URI, or null if not available
   */
  @Nullable
  public URI getSource() {
    return source;
  }

  /**
   * Get the location within the document.
   *
   * @return the resource location
   */
  @NonNull
  public IResourceLocation getLocation() {
    return location;
  }

  /**
   * Get the path to the current element.
   *
   * @return the element path
   */
  @NonNull
  public String getPath() {
    return path;
  }

  /**
   * Get the format being parsed.
   *
   * @return the format
   */
  @NonNull
  public Format getFormat() {
    return format;
  }

  /**
   * Format the location information as a human-readable string.
   * <p>
   * The format is: "in 'source' at line:column" or "at line:column" if no source
   * is available, or empty string if location is unknown.
   *
   * @return a formatted location string
   */
  @NonNull
  public String formatLocation() {
    StringBuilder sb = new StringBuilder();

    int line = location.getLine();
    int column = location.getColumn();

    if (source != null) {
      sb.append("in '").append(formatSourceName()).append("'");
      if (line >= 0) {
        sb.append(" at ").append(line);
        if (column >= 0) {
          sb.append(':').append(column);
        }
      }
    } else if (line >= 0) {
      sb.append("at ").append(line);
      if (column >= 0) {
        sb.append(':').append(column);
      }
    }

    return ObjectUtils.notNull(sb.toString());
  }

  /**
   * Format the source name for display.
   * <p>
   * Uses the file name if available, otherwise the full URI.
   *
   * @return a formatted source name, or empty string if no source
   */
  @NonNull
  private String formatSourceName() {
    URI sourceUri = source;
    if (sourceUri == null) {
      return "";
    }
    String path = sourceUri.getPath();
    if (path != null && !path.isEmpty()) {
      int lastSlash = path.lastIndexOf('/');
      if (lastSlash >= 0 && lastSlash < path.length() - 1) {
        return ObjectUtils.notNull(path.substring(lastSlash + 1));
      }
      return ObjectUtils.notNull(path);
    }
    return ObjectUtils.notNull(sourceUri.toString());
  }

  /**
   * Format the path information for display.
   *
   * @return the path, or "at document root" if path is empty or "/"
   */
  @NonNull
  public String formatPath() {
    if (path.isEmpty() || "/".equals(path)) {
      return "at document root";
    }
    return "Path: " + path;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ValidationContext[");
    sb.append("format=").append(format);
    if (source != null) {
      sb.append(", source=").append(source);
    }
    sb.append(", location=").append(location);
    sb.append(", path=").append(path);
    sb.append(']');
    return sb.toString();
  }
}
