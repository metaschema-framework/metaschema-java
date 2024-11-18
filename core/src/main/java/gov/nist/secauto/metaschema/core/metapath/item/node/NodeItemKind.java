
package gov.nist.secauto.metaschema.core.metapath.item.node;

/**
 * This enumeration provides a listing of the available kinds of
 * {@link INodeItem} implementations.
 */
public enum NodeItemKind {
  /**
   * An {@link INodeItem} based on a Metaschema (@link {@link IModuleNodeItem}}).
   */
  METASCHEMA,
  /**
   * An {@link INodeItem} based on data represented using a Metaschema-based model
   * (@link {@link IDocumentNodeItem}}).
   */
  DOCUMENT,
  ASSEMBLY,
  FIELD,
  FLAG;
}
