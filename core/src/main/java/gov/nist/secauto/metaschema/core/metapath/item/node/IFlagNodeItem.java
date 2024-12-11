
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A Metapath node valued item representing a Metaschema module flag.
 */

public interface IFlagNodeItem
    extends IDefinitionNodeItem<IFlagDefinition, IFlagInstance>, IAtomicValuedNodeItem {
  /**
   * Get the static type information of the node item.
   *
   * @return the item type
   */
  @NonNull
  static IItemType type() {
    return IItemType.flag();
  }

  @Override
  default NodeItemKind getNodeItemKind() {
    return NodeItemKind.FLAG;
  }

  @Override
  default NodeType getNodeType() {
    return NodeType.FLAG;
  }

  @Override
  default IFlagNodeItem getNodeItem() {
    return this;
  }

  @Override
  IFlagDefinition getDefinition();

  @Override
  IFlagInstance getInstance();

  @Override
  default IKindTest<IFlagNodeItem> getType() {
    StaticContext staticContext = getStaticContext();
    return IItemType.flag(
        getQName(),
        getDefinition().getDefinitionQName().toEQName(staticContext),
        staticContext);
  }

  @Override
  default IAtomicOrUnionType<?> getValueItemType() {
    return getDefinition().getJavaTypeAdapter().getItemType();
  }

  @Override
  @Nullable
  default URI getBaseUri() {
    INodeItem parent = getParentNodeItem();
    return parent == null ? null : parent.getBaseUri();
  }

  /**
   * FlagContainer do not have flag items. This call should return an empty
   * collection.
   */
  @SuppressWarnings("null")
  @Override
  default Collection<? extends IFlagNodeItem> getFlags() {
    // a flag does not have flags
    return Collections.emptyList();
  }

  /**
   * FlagContainer do not have flag items. This call should return {@code null}.
   */
  @Override
  default IFlagNodeItem getFlagByName(@NonNull IEnhancedQName name) {
    // a flag does not have flags
    return null;
  }

  /**
   * FlagContainer do not have flag items. This call should return an empty
   * stream.
   */
  @SuppressWarnings("null")
  @Override
  default @NonNull
  Stream<? extends IFlagNodeItem> flags() {
    // a flag does not have flags
    return Stream.empty();
  }

  /**
   * FlagContainer do not have model items. This call should return an empty
   * collection.
   */
  @SuppressWarnings("null")
  @Override
  default @NonNull
  Collection<? extends List<? extends IModelNodeItem<?, ?>>> getModelItems() {
    // a flag does not have model items
    return Collections.emptyList();
  }

  /**
   * FlagContainer do not have model items. This call should return an empty list.
   */
  @SuppressWarnings("null")
  @Override
  default List<? extends IModelNodeItem<?, ?>> getModelItemsByName(IEnhancedQName name) {
    // a flag does not have model items
    return Collections.emptyList();
  }

  /**
   * FlagContainer do not have model items. This call should return an empty
   * stream.
   */
  @SuppressWarnings("null")
  @NonNull
  @Override
  default Stream<? extends IModelNodeItem<?, ?>> modelItems() {
    // a flag does not have model items
    return Stream.empty();
  }

  @Override
  default @NonNull
  String format(@NonNull IPathFormatter formatter) {
    return formatter.formatFlag(this);
  }

  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull INodeItemVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitFlag(this, context);
  }

  @Override
  default boolean deepEquals(ICollectionValue other) {
    return other instanceof IFlagNodeItem
        && NodeComparators.compareNodeItem(this, (IFlagNodeItem) other) == 0;
  }
}
