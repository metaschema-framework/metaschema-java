/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IHexBinaryItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Maintains a byte buffer backed representation of a byte stream parsed from a
 * base64 encoded string.
 * <p>
 * Provides support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#base64">base64</a>
 * data type.
 */
public class HexBinaryAdapter
    extends AbstractBinaryAdapter<IHexBinaryItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(List.of(
      EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "hex-binary")));

  @NonNull
  private static final Hex HEX = new Hex(StandardCharsets.UTF_8);

  HexBinaryAdapter() {
    super(IHexBinaryItem.class, IHexBinaryItem::cast);
  }

  @Override
  protected BinaryEncoder getEncoder() {
    return HEX;
  }

  @Override
  protected BinaryDecoder getDecoder() {
    return HEX;
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public IHexBinaryItem newItem(Object value) {
    ByteBuffer item = toValue(value);
    return IHexBinaryItem.valueOf(item);
  }
}
