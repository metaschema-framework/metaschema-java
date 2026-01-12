
package dev.metaschema.core.metapath.item.node;

import java.net.URI;

import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.model.IDefinition;
import dev.metaschema.core.model.INamedInstance;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A base implementation of a node item for a Metaschema definition that exists
 * without a parent context (orphaned).
 *
 * @param <D>
 *          the Java type of the definition
 * @param <I>
 *          the Java type of the instance
 */
public abstract class AbstractOrphanedDefinitionNodeItem<D extends IDefinition, I extends INamedInstance>
    extends AbstractDefinitionNodeItem<D, I> {

  @Nullable
  private final URI baseUri;
  @NonNull
  private final StaticContext staticContext;

  /**
   * Construct a new orphaned definition node item.
   *
   * @param definition
   *          the Metaschema definition this node represents
   * @param baseUri
   *          the base URI for resolving relative references, or {@code null} if
   *          not applicable
   */
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
