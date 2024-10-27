/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides support for maintaining a set of Metaschema constraints.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class AssemblyConstraintSet
    extends ValueConstraintSet
    implements IModelConstrained {

  @NonNull
  private final List<IIndexConstraint> indexConstraints = new LinkedList<>();
  @NonNull
  private final List<IUniqueConstraint> uniqueConstraints = new LinkedList<>();
  @NonNull
  private final List<ICardinalityConstraint> cardinalityConstraints = new LinkedList<>();

  @Override
  public List<IIndexConstraint> getIndexConstraints() {
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      return CollectionUtil.unmodifiableList(indexConstraints);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public List<IUniqueConstraint> getUniqueConstraints() {
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      return CollectionUtil.unmodifiableList(uniqueConstraints);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public List<ICardinalityConstraint> getHasCardinalityConstraints() {
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      return CollectionUtil.unmodifiableList(cardinalityConstraints);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IIndexConstraint constraint) {
    Lock writeLock = instanceLock.writeLock();
    writeLock.lock();
    try {
      constraints.add(constraint);
      indexConstraints.add(constraint);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IUniqueConstraint constraint) {
    Lock writeLock = instanceLock.writeLock();
    writeLock.lock();
    try {
      constraints.add(constraint);
      uniqueConstraints.add(constraint);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull ICardinalityConstraint constraint) {
    Lock writeLock = instanceLock.writeLock();
    writeLock.lock();
    try {
      constraints.add(constraint);
      cardinalityConstraints.add(constraint);
    } finally {
      writeLock.unlock();
    }
  }
}
