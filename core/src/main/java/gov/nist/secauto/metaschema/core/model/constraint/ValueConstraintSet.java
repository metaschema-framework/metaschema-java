/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ValueConstraintSet implements IValueConstrained { // NOPMD - intentional
  @SuppressWarnings("PMD.UseConcurrentHashMap") // need ordering
  @NonNull
  private final Map<QName, ILet> lets = new LinkedHashMap<>();
  @NonNull
  protected final List<IConstraint> constraints = new LinkedList<>();
  @NonNull
  private final List<IAllowedValuesConstraint> allowedValuesConstraints = new LinkedList<>();
  @NonNull
  private final List<IMatchesConstraint> matchesConstraints = new LinkedList<>();
  @NonNull
  private final List<IIndexHasKeyConstraint> indexHasKeyConstraints = new LinkedList<>();
  @NonNull
  private final List<IExpectConstraint> expectConstraints = new LinkedList<>();
  @NonNull
  protected final ReadWriteLock instanceLock = new ReentrantReadWriteLock();

  @Override
  public Map<QName, ILet> getLetExpressions() {
    return lets;
  }

  @Override
  public ILet addLetExpression(ILet let) {
    return lets.put(let.getName(), let);
  }

  @Override
  public List<IConstraint> getConstraints() {
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      return CollectionUtil.unmodifiableList(constraints);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public List<IAllowedValuesConstraint> getAllowedValuesConstraints() {
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      return CollectionUtil.unmodifiableList(allowedValuesConstraints);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public List<IMatchesConstraint> getMatchesConstraints() {
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      return CollectionUtil.unmodifiableList(matchesConstraints);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public List<IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      return CollectionUtil.unmodifiableList(indexHasKeyConstraints);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public List<IExpectConstraint> getExpectConstraints() {
    Lock readLock = instanceLock.readLock();
    readLock.lock();
    try {
      return CollectionUtil.unmodifiableList(expectConstraints);
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IAllowedValuesConstraint constraint) {
    Lock writeLock = instanceLock.writeLock();
    writeLock.lock();
    try {
      constraints.add(constraint);
      allowedValuesConstraints.add(constraint);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IMatchesConstraint constraint) {
    Lock writeLock = instanceLock.writeLock();
    writeLock.lock();
    try {
      constraints.add(constraint);
      matchesConstraints.add(constraint);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IIndexHasKeyConstraint constraint) {
    Lock writeLock = instanceLock.writeLock();
    writeLock.lock();
    try {
      constraints.add(constraint);
      indexHasKeyConstraints.add(constraint);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IExpectConstraint constraint) {
    Lock writeLock = instanceLock.writeLock();
    writeLock.lock();
    try {
      constraints.add(constraint);
      expectConstraints.add(constraint);
    } finally {
      writeLock.unlock();
    }
  }
}
