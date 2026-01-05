/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.datatype.adapter;

import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.item.atomic.IEmailAddressItem;
import dev.metaschema.core.qname.EQNameFactory;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#email-address">email-address</a>
 * data type.
 */
public class EmailAddressAdapter
    extends AbstractStringAdapter<IEmailAddressItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "email-address"),
          // for backwards compatibility with original type name
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "email")));

  EmailAddressAdapter() {
    super(IEmailAddressItem.class, IEmailAddressItem::cast);
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public IEmailAddressItem newItem(Object value) {
    String item = asString(value);
    return IEmailAddressItem.valueOf(item);
  }
}
