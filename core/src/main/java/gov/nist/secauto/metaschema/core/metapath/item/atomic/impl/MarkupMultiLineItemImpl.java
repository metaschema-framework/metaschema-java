/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultilineAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.AbstractUntypedAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IMarkupItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item representing a multi-line Markup
 * data value.
 */
public class MarkupMultiLineItemImpl
    extends AbstractUntypedAtomicItem<MarkupMultiline>
    implements IMarkupItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public MarkupMultiLineItemImpl(@NonNull MarkupMultiline value) {
    super(value);
  }

  @Override
  public MarkupMultiline asMarkup() {
    return getValue();
  }

  @Override
  public MarkupMultilineAdapter getJavaTypeAdapter() {
    return MarkupDataTypeProvider.MARKUP_MULTILINE;
  }
}
