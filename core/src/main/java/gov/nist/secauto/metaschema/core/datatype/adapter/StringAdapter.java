/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#string">string</a>
 * data type.
 */
public class StringAdapter
    extends AbstractStringAdapter<IStringItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "string")));

  StringAdapter() {
    // avoid general construction
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public Class<IStringItem> getItemClass() {
    return IStringItem.class;
  }

  @Override
  @NonNull
  public IStringItem newItem(@NonNull Object value) {
    String item = asString(value);
    return IStringItem.valueOf(item);
  }
}
