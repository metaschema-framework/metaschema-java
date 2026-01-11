/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.datatype.adapter;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.binary.Base64;

import java.nio.ByteBuffer;
import java.util.List;

import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import dev.metaschema.core.qname.EQNameFactory;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Maintains a byte buffer backed representation of a byte stream parsed from a
 * BASE64 encoded string.
 * <p>
 * Provides support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#BASE64">BASE64</a>
 * data type.
 */
public class Base64Adapter
    extends AbstractBinaryAdapter<IBase64BinaryItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "base64"),
          // for backwards compatibility with original type name
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "base64Binary")));

  @NonNull
  private static final Base64 BASE64 = ObjectUtils.notNull(Base64.builder().get());

  Base64Adapter() {
    super(IBase64BinaryItem.class, IBase64BinaryItem::cast);
  }

  @Override
  protected BinaryEncoder getEncoder() {
    return BASE64;
  }

  @Override
  protected BinaryDecoder getDecoder() {
    return BASE64;
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public IBase64BinaryItem newItem(Object value) {
    ByteBuffer item = toValue(value);
    return IBase64BinaryItem.valueOf(item);
  }
}
