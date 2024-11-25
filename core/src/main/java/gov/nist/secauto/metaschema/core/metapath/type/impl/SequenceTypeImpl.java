/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.metapath.type.Occurrence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A concrete implementation that supports
 * <a href="https://www.w3.org/TR/xpath-31/#id-sequencetype-syntax">sequence
 * type</a> testing.
 */
public class SequenceTypeImpl implements ISequenceType {
  @NonNull
  private static final ISequenceType EMPTY = new ISequenceType() {
    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public IItemType getType() {
      return IItemType.item();
    }

    @Override
    public Occurrence getOccurrence() {
      return Occurrence.ZERO;
    }

    @Override
    public String toSignature() {
      return "()";
    }

    @Override
    public boolean matches(ICollectionValue item) {
      return false;
    }
  };

  @NonNull
  private final IItemType type;
  @NonNull
  private final Occurrence occurrence;

  /**
   * Matches an empty sequence.
   *
   * @return the empty sequence type
   */
  @NonNull
  public static ISequenceType empty() {
    return EMPTY;
  }

  /**
   * Construct a new sequence type.
   *
   * @param type
   *          the type of items in the sequence
   * @param occurrence
   *          the occurrence of items in the sequence
   */
  public SequenceTypeImpl(@NonNull IItemType type, @NonNull Occurrence occurrence) {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(occurrence, "occurrence");
    this.type = type;
    this.occurrence = occurrence;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public IItemType getType() {
    return type;
  }

  @Override
  public Occurrence getOccurrence() {
    return occurrence;
  }

  @Override
  public String toString() {
    return toSignature();
  }

  @Override
  public String toSignature() {
    StringBuilder builder = new StringBuilder();

    IItemType type = getType();
    // name
    builder.append(type.toSignature())
        // occurrence
        .append(getOccurrence().getIndicator());

    return ObjectUtils.notNull(builder.toString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(occurrence, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true; // NOPMD - readability
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false; // NOPMD - readability
    }
    ISequenceType other = (ISequenceType) obj;
    return Objects.equals(occurrence, other.getOccurrence()) && Objects.equals(type, other.getType());
  }

  @Override
  public boolean matches(ICollectionValue item) {
    ISequence<?> sequence = item instanceof IItem ? ((IItem) item).toSequence() : (ISequence<?>) item;

    boolean retval;
    // check the occurrence matches
    int size = sequence.size();
    switch (getOccurrence()) {
    case ONE:
      retval = size == 1;
      break;
    case ONE_OR_MORE:
      retval = size >= 1;
      break;
    case ZERO:
      retval = size == 0;
      break;
    case ZERO_OR_MORE:
      retval = true;
      break;
    case ZERO_OR_ONE:
      retval = size <= 1;
      break;
    default:
      throw new UnsupportedOperationException(
          String.format("Unsupported occurrence type '%s'.", getOccurrence().name()));
    }

    if (retval) {
      // check the item type matches
      IItemType type = getType();
      retval = sequence.stream().allMatch(type::isInstance);
    }
    return retval;
  }
}
