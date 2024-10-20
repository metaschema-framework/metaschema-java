/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Deprecated(since = "1.3.0", forRemoval = true)
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class ExternalConstraintsModulePostProcessor
    extends gov.nist.secauto.metaschema.core.model.constraint.ExternalConstraintsModulePostProcessor {

  /**
   * This implementation has been moved to
   * {@link gov.nist.secauto.metaschema.core.model.constraint.ExternalConstraintsModulePostProcessor},
   * which should be used instead.
   *
   * @param additionalConstraintSets
   *          constraints to configure
   */
  public ExternalConstraintsModulePostProcessor(@NonNull Collection<IConstraintSet> additionalConstraintSets) {
    super(additionalConstraintSets);
  }

}
