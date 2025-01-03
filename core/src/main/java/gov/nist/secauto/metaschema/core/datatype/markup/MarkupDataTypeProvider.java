/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeProvider;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides for runtime discovery of built-in implementations of the markup
 * Metaschema data types.
 */
public final class MarkupDataTypeProvider
    extends AbstractDataTypeProvider {
  @NonNull
  public static final MarkupLineAdapter MARKUP_LINE = new MarkupLineAdapter();
  @NonNull
  public static final MarkupMultilineAdapter MARKUP_MULTILINE = new MarkupMultilineAdapter();

  /**
   * Create the data type provider.
   */
  public MarkupDataTypeProvider() {
    registerDatatype(MARKUP_LINE);
    registerDatatype(MARKUP_MULTILINE);
  }
}
