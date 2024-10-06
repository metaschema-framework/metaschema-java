/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ValueConstraintSet implements IValueConstrained { // NOPMD - intentional
  @SuppressWarnings("PMD.UseConcurrentHashMap") // need ordering
  @NonNull
  private final Map<QName, ILet> lets = new LinkedHashMap<>();
  @NonNull
  private final List<IConstraint> constraints = new LinkedList<>();
  @NonNull
  private final List<IAllowedValuesConstraint> allowedValuesConstraints = new LinkedList<>();
  @NonNull
  private final List<IMatchesConstraint> matchesConstraints = new LinkedList<>();
  @NonNull
  private final List<IIndexHasKeyConstraint> indexHasKeyConstraints = new LinkedList<>();
  @NonNull
  private final List<IExpectConstraint> expectConstraints = new LinkedList<>();
  @NonNull
  protected final Lock instanceLock = new ReentrantLock();

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
    try {
      instanceLock.lock();
      return constraints;
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public List<IAllowedValuesConstraint> getAllowedValuesConstraints() {
    try {
      instanceLock.lock();
      return allowedValuesConstraints;
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public List<IMatchesConstraint> getMatchesConstraints() {
    try {
      instanceLock.lock();
      return matchesConstraints;
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public List<IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
    try {
      instanceLock.lock();
      return indexHasKeyConstraints;
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public List<IExpectConstraint> getExpectConstraints() {
    try {
      instanceLock.lock();
      return expectConstraints;
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IAllowedValuesConstraint constraint) {
    try {
      instanceLock.lock();
      constraints.add(constraint);
      allowedValuesConstraints.add(constraint);
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IMatchesConstraint constraint) {
    try {
      instanceLock.lock();
      constraints.add(constraint);
      matchesConstraints.add(constraint);
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IIndexHasKeyConstraint constraint) {
    try {
      instanceLock.lock();
      constraints.add(constraint);
      indexHasKeyConstraints.add(constraint);
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IExpectConstraint constraint) {
    try {
      instanceLock.lock();
      constraints.add(constraint);
      expectConstraints.add(constraint);
    } finally {
      instanceLock.unlock();
    }
  }
}
