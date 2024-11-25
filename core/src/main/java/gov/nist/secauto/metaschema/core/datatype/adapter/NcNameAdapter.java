/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INcNameItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.regex.Pattern;

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
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "ncname"),
          // for backwards compatibility with original type name
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "NCName")));
  private static final Pattern NCNAME = Pattern.compile(String.format("^(\\p{L}|_)(\\p{L}|\\p{N}|[.\\-_])*$"));

  /**
   * Determine if the name is a non-colonized name.
   *
   * @param name
   *          the name to test
   * @return {@code true} if the name is not colonized, or {@code false} otherwise
   */
  public static boolean isNcName(@NonNull String name) {
    return NCNAME.matcher(name).matches();
  }

  NcNameAdapter() {
    super(INcNameItem.class, INcNameItem::cast);
    // avoid general construction
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public String parse(String value) {
    if (!isNcName(value)) {
      throw new IllegalArgumentException(String.format("Value '%s' is not a valid ncname.", value));
    }
    return super.parse(value);
  }

  @Override
  public INcNameItem newItem(Object value) {
    String item = asString(value);
    return INcNameItem.valueOf(item);
  }
}
