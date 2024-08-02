/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultConstraintSet implements IConstraintSet {
  @NonNull
  private final URI resourceLocation;
  @NonNull
  private final Set<IConstraintSet> importedConstraintSets;
  @NonNull
  private final Map<QName, List<IScopedContraints>> scopedContraints;

  /**
   * Construct a new constraint set.
   *
   * @param resourceLocation
   *          the resource the constraint was provided from
   * @param scopedContraints
   *          a set of constraints qualified by a scope path
   * @param importedConstraintSets
   *          constraint sets imported by this constraint set
   */
  @SuppressWarnings("null")
  public DefaultConstraintSet(
      @NonNull URI resourceLocation,
      @NonNull List<IScopedContraints> scopedContraints,
      @NonNull Set<IConstraintSet> importedConstraintSets) {
    this.resourceLocation = resourceLocation;
    this.scopedContraints = scopedContraints.stream()
        .collect(
            Collectors.collectingAndThen(
                Collectors.groupingBy(
                    scope -> new QName(scope.getModuleNamespace().toString(), scope.getModuleShortName()),
                    Collectors.toUnmodifiableList()),
                Collections::unmodifiableMap));
    this.importedConstraintSets = CollectionUtil.unmodifiableSet(importedConstraintSets);
  }

  /**
   * Get the resource the constraint was provided from.
   *
   * @return the resource
   */
  @NonNull
  protected URI getResourceLocation() {
    return resourceLocation;
  }

  /**
   * Get the set of Metaschema scoped constraints to apply by a {@link QName}
   * formed from the Metaschema namespace and short name.
   *
   * @return the mapping of QName to scoped constraints
   */
  @NonNull
  public Map<QName, List<IScopedContraints>> getScopedContraints() {
    return scopedContraints;
  }

  @Override
  public Set<IConstraintSet> getImportedConstraintSets() {
    return importedConstraintSets;
  }

  @Override
  public Iterable<ITargetedConstraints> getTargetedConstraintsForModule(@NonNull IModule module) {
    QName qname = module.getQName();

    Map<QName, List<IScopedContraints>> map = getScopedContraints();
    List<IScopedContraints> scopes = map.getOrDefault(qname, CollectionUtil.emptyList());
    return CollectionUtil.toIterable(ObjectUtils.notNull(scopes.stream()
        .flatMap(scoped -> scoped.getTargetedContraints().stream())));
  }

}
