/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import java.util.List;

import dev.metaschema.core.datatype.AbstractDataTypeAdapter;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.item.atomic.IQNameItem;
import dev.metaschema.core.qname.EQNameFactory;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#qname">qname</a>
 * data type.
 */
public class QNameAdapter
    extends AbstractDataTypeAdapter<IEnhancedQName, IQNameItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "qname")));

  QNameAdapter() {
    super(IEnhancedQName.class, IQNameItem.class, IQNameItem::cast);
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @Override
  public IEnhancedQName parse(String value) {
    throw new UnsupportedOperationException("QNameAdapter does not support parse qualified namespaces or prefixes.");
  }

  @Override
  public IEnhancedQName copy(Object obj) {
    // a URI is immutable
    return (IEnhancedQName) obj;
  }

  @Override
  public IQNameItem newItem(Object value) {
    IEnhancedQName item = toValue(value);
    // TODO: Review metaschema-framework/metaschema-java#396 and change accordingly.
    return IQNameItem.valueOf(item);
  }
}
