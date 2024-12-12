
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a Metapath node item that is based on an underlying Metaschema
 * module definition and instance.
 *
 * @param <D>
 *          the Java type of the definition associated with a Metaschema module
 * @param <I>
 *          the Java type of the instance associated with a Metaschema module
 */
public interface IDefinitionNodeItem<D extends IDefinition, I extends INamedInstance> extends INodeItem {
  /**
   * Get the name of this node.
   *
   * @return the qualified name
   */
  @NonNull
  default IEnhancedQName getQName() {
    I instance = getInstance();
    return instance == null
        ? getDefinition().getQName()
        : instance.getQName();
  }

  /**
   * Get the Metaschema definition associated with this node.
   *
   * @return the definition
   */
  @NonNull
  D getDefinition();

  /**
   * Retrieve the instance associated with this path segment.
   *
   * @return the instance of the segment, or {@code null} if it doesn't have one
   */
  I getInstance();

  @Override
  @Nullable
  default IResourceLocation getLocation() {
    Object value = getValue();
    return value == null ? null : getDefinition().getLocation(value);
  }
}
