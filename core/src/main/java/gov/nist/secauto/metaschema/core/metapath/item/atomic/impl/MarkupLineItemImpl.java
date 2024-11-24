/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLineAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IMarkupLineItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item representing a single line Markup
 * data value.
 */
public class MarkupLineItemImpl
    extends AbstractMarkupItem<MarkupLine>
    implements IMarkupLineItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public MarkupLineItemImpl(@NonNull MarkupLine value) {
    super(value);
  }

  @Override
  public MarkupLine asMarkup() {
    return getValue();
  }

  @Override
  public MarkupLineAdapter getJavaTypeAdapter() {
    return MarkupDataTypeProvider.MARKUP_LINE;
  }
}
