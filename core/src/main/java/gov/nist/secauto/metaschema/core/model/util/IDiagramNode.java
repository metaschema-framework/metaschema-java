/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.util;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModelInstance;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metaschema module {@link IModelDefinition} node that is part of
 * a model diagram.
 */
public interface IDiagramNode {
  /**
   * The Metaschema module definition associated with this node.
   *
   * @return the definition
   */
  @NonNull
  IModelDefinition getDefinition();

  /**
   * The identifier of the node used for cross-referenceing.
   *
   * @return the identifier
   */
  @NonNull
  String getIdentifier();

  /**
   * The human-readable label for the node.
   *
   * @return the label
   */
  @NonNull
  default String getLabel() {
    return getDefinition().getEffectiveName();
  }

  /**
   * Get the sequence of scalar data points associated with the definition.
   *
   * @return the sequence
   */
  @NonNull
  List<IAttribute> getAttributes();

  /**
   * Get the sequence of relationships between this and other nodes.
   *
   * @return the sequence
   */
  @NonNull
  List<IEdge> getEdges();

  /**
   * Represents a scalar-valued data point associated with a node.
   */
  interface IAttribute {
    /**
     * Get the node containing the attribute.
     *
     * @return the node
     */
    @NonNull
    IDiagramNode getNode();

    /**
     * The human-readable label for the attribute.
     *
     * @return the label
     */
    @NonNull
    String getLabel();

    /**
     * Get the data type for the attribute.
     *
     * @return the data type
     */
    @NonNull
    IDataTypeAdapter<?> getDataType();
  }

  /**
   * Represents a relationship between a subject node and a target node.
   */
  interface IEdge {
    /**
     * Get the subject node that owns the edge.
     *
     * @return the node
     */
    @NonNull
    IDiagramNode getNode();

    /**
     * Get the relationship type.
     *
     * @return the relationship
     */
    @NonNull
    Relationship getRelationship();

    /**
     * Get the associated Metaschema definition instance the edge is based on.
     *
     * @return the instance
     */
    @NonNull
    IModelInstance getInstance();

    /**
     * A visitor dispatch method used to process the edge.
     *
     * @param visitor
     *          the visitor to use for dispatch
     */
    void accept(@NonNull IDiagramNodeVisitor visitor);
  }

  /**
   * The nature of a relationship between two nodes.
   */
  enum Relationship {
    ZERO_OR_ONE,
    ONE,
    ZERO_OR_MORE,
    ONE_OR_MORE;

    /**
     * Get the relationship based on the provided grouping information.
     *
     * @param groupable
     *          the grouping information
     * @return the relationship
     */
    @NonNull
    public static Relationship toRelationship(@NonNull IGroupable groupable) {
      return toRelationship(groupable.getMinOccurs(), groupable.getMaxOccurs());
    }

    @NonNull
    private static Relationship toRelationship(int minOccurs, int maxOccurs) {
      return minOccurs < 1
          ? maxOccurs == 1
              ? IDiagramNode.Relationship.ZERO_OR_ONE
              : IDiagramNode.Relationship.ZERO_OR_MORE
          : maxOccurs == 1
              ? IDiagramNode.Relationship.ONE
              : IDiagramNode.Relationship.ONE_OR_MORE;
    }
  }
}
