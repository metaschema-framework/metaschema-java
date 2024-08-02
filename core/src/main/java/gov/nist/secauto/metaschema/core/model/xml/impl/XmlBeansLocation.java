/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.XmlBookmark;
import org.apache.xmlbeans.XmlLineNumber;
import org.apache.xmlbeans.XmlObject;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides location information for an XMLBeans object.
 */
public final class XmlBeansLocation implements IResourceLocation {
  @NonNull
  private final XmlLineNumber lineNumber;

  /**
   * Get the location information for an XMLBeans object if the location is
   * available.
   *
   * @param xmlObject
   *          the XMLBeans object to get the location information for
   * @return the location information or {@code null} if the location information
   *         is unavailable
   */
  @Nullable
  public static IResourceLocation toLocation(@NonNull XmlObject xmlObject) {
    try (XmlCursor cursor = xmlObject.newCursor()) {
      XmlBookmark bookmark = cursor.getBookmark(XmlLineNumber.class);
      return bookmark == null ? null : new XmlBeansLocation((XmlLineNumber) bookmark);
    }
  }

  private XmlBeansLocation(@NonNull XmlLineNumber lineNumber) {
    this.lineNumber = lineNumber;
  }

  @Override
  public int getLine() {
    return lineNumber.getLine();
  }

  @Override
  public int getColumn() {
    return lineNumber.getColumn();
  }

  @Override
  public long getCharOffset() {
    return lineNumber.getOffset();
  }

  @Override
  public long getByteOffset() {
    // not supported
    return -1;
  }
}
