
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IModelNodeItem<D extends IModelDefinition, I extends INamedModelInstance>
    extends IDefinitionNodeItem<D, I> {

  @Override
  int getPosition();

  /**
   * {@inheritDoc}
   * <p>
   * The parent can be an assembly or a document (in the case of a root assembly.
   */
  @Override
  INodeItem getParentNodeItem();

  @Override
  IAssemblyNodeItem getParentContentNodeItem();

  @Override
  @NonNull
  default Stream<? extends IModelNodeItem<?, ?>> descendantOrSelf() {
    return ObjectUtils.notNull(Stream.concat(Stream.of(this), descendant()));
  }

  @SuppressWarnings("PMD.CompareObjectsWithEquals")
  @Override
  @NonNull
  default Stream<? extends IModelNodeItem<?, ?>> followingSibling() {
    IModelNodeItem<?, ?> parent = getParentContentNodeItem();
    return ObjectUtils.notNull(parent == null
        ? Stream.empty()
        : parent.modelItems()
            // need to use != vs !Object.equals to ensure we are matching the same object
            .dropWhile(item -> this != item)
            .skip(1));
  }

  @SuppressWarnings("PMD.CompareObjectsWithEquals")
  @Override
  @NonNull
  default Stream<? extends IModelNodeItem<?, ?>> precedingSibling() {
    IModelNodeItem<?, ?> parent = getParentContentNodeItem();
    return ObjectUtils.notNull(parent == null
        ? Stream.empty()
        // need to use != vs !Object.equals to ensure we are matching the same object
        : parent.modelItems().takeWhile(item -> this != item));
  }

  @Override
  default Stream<? extends IModelNodeItem<?, ?>> following() {
    return followingSibling()
        .flatMap(IModelNodeItem::descendantOrSelf);
  }

  @Override
  default Stream<? extends IModelNodeItem<?, ?>> preceding() {
    return precedingSibling()
        .flatMap(IModelNodeItem::descendantOrSelf);
  }
}
