/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITokenItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#token">token</a>
 * data type.
 */
public class TokenAdapter
    extends AbstractStringAdapter<ITokenItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "token")));

  TokenAdapter() {
    // avoid general construction
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public Class<ITokenItem> getItemClass() {
    return ITokenItem.class;
  }

  @Override
  public ITokenItem newItem(Object value) {
    String item = asString(value);
    return ITokenItem.valueOf(item);
  }
}
