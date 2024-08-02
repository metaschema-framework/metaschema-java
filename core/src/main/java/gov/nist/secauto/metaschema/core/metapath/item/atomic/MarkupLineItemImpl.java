/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLineAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

class MarkupLineItemImpl
    extends AbstractUntypedAtomicItem<MarkupLine>
    implements IMarkupItem {

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
