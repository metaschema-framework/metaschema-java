/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.util;

import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDiagramNodeVisitor {
  /**
   * Handle an edge based on a {@link INamedModelInstanceAbsolute}.
   *
   * @param edge
   *          the edge
   */
  void visit(@NonNull DefaultDiagramNode.ModelEdge edge);

  /**
   * Handle an edge based on a {@link INamedModelInstanceAbsolute} that is a
   * member of a {@link IChoiceInstance}.
   *
   * @param edge
   *          the edge
   */
  void visit(@NonNull DefaultDiagramNode.ChoiceEdge edge);

  /**
   * Handle an edge based on a {@link INamedModelInstanceGrouped} that is a member
   * of a {@link IChoiceGroupInstance}.
   *
   * @param edge
   *          the edge
   */
  void visit(@NonNull DefaultDiagramNode.ChoiceGroupEdge edge);
}
