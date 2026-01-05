/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.datatype.adapter;

import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.qname.EQNameFactory;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#string">string</a>
 * data type.
 */
public class StringAdapter
    extends AbstractStringAdapter<IStringItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "string")));

  StringAdapter() {
    super(IStringItem.class, IStringItem::cast);
    // avoid general construction
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  @NonNull
  public IStringItem newItem(@NonNull Object value) {
    String item = asString(value);
    return IStringItem.valueOf(item);
  }
}
