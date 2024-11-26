
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
 *          the definition type
 */
abstract class AbstractGlobalDefinitionNodeItem<D extends IDefinition, I extends INamedInstance>
    extends AbstractDefinitionNodeItem<D, I> {
  @NonNull
  private final IModuleNodeItem metaschemaNodeItem;

  protected AbstractGlobalDefinitionNodeItem(
      @NonNull D definition,
      @NonNull IModuleNodeItem metaschemaNodeItem) {
    super(definition);
    this.metaschemaNodeItem = metaschemaNodeItem;
  }

  @NonNull
  protected IModuleNodeItem getMetaschemaNodeItem() {
    return metaschemaNodeItem;
  }

  @Override
  @NonNull
  public IModuleNodeItem getParentNodeItem() {
    return getMetaschemaNodeItem();
  }

  @Override
  public URI getBaseUri() {
    return getMetaschemaNodeItem().getDocumentUri();
  }

  @Override
  public StaticContext getStaticContext() {
    return getMetaschemaNodeItem().getStaticContext();
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
