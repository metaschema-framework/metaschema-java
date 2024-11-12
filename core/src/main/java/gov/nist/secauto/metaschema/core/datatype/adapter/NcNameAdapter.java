/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INcNameItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#ncname">ncname</a>
 * data type.
 */
@Deprecated(since = "0.7.0")
public class NcNameAdapter
    extends AbstractStringAdapter<INcNameItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "ncname"),
          // for backwards compatibility with original type name
          new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "NCName")));

  NcNameAdapter() {
    // avoid general construction
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public Class<INcNameItem> getItemClass() {
    return INcNameItem.class;
  }

  @Override
  public INcNameItem newItem(Object value) {
    String item = asString(value);
    return INcNameItem.valueOf(item);
  }
}
