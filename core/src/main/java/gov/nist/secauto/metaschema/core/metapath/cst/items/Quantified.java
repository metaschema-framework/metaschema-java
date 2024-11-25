/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnBoolean;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Quantified
    extends AbstractExpression {
  public enum Quantifier {
    SOME,
    EVERY;
  }

  @NonNull
  private final Quantifier quantifier;
  @NonNull
  private final Map<IEnhancedQName, IExpression> inClauses;
  @NonNull
  private final IExpression satisfies;

  /**
   * Construct a new quantified expression.
   *
   * @param quantifier
   *          the quantifier operation
   * @param inClauses
   *          the set of expressions that define the variables to use for
   *          determining the Cartesian product for evaluation
   * @param satisfies
   *          the expression used for evaluation using the Cartesian product of
   *          the variables
   */
  public Quantified(
      @NonNull Quantifier quantifier,
      @NonNull Map<IEnhancedQName, IExpression> inClauses,
      @NonNull IExpression satisfies) {
    this.quantifier = quantifier;
    this.inClauses = inClauses;
    this.satisfies = satisfies;
  }

  /**
   * Get the quantifier operation.
   *
   * @return the quantifier operations
   */
  @NonNull
  public Quantifier getQuantifier() {
    return quantifier;
  }

  /**
   * Get the set of expressions that define the variables to use for determining
   * the Cartesian product for evaluation.
   *
   * @return the variable names mapped to the associated Metapath expression
   */
  @NonNull
  public Map<IEnhancedQName, IExpression> getInClauses() {
    return inClauses;
  }

  /**
   * Get the expression used for evaluation using the Cartesian product of the
   * variables.
   *
   * @return the evaluation expression
   */
  @NonNull
  public IExpression getSatisfies() {
    return satisfies;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return ObjectUtils.notNull(Stream.concat(inClauses.values().stream(), Stream.of(satisfies))
        .collect(Collectors.toList()));
  }

  @SuppressWarnings("PMD.SystemPrintln")
  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    Map<IEnhancedQName, ISequence<? extends IItem>> clauses = getInClauses().entrySet().stream()
        .map(entry -> Map.entry(
            entry.getKey(),
            entry.getValue().accept(dynamicContext, focus)))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    List<IEnhancedQName> clauseKeys = new ArrayList<>(clauses.keySet());
    List<? extends Collection<? extends IItem>> clauseValues = new ArrayList<>(clauses.values());

    boolean retval = true;
    for (List<IItem> product : new CartesianProduct<>(clauseValues)) {
      DynamicContext subDynamicContext = dynamicContext.subContext();
      for (int idx = 0; idx < product.size(); idx++) {
        IEnhancedQName var = clauseKeys.get(idx);
        IItem item = product.get(idx);

        assert var != null;

        subDynamicContext.bindVariableValue(var, ISequence.of(item));
      }
      boolean result = FnBoolean.fnBooleanAsPrimitive(getSatisfies().accept(subDynamicContext, focus));
      if (Quantifier.EVERY.equals(quantifier) && !result) {
        // fail on first false
        retval = false;
        break;
      }
      if (Quantifier.SOME.equals(quantifier)) {
        if (result) {
          // pass on first true
          retval = true;
          break;
        }
        // store (false) result
        retval = false;
      }
    }

    return ISequence.of(IBooleanItem.valueOf(retval));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitQuantified(this, context);
  }

  /**
   * Get the Cartesian product of the provided lists of value axis.
   *
   * @param <T>
   *          the Java type of value item
   * @param axes
   *          the values to compute the Cartesian product of
   * @return an iterator of lists contain the Cartesian product of the axis values
   */
  public static <T extends IItem> Iterable<List<T>> cartesianProduct(
      @NonNull List<? extends Collection<? extends T>> axes) {
    return new CartesianProduct<>(axes);
  }

  // based on https://gist.github.com/jhorstmann/a7aba9947bc4926a75f6de8f69560c6e
  private static class CartesianProductIterator<T extends IItem> implements Iterator<List<T>> {
    private final Object[][] dimensions;
    private final int length;
    private final int[] indizes;
    private boolean reachedMax;

    @SuppressWarnings({
        "PMD.UseVarargs",
        "PMD.ArrayIsStoredDirectly" // ok for internal use
    })
    CartesianProductIterator(final Object[][] dimensions) {
      this.dimensions = dimensions;
      this.length = dimensions.length;
      this.indizes = new int[length];
    }

    private void increment(final int index) {
      if (index >= length) {
        reachedMax = true;
      } else {
        indizes[index]++;
        if (indizes[index] == dimensions[index].length) {
          indizes[index] = 0;
          increment(index + 1);
        }
      }
    }

    private void increment() {
      increment(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> next() {
      if (reachedMax) {
        throw new NoSuchElementException();
      }

      List<T> list = new ArrayList<>();
      for (int i = 0; i < length; i++) {
        list.add((T) dimensions[i][indizes[i]]);
      }

      increment();

      return Collections.unmodifiableList(list);
    }

    @Override
    public boolean hasNext() {
      return !reachedMax;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove not supported");
    }
  }

  // based on https://gist.github.com/jhorstmann/a7aba9947bc4926a75f6de8f69560c6e
  private static final class CartesianProduct<T extends IItem> implements Iterable<List<T>> {
    private final Object[][] dimensions;
    private final long size;

    private CartesianProduct(final List<? extends Collection<? extends T>> axes) {
      Object[][] dimensions = new Object[axes.size()][];
      long size = dimensions.length == 0 ? 0 : 1;
      for (int i = 0; i < axes.size(); i++) {
        dimensions[i] = axes.get(i).toArray();
        size *= dimensions[i].length;
      }
      this.dimensions = dimensions;
      this.size = size;
    }

    @SuppressWarnings("PMD.OnlyOneReturn") // readability
    @Override
    public Iterator<List<T>> iterator() {
      if (size == 0) {
        return Collections.emptyListIterator();
      }
      return new CartesianProductIterator<>(dimensions);
    }

    // /**
    // * Get a stream of list items, representing each Cartesian product, based on
    // * this iterator.
    // *
    // * @return a stream of list items representing each Cartesian product
    // */
    // @NonNull
    // public Stream<List<T>> stream() {
    // int characteristics = Spliterator.ORDERED | Spliterator.SIZED |
    // Spliterator.IMMUTABLE;
    // return StreamSupport.stream(Spliterators.spliterator(iterator(), size,
    // characteristics), false);
    // }
  }
}
