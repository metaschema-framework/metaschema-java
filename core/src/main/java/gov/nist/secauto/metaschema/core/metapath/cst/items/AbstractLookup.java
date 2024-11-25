/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IKeySpecifier;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of
 * <a href="https://www.w3.org/TR/xpath-31/#id-lookup">Lookup Operators</a>
 * supporting access to items in Metapath maps and arrays.
 * <p>
 * Provides support for various types of key- and index-based lookups related to
 * {@link IMapItem} and {@link IArrayItem} objects.
 */
public abstract class AbstractLookup implements IExpression {
  @NonNull
  private final IKeySpecifier keySpecifier;

  /**
   * Construct a new lookup expression that uses the provided key specifier.
   *
   * @param keySpecifier
   *          the key specifier that identifies how to lookup entries
   */
  protected AbstractLookup(@NonNull IKeySpecifier keySpecifier) {
    this.keySpecifier = keySpecifier;
  }

  /**
   * Get the key specifier implementation.
   *
   * @return the key specifier
   */
  @NonNull
  public IKeySpecifier getKeySpecifier() {
    return keySpecifier;
  }
}
