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
    synchronized (this) {
      return indexConstraints;
    }
  }

  @Override
  public List<IUniqueConstraint> getUniqueConstraints() {
    synchronized (this) {
      return uniqueConstraints;
    }
  }

  @Override
  public List<ICardinalityConstraint> getHasCardinalityConstraints() {
    synchronized (this) {
      return cardinalityConstraints;
    }
  }

  @Override
  public final void addConstraint(@NonNull IIndexConstraint constraint) {
    synchronized (this) {
      getConstraints().add(constraint);
      indexConstraints.add(constraint);
    }
  }

  @Override
  public final void addConstraint(@NonNull IUniqueConstraint constraint) {
    synchronized (this) {
      getConstraints().add(constraint);
      uniqueConstraints.add(constraint);
    }
  }

  @Override
  public final void addConstraint(@NonNull ICardinalityConstraint constraint) {
    synchronized (this) {
      getConstraints().add(constraint);
      cardinalityConstraints.add(constraint);
    }
  }

}
