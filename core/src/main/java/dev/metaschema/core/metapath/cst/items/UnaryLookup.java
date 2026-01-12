/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.items;

import java.util.List;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IExpression;
import dev.metaschema.core.metapath.cst.IExpressionVisitor;
import dev.metaschema.core.metapath.item.ICollectionValue;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.function.IArrayItem;
import dev.metaschema.core.metapath.item.function.IKeySpecifier;
import dev.metaschema.core.metapath.item.function.IMapItem;
import dev.metaschema.core.util.ObjectUtils;
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
   * @param text
   *          the parsed text of the expression
   * @param keySpecifier
   *          the key specifier used to determine matching entries
   */
  public UnaryLookup(@NonNull String text, @NonNull IKeySpecifier keySpecifier) {
    super(text, keySpecifier);
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of();
  }

  @Override
  protected ISequence<? extends IItem> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
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
