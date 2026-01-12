/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic.impl;

import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.datatype.adapter.TokenAdapter;
import dev.metaschema.core.metapath.item.atomic.ITokenItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a text token data
 * value.
 */
public class TokenItemImpl
    extends AbstractStringItem
    implements ITokenItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public TokenItemImpl(@NonNull String value) {
    super(value);
  }

  @Override
  public TokenAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.TOKEN;
  }
}
