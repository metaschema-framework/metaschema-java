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
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.function.IArrayItem;
import dev.metaschema.core.metapath.item.function.IKeySpecifier;
import dev.metaschema.core.metapath.item.function.IMapItem;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of
 * <a href="https://www.w3.org/TR/xpath-31/#id-postfix-lookup">Postfix Lookup
 * Operators</a> supporting access to items in Metapath maps and arrays.
 * <p>
 * Provides support for various types of key- and index-based lookups related to
 * {@link IMapItem} and {@link IArrayItem} objects.
 */
public class PostfixLookup
    extends AbstractLookup {

  @NonNull
  private final IExpression base;

  /**
   * Construct a new postfix lookup expression that uses the provided key
   * specifier.
   *
   * @param text
   *          the parsed text of the expression
   * @param base
   *          the base expression used to get the target of the lookup
   * @param keySpecifier
   *          the key specifier used to determine matching entries
   */
  public PostfixLookup(@NonNull String text, @NonNull IExpression base, @NonNull IKeySpecifier keySpecifier) {
    super(text, keySpecifier);
    this.base = base;
  }

  /**
   * Get the base sub-expression.
   *
   * @return the sub-expression
   */
  @NonNull
  public IExpression getBase() {
    return base;
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of(getBase());
  }

  @Override
  protected ISequence<?> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<?> base = getBase().accept(dynamicContext, focus);

    IKeySpecifier specifier = getKeySpecifier();

    return ISequence.of(ObjectUtils.notNull(base.stream()
        .flatMap(item -> {
          assert item != null;
          return specifier.lookup(item, dynamicContext, focus);
        })
        .flatMap(ICollectionValue::normalizeAsItems)));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(@NonNull IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitPostfixLookup(this, context);
  }
}
