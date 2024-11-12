/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IEmailAddressItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#email-address">email-address</a>
 * data type.
 */
public class EmailAddressAdapter
    extends AbstractStringAdapter<IEmailAddressItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(
          new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "email-address"),
          // for backwards compatibility with original type name
          new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "email")));

  EmailAddressAdapter() {
    // avoid general construction
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  @NonNull
  public Class<IEmailAddressItem> getItemClass() {
    return IEmailAddressItem.class;
  }

  @Override
  public IEmailAddressItem newItem(Object value) {
    String item = asString(value);
    return IEmailAddressItem.valueOf(item);
  }
}
