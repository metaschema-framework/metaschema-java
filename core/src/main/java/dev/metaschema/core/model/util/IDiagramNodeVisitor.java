/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.util;

import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.INamedModelInstanceAbsolute;
import dev.metaschema.core.model.INamedModelInstanceGrouped;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A visitor for processing edges in a diagram node model.
 * <p>
 * Implementations of this interface can traverse and process different types of
 * edges in a Metaschema diagram, dispatching to the appropriate method based on
 * the edge type.
 */
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
