/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.LinkedList;
import java.util.List;

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
    try {
      instanceLock.lock();
      return indexConstraints;
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public List<IUniqueConstraint> getUniqueConstraints() {
    try {
      instanceLock.lock();
      return uniqueConstraints;
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public List<ICardinalityConstraint> getHasCardinalityConstraints() {
    try {
      instanceLock.lock();
      return cardinalityConstraints;
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IIndexConstraint constraint) {
    try {
      instanceLock.lock();
      getConstraints().add(constraint);
      indexConstraints.add(constraint);
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull IUniqueConstraint constraint) {
    try {
      instanceLock.lock();
      getConstraints().add(constraint);
      uniqueConstraints.add(constraint);
    } finally {
      instanceLock.unlock();
    }
  }

  @Override
  public final void addConstraint(@NonNull ICardinalityConstraint constraint) {
    try {
      instanceLock.lock();
      getConstraints().add(constraint);
      cardinalityConstraints.add(constraint);
    } finally {
      instanceLock.unlock();
    }
  }

}
