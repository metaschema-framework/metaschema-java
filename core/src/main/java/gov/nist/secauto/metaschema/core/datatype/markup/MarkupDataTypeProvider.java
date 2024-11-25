/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IMarkupItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides for runtime discovery of built-in implementations of the markup
 * Metaschema data types.
 */
public final class MarkupDataTypeProvider
    extends AbstractDataTypeProvider {
  /**
   * The Metaschema <a href=
   * "https://pages.nist.gov/metaschema/specification/datatypes/#markup-line">markup-line</a>
   * data type instance.
   */
  @NonNull
  public static final MarkupLineAdapter MARKUP_LINE = new MarkupLineAdapter();
  /**
   * The Metaschema <a href=
   * "https://pages.nist.gov/metaschema/specification/datatypes/#markup-multiline">markup-multiline</a>
   * data type instance.
   */
  @NonNull
  public static final MarkupMultilineAdapter MARKUP_MULTILINE = new MarkupMultilineAdapter();

  /**
   * The Metaschema data type that represents all markup types.
   */
  @NonNull
  public static final IAtomicOrUnionType<IMarkupItem> MARKUP_TYPE
      = IAtomicOrUnionType.of(
          IMarkupItem.class,
          IMarkupItem::cast,
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "markup"));

  /**
   * Create the data type provider.
   */
  public MarkupDataTypeProvider() {
    register(MARKUP_LINE);
    register(MARKUP_MULTILINE);

    // register abstract types
    register(MARKUP_TYPE);
  }
}
