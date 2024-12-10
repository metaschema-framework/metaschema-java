
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractOrphanedDefinitionNodeItem<D extends IDefinition, I extends INamedInstance>
    extends AbstractDefinitionNodeItem<D, I> {

  @Nullable
  private final URI baseUri;
  @NonNull
  private final StaticContext staticContext;

  public AbstractOrphanedDefinitionNodeItem(
      @NonNull D definition,
      @Nullable URI baseUri) {
    super(definition);
    this.baseUri = baseUri;
    StaticContext.Builder builder = StaticContext.builder();

    builder.defaultModelNamespace(ObjectUtils.notNull(definition.getQName().getNamespace()));

    if (baseUri != null) {
      builder.baseUri(baseUri);
    }

    this.staticContext = builder.build();
  }

  @Override
  public INodeItem getParentNodeItem() {
    // no parent
    return null;
  }

  @Override
  public URI getBaseUri() {
    return baseUri;
  }

  @Override
  public StaticContext getStaticContext() {
    return staticContext;
  }

  @Override
  protected String getValueSignature() {
    return null;
  }
}
