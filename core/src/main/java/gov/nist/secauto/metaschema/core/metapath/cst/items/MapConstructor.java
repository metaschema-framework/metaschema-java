/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of the
 * <a href="https://www.w3.org/TR/xpath-31/#id-map-constructors">Map
 * Constructor</a> supporting the creation of a Metapath {@link IMapItem}.
 */
public class MapConstructor
    extends AbstractExpression {
  @NonNull
  private final List<MapConstructor.Entry> entries;

  /**
   * Construct a new map constructor expression that uses the provided entry
   * expressions to initialize the map entries.
   *
   * @param text
   *          the parsed text of the expression
   * @param entries
   *          the expressions used to produce the map entries
   */
  public MapConstructor(@NonNull String text, @NonNull List<MapConstructor.Entry> entries) {
    super(text);
    this.entries = entries;
  }

  @Override
  public List<MapConstructor.Entry> getChildren() {
    return entries;
  }

  @Override
  protected ISequence<?> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    return IMapItem.ofCollection(
        ObjectUtils.notNull(getChildren().stream()
            .map(item -> {
              IExpression keyExpression = item.getKeyExpression();
              IAnyAtomicItem key = ISequence.of(keyExpression.accept(dynamicContext, focus).atomize())
                  .getFirstItem(true);
              if (key == null) {
                throw new InvalidTypeMetapathException(null, String.format(
                    "The expression '%s' did not result in a single key atomic value.", keyExpression.toCSTString()));
              }
              ICollectionValue value = item.getValueExpression().accept(dynamicContext, focus).toCollectionValue();

              return IMapItem.entry(key, value);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
        .toSequence();
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitMapConstructor(this, context);
  }

  /**
   * A map entry expression used to produce an entry in a {@link IMapItem}.
   */
  public static class Entry
      extends AbstractExpression {
    @NonNull
    private final IExpression keyExpression;
    @NonNull
    private final IExpression valueExpression;

    /**
     * Construct a new map entry expression using the provided key and value
     * expressions.
     *
     * @param text
     *          the parsed text of the expression
     * @param keyExpression
     *          the expression used to get the map entry key
     * @param valueExpression
     *          the expression used to get the map entry value
     */
    public Entry(@NonNull String text, @NonNull IExpression keyExpression, @NonNull IExpression valueExpression) {
      super(text);
      this.keyExpression = keyExpression;
      this.valueExpression = valueExpression;
    }

    /**
     * Get the map entry key expression.
     *
     * @return the key expression
     */
    @NonNull
    public IExpression getKeyExpression() {
      return keyExpression;
    }

    /**
     * Get the map entry value expression.
     *
     * @return the value expression
     */
    @NonNull
    public IExpression getValueExpression() {
      return valueExpression;
    }

    @SuppressWarnings("null")
    @Override
    public List<? extends IExpression> getChildren() {
      return List.of(keyExpression, valueExpression);
    }

    @Override
    protected ISequence<?> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
      throw new UnsupportedOperationException("handled by the map constructor");
    }

    @Override
    public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
      return visitor.visitMapConstructorEntry(this, context);
    }
  }
}
