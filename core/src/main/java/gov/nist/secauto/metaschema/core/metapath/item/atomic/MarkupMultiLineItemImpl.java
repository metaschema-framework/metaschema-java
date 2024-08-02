/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultilineAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

class MarkupMultiLineItemImpl
    extends AbstractUntypedAtomicItem<MarkupMultiline>
    implements IMarkupItem {

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
