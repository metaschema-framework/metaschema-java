/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.metapath.impl.AbstractStringMapKey;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractStringItem
    extends AbstractAnyAtomicItem<String>
    implements IStringItem {
  private static final String WHITESPACE_SEGMENT = "[ \t\r\n]";
  private static final Pattern TRIM_END = Pattern.compile(WHITESPACE_SEGMENT + "++$");
  private static final Pattern TRIM_START = Pattern.compile("^" + WHITESPACE_SEGMENT + "+");
  private static final Pattern TRIM_MIDDLE = Pattern.compile(WHITESPACE_SEGMENT + "{2,}");

  /**
   * Construct a new string item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  protected AbstractStringItem(@NonNull String value) {
    super(value);
  }

  @Override
  public String asString() {
    return getValue();
  }

  @Override
  public IMapKey asMapKey() {
    return new MapKey();
  }

  @Override
  public int hashCode() {
    return asString().hashCode();
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || (obj instanceof IStringItem && compareTo((IStringItem) obj) == 0);
  }

  private final class MapKey
      extends AbstractStringMapKey {

    @Override
    public IStringItem getKey() {
      return AbstractStringItem.this;
    }
  }

  @Override
  public IStringItem normalizeSpace() {
    String value = asString();
    value = TRIM_START.matcher(value).replaceFirst("");
    value = TRIM_MIDDLE.matcher(value).replaceAll(" ");
    value = TRIM_END.matcher(value).replaceFirst("");
    assert value != null;

    return IStringItem.valueOf(value);
  }

}
