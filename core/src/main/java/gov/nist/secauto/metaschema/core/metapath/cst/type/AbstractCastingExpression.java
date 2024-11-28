/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for compact syntax expressions that perform casting operations.
 */
public abstract class AbstractCastingExpression
    extends AbstractExpression {
  @NonNull
  private final IExpression value;
  @NonNull
  private final IAtomicOrUnionType<?> type;
  private final boolean allowEmptySequence;

  /**
   * Construct a new cast expression.
   *
   * @param value
   *          the expression that will produce the item to cast
   * @param type
   *          the atomic type to cast to
   * @param allowEmptySequence
   *          {@code true} if the value expression is allowed to produce an empty
   *          sequence, or {@code false} otherwise
   */
  public AbstractCastingExpression(
      @NonNull IExpression value,
      @NonNull IAtomicOrUnionType<?> type,
      boolean allowEmptySequence) {
    this.value = value;
    this.type = type;
    this.allowEmptySequence = allowEmptySequence;
  }

  /**
   * Get the value expression used to produce the value to cast.
   *
   * @return the expression
   */
  @NonNull
  public IExpression getValue() {
    return value;
  }

  /**
   * Get the atomic type to cast to.
   *
   * @return the type
   */
  @NonNull
  public IAtomicOrUnionType<?> getType() {
    return type;
  }

  /**
   * Indicates if the value expression is allowed to produce an empty sequence.
   *
   * @return {@code true} if the value expression is allowed to produce an empty
   *         sequence, or {@code false} otherwise
   */
  public boolean isEmptySequenceAllowed() {
    return allowEmptySequence;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return ObjectUtils.notNull(List.of(value));
  }

  /**
   * Perform the cast operation.
   *
   * @param sequence
   *          the sequence to cast, which should contain a single item
   * @return a sequence containing the casted item
   */
  @NonNull
  protected ISequence<IAnyAtomicItem> cast(@NonNull ISequence<?> sequence) {
    IAnyAtomicItem result = type.cast(sequence);

    if (result == null && !allowEmptySequence) {
      throw new InvalidTypeMetapathException(
          null,
          "An empty sequence was used in a cast without the '?' operator.");
    }
    return ISequence.of(result); // empty if null
  }

  @Override
  public String toASTString() {
    return ObjectUtils.notNull(String.format("%s[type=%s, allowEmpty=%s]",
        getClass().getName(),
        getType().toSignature(),
        isEmptySequenceAllowed()));
  }
}
