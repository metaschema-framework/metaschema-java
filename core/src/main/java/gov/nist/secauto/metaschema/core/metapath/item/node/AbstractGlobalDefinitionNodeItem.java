
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a node item based on a global definition from a Metaschema.
 *
 * @param <D>
 *          the Java type of the associated Metaschema module definition
 * @param <I>
 *          the Java type of the associated Metaschema module instance
 */
public abstract class AbstractGlobalDefinitionNodeItem<D extends IDefinition, I extends INamedInstance>
    extends AbstractDefinitionNodeItem<D, I> {
  @NonNull
  private final IModuleNodeItem metaschemaNodeItem;

  /**
   * Construct a new Metaschema definition-based node item.
   *
   * @param definition
   *          the Metaschema definition the node item is an instance of
   * @param metaschemaNodeItem
   *          the Metaschema module containing this definition
   */
  protected AbstractGlobalDefinitionNodeItem(
      @NonNull D definition,
      @NonNull IModuleNodeItem metaschemaNodeItem) {
    super(definition);
    this.metaschemaNodeItem = metaschemaNodeItem;
  }

  /**
   * Get the parent module containing this item.
   *
   * @return the module node item
   */
  @NonNull
  protected IModuleNodeItem getModuleNodeItem() {
    return metaschemaNodeItem;
  }

  @Override
  @NonNull
  public IModuleNodeItem getParentNodeItem() {
    return getModuleNodeItem();
  }

  @Override
  public URI getBaseUri() {
    return getModuleNodeItem().getDocumentUri();
  }

  @Override
  public StaticContext getStaticContext() {
    return getModuleNodeItem().getStaticContext();
  }

  @Override
  public String stringValue() {
    return "";
  }

  @Override
  protected String getValueSignature() {
    return null;
  }
}
