
package dev.metaschema.core.metapath.item.node;

import dev.metaschema.core.model.IDefinition;
import dev.metaschema.core.model.INamedInstance;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base implementation of a node item backed by a Metaschema definition.
 *
 * @param <D>
 *          the Java type of the definition
 * @param <I>
 *          the Java type of the instance
 */
public abstract class AbstractDefinitionNodeItem<D extends IDefinition, I extends INamedInstance>
    extends AbstractNodeItem
    implements IFeatureOrhpanedDefinitionNodeItem<D, I> {

  @NonNull
  private final D definition;

  /**
   * Construct a new node item for the provided definition.
   *
   * @param definition
   *          the Metaschema definition this node represents
   */
  public AbstractDefinitionNodeItem(@NonNull D definition) {
    this.definition = definition;
  }

  @Override
  public D getDefinition() {
    return definition;
  }
}
