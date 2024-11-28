/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IKeySpecifier;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of
 * <a href="https://www.w3.org/TR/xpath-31/#id-unary-lookup">Unary Lookup
 * Operators</a> supporting access to items in Metapath maps and arrays.
 * <p>
 * Provides support for various types of key- and index-based lookups related to
 * {@link IMapItem} and {@link IArrayItem} objects.
 */
public class UnaryLookup
    extends AbstractLookup {
  /**
   * Construct a new unary lookup expression that uses the provided key specifier.
   *
   * @param keySpecifier
   *          the key specifier used to determine matching entries
   */
  public UnaryLookup(@NonNull IKeySpecifier keySpecifier) {
    super(keySpecifier);
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of();
  }

  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    IKeySpecifier specifier = getKeySpecifier();

    return ISequence.of(ObjectUtils.notNull(focus.stream()
        .flatMap(item -> {
          assert item != null;
          return specifier.lookup(item, dynamicContext, focus);
        })
        .flatMap(ICollectionValue::normalizeAsItems)));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(@NonNull IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitUnaryLookup(this, context);
  }
}
