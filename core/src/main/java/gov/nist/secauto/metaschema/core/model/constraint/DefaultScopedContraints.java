/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.IModule;

import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultScopedContraints implements IScopedContraints {
  @NonNull
  private final URI namespace;
  @NonNull
  private final String shortName;
  @NonNull
  private final List<ITargetedConstraints> targetedConstraints;

  /**
   * Construct a new set of scoped constraints.
   *
   * @param namespace
   *          the associated Module namespace
   * @param shortName
   *          the associated Module short name
   * @param targetedConstraints
   *          the set of constraints
   * @see IModule#getXmlNamespace()
   * @see IModule#getShortName()
   */
  public DefaultScopedContraints(
      @NonNull URI namespace,
      @NonNull String shortName,
      @NonNull List<ITargetedConstraints> targetedConstraints) {
    this.namespace = namespace;
    this.shortName = shortName;
    this.targetedConstraints = targetedConstraints;
  }

  @Override
  public URI getModuleNamespace() {
    return namespace;
  }

  @Override
  public String getModuleShortName() {
    return shortName;
  }

  @Override
  public List<ITargetedConstraints> getTargetedContraints() {
    return targetedConstraints;
  }

}
