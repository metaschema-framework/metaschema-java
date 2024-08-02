
package gov.nist.secauto.metaschema.core.metapath.item.node;

/**
 * This enumeration provides a listing of the available types of
 * {@link INodeItem} implementations.
 */
public enum NodeItemType {
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
