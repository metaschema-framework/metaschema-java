/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.metapath.type.Occurrence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

public class SequenceTypeImpl implements ISequenceType {
  @NonNull
  public static final ISequenceType EMPTY = new ISequenceType() {
    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public IItemType getType() {
      return null;
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
    builder.append(type == null
        ? ""
        : type.toSignature())
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
    throw new UnsupportedOperationException("implement");
  }
}