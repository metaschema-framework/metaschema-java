/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import com.fasterxml.jackson.core.JsonLocation;

import javax.xml.stream.Location;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A simple implementation of {@link IMetaschemaData} that stores location
 * information from various parser sources.
 * <p>
 * This class can be used both for bound object metadata during parsing and for
 * validation context in error messages.
 */
public final class SimpleResourceLocation implements IMetaschemaData {
  /** A constant representing an unknown location. */
  @NonNull
  public static final IMetaschemaData UNKNOWN = new SimpleResourceLocation(-1, -1, -1L, -1L);

  private final int line;
  private final int column;
  private final long charOffset;
  private final long byteOffset;

  /**
   * Construct a new resource location with the specified values.
   *
   * @param line
   *          the line number (1-based), or -1 if unknown
   * @param column
   *          the column number (1-based), or -1 if unknown
   * @param charOffset
   *          the character offset (0-based), or -1 if unknown
   * @param byteOffset
   *          the byte offset (0-based), or -1 if unknown
   */
  public SimpleResourceLocation(int line, int column, long charOffset, long byteOffset) {
    this.line = line;
    this.column = column;
    this.charOffset = charOffset;
    this.byteOffset = byteOffset;
  }

  /**
   * Create a resource location from an XML stream location.
   *
   * @param location
   *          the XML stream location, may be {@code null}
   * @return a new resource location, or {@link #UNKNOWN} if the input is null
   */
  @NonNull
  public static IMetaschemaData fromXmlLocation(Location location) {
    if (location == null) {
      return UNKNOWN;
    }
    return new SimpleResourceLocation(
        location.getLineNumber(),
        location.getColumnNumber(),
        location.getCharacterOffset(),
        -1L);
  }

  /**
   * Create a resource location from a Jackson JSON location.
   *
   * @param location
   *          the JSON location, may be {@code null}
   * @return a new resource location, or {@link #UNKNOWN} if the input is null
   */
  @NonNull
  public static IMetaschemaData fromJsonLocation(JsonLocation location) {
    if (location == null) {
      return UNKNOWN;
    }
    return new SimpleResourceLocation(
        location.getLineNr(),
        location.getColumnNr(),
        location.getCharOffset(),
        location.getByteOffset());
  }

  /**
   * Create a resource location with just line and column information.
   *
   * @param line
   *          the line number (1-based), or -1 if unknown
   * @param column
   *          the column number (1-based), or -1 if unknown
   * @return a new resource location
   */
  @NonNull
  public static IMetaschemaData of(int line, int column) {
    return new SimpleResourceLocation(line, column, -1L, -1L);
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getColumn() {
    return column;
  }

  @Override
  public long getCharOffset() {
    return charOffset;
  }

  @Override
  public long getByteOffset() {
    return byteOffset;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (line >= 0) {
      sb.append(line);
      if (column >= 0) {
        sb.append(':').append(column);
      }
    } else {
      sb.append("unknown");
    }
    return sb.toString();
  }
}
