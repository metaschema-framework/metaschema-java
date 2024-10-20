
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAtomicValuedItem;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A Metapath node valued item representing a Metaschema module flag.
 */

public interface IFlagNodeItem
    extends IDefinitionNodeItem<IFlagDefinition, IFlagInstance>, IAtomicValuedItem {
  @Override
  default NodeItemType getNodeItemType() {
    return NodeItemType.FLAG;
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
  default IFlagNodeItem getFlagByName(@NonNull QName name) {
    // a flag does not have flags
    return null;
  }

  /**
   * FlagContainer do not have flag items. This call should return an empty
   * stream.
   */
  @SuppressWarnings("null")
  @Override
  default @NonNull Stream<? extends IFlagNodeItem> flags() {
    // a flag does not have flags
    return Stream.empty();
  }

  /**
   * FlagContainer do not have model items. This call should return an empty
   * collection.
   */
  @SuppressWarnings("null")
  @Override
  default @NonNull Collection<? extends List<? extends IModelNodeItem<?, ?>>> getModelItems() {
    // a flag does not have model items
    return Collections.emptyList();
  }

  /**
   * FlagContainer do not have model items. This call should return an empty list.
   */
  @SuppressWarnings("null")
  @Override
  default List<? extends IModelNodeItem<?, ?>> getModelItemsByName(QName name) {
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
  default @NonNull String format(@NonNull IPathFormatter formatter) {
    return formatter.formatFlag(this);
  }

  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull INodeItemVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitFlag(this, context);
  }
}
