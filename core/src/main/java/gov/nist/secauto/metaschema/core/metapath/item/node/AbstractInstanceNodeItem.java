
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base implementation of a node item backed by a Metaschema instance.
 *
 * @param <D>
 *          the Java type of the definition
 * @param <I>
 *          the Java type of the instance
 * @param <P>
 *          the Java type of the parent node item
 */
public abstract class AbstractInstanceNodeItem<
    D extends IDefinition,
    I extends INamedInstance,
    P extends IModelNodeItem<? extends IModelDefinition, ? extends INamedInstance>>
    extends AbstractNodeItem
    implements IDefinitionNodeItem<D, I> {

  @NonNull
  private final I instance;
  @NonNull
  private final P parent;

  /**
   * Construct a new instance node item.
   *
   * @param instance
   *          the Metaschema instance this node represents
   * @param parent
   *          the parent node item containing this instance
   */
  public AbstractInstanceNodeItem(
      @NonNull I instance,
      @NonNull P parent) {
    this.instance = instance;
    this.parent = parent;
  }

  @SuppressWarnings("unchecked")
  @Override
  public D getDefinition() {
    return (D) getInstance().getDefinition();
  }

  @Override
  public I getInstance() {
    return instance;
  }

  @Override
  public P getParentNodeItem() {
    return parent;
  }

  @Override
  public P getParentContentNodeItem() {
    return getParentNodeItem();
  }
}
