/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.datatype.adapter;

import java.util.List;

import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.item.atomic.IHostnameItem;
import dev.metaschema.core.qname.EQNameFactory;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#hostname">hostname</a>
 * data type.
 */
public class HostnameAdapter
    extends AbstractStringAdapter<IHostnameItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "hostname")));

  HostnameAdapter() {
    super(IHostnameItem.class, IHostnameItem::cast);
    // avoid general construction
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public IHostnameItem newItem(Object value) {
    String item = asString(value);
    return IHostnameItem.valueOf(item);
  }
}
