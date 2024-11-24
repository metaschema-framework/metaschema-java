/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IEmailAddressItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.qname.QNameCache;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

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
          QNameCache.instance().of(MetapathConstants.NS_METAPATH, "email-address"),
          // for backwards compatibility with original type name
          QNameCache.instance().of(MetapathConstants.NS_METAPATH, "email")));

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
