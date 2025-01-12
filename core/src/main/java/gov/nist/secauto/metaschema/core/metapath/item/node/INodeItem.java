
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.format.IPathSegment;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.IItemVisitor;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a Metapath model node.
 */
public interface INodeItem extends IItem, IPathSegment, INodeItemVisitable {
  /**
   * The type of node.
   */
  enum NodeType {
    MODULE,
    DOCUMENT,
    ASSEMBLY,
    FIELD,
    FLAG;
  }

  /**
   * Get the node type for the node item.
   *
   * @return the node type
   */
  @NonNull
  NodeType getNodeType();

  /**
   * Get the static type information of the node item.
   *
   * @return the item type
   */
  @NonNull
  static IItemType type() {
    return IItemType.node();
  }

  /**
   * Retrieve the relative position of this node relative to sibling nodes.
   * <p>
   * A singleton item in a sequence will have a position value of {@code 1}.
   * <p>
   * The value {@code 1} is used as the starting value to align with the XPath
   * specification.
   *
   * @return a positive integer value designating this instance's position within
   *         a collection
   */
  default int getPosition() {
    // only model node items have positions other than 1
    return 1;
  }

  /**
   * Generate a path for this node in the directed node graph, using the provided
   * path formatter.
   */
  @Override
  String format(IPathFormatter formatter);

  /**
   * Gets the value of the provided node item.
   * <p>
   * If the provided node item is a document, this method get the first child node
   * item's value, since a document doesn't have a value.
   *
   * @param <CLASS>
   *          the type of the bound object to return
   * @param item
   *          the node item to get the value of
   * @return a bound object
   * @throws NullPointerException
   *           if the node item has no associated value
   */
  @SuppressWarnings("unchecked")
  @NonNull
  static <CLASS> CLASS toValue(@NonNull INodeItem item) {
    INodeItem valuedItem;
    if (item instanceof IDocumentNodeItem) {
      // get first child item, since the document has no value
      valuedItem = item.modelItems().findFirst().get();
    } else {
      valuedItem = item;
    }
    return ObjectUtils.requireNonNull((CLASS) valuedItem.getValue());
  }

  /**
   * Retrieve the parent node item if it exists.
   *
   * @return the parent node item, or {@code null} if this node item has no known
   *         parent
   */
  INodeItem getParentNodeItem();

  /**
   * Retrieve the parent content node item if it exists. A content node is a
   * non-document node.
   *
   * @return the parent content node item, or {@code null} if this node item has
   *         no known parent content node item
   */
  IModelNodeItem<?, ?> getParentContentNodeItem();

  /**
   * Get the kind of node item this is.
   *
   * @return the node item's kind
   */
  @NonNull
  NodeItemKind getNodeItemKind();

  /**
   * Retrieve the base URI of this node.
   * <p>
   * The base URI of a node will be in order of preference:
   * <ol>
   * <li>the base URI defined on the node
   * <li>the base URI defined on the nearest ancestor node
   * <li>the base URI defined on the document node
   * <li>{@code null} if the document node is unknown
   * </ol>
   *
   * @return the base URI or {@code null} if it is unknown
   */
  URI getBaseUri();

  /**
   * Get the path for this node item as a Metapath.
   *
   * @return the Metapath
   */
  @NonNull
  default String getMetapath() {
    return toPath(IPathFormatter.METAPATH_PATH_FORMATER);
  }

  @Override
  default Stream<? extends INodeItem> getPathStream() {
    INodeItem parent = getParentNodeItem();
    return ObjectUtils.notNull(
        parent == null ? Stream.of(this) : Stream.concat(getParentNodeItem().getPathStream(), Stream.of(this)));
  }

  /**
   * Get a stream of all ancestors of this node item. The stream is ordered from
   * closest to farthest ancestor.
   *
   * @return a stream of ancestor node items
   */
  @NonNull
  default Stream<? extends INodeItem> ancestor() {
    return ancestorsOf(this);
  }

  /**
   * Get a stream of this and all ancestors of this node item. The stream is
   * ordered from self, then closest to farthest ancestor.
   *
   * @return a stream of this node followed by all ancestor node items
   */
  @NonNull
  default Stream<? extends INodeItem> ancestorOrSelf() {
    return ObjectUtils.notNull(Stream.concat(ancestor(), Stream.of(this)));
  }

  /**
   * Get a stream of the ancestors of the provided {@code item}. The stream is
   * ordered from the farthest ancestor to the closest.
   *
   * @param item
   *          the target item to get ancestors for
   *
   * @return a stream of all ancestor node items
   */
  @NonNull
  static Stream<? extends INodeItem> ancestorsOf(@NonNull INodeItem item) {
    INodeItem parent = item.getParentNodeItem();
    return ObjectUtils.notNull(parent == null ? Stream.empty() : Stream.concat(ancestorsOf(parent), Stream.of(parent)));
  }

  /**
   * Get a stream of all descendant model items of this node item. The stream is
   * ordered from closest to farthest descendants in a depth-first order.
   *
   * @return a stream of descendant node items
   */
  @NonNull
  default Stream<? extends IModelNodeItem<?, ?>> descendant() {
    return decendantsOf(this);
  }

  /**
   * Get a stream of all descendant model items of the provided {@code item}. The
   * stream is ordered from closest to farthest descendants in a depth-first
   * order.
   *
   * @param item
   *          the target item to get descendants for
   *
   * @return a stream of descendant node items
   */
  @NonNull
  static Stream<? extends IModelNodeItem<?, ?>> decendantsOf(@NonNull INodeItem item) {
    Stream<? extends IModelNodeItem<?, ?>> children = item.modelItems();

    return ObjectUtils.notNull(children.flatMap(child -> {
      assert child != null;
      return Stream.concat(Stream.of(child), decendantsOf(child));
    }));
  }

  /**
   * Get a stream of this node, followed by all descendant model items of this
   * node item. The stream is ordered from closest to farthest descendants in a
   * depth-first order.
   *
   * @return a stream of this node and descendant node items
   */
  @NonNull
  default Stream<? extends INodeItem> descendantOrSelf() {
    return ObjectUtils.notNull(Stream.concat(Stream.of(this), descendant()));
  }

  /**
   * Get the children of this node's parent that occur after this node in a
   * depth-first order.
   *
   * @return a stream of nodes
   */
  @NonNull
  default Stream<? extends IModelNodeItem<?, ?>> followingSibling() {
    return ObjectUtils.notNull(Stream.empty());
  }

  /**
   * Get the children of this node's parent, and their descendants, that occur
   * before this node in a depth-first order.
   *
   * @return a stream of nodes
   */
  @NonNull
  default Stream<? extends IModelNodeItem<?, ?>> precedingSibling() {
    return ObjectUtils.notNull(Stream.empty());
  }

  /**
   * Get the children of this node's parent, and their descendants, that occur
   * before this node in a depth-first order.
   *
   * @return a stream of nodes
   */
  default Stream<? extends IModelNodeItem<?, ?>> following() {
    return ObjectUtils.notNull(Stream.empty());
  }

  /**
   * Get the children of this node's parent, and their descendants, that occur
   * after this node in a depth-first order.
   *
   * @return a stream of nodes
   */
  default Stream<? extends IModelNodeItem<?, ?>> preceding() {
    return ObjectUtils.notNull(Stream.empty());
  }

  /**
   * Get the flags and value data associated this node. The resulting collection
   * is expected to be ordered, with the results in document order.
   * <p>
   * The resulting collection may be modified, but such modification is not thread
   * safe
   *
   * @return a collection of flags
   */
  @NonNull
  Collection<? extends IFlagNodeItem> getFlags();

  /**
   * Lookup a flag and value data on this node by it's effective qualified name.
   *
   * @param name
   *          the effective qualified name of the flag
   * @return the flag with the matching effective name or {@code null} if no match
   *         was found
   */
  @Nullable
  IFlagNodeItem getFlagByName(@NonNull IEnhancedQName name);

  /**
   * Get the flags and value data associated with this node as a stream.
   *
   * @return the stream of flags or an empty stream if none exist
   */
  @SuppressWarnings("null")
  @NonNull
  default Stream<? extends IFlagNodeItem> flags() {
    return getFlags().stream();
  }

  /**
   * Get the model items (i.e., fields, assemblies) and value data associated this
   * node. A given model instance can be multi-valued, so the value of each
   * instance will be a list. The resulting collection is expected to be ordered,
   * with the results in document order.
   * <p>
   * The resulting collection may be modified, but such modification is not thread
   * safe
   *
   * @return a collection of list(s), with each list containing the items for a
   *         given model instance
   */
  @NonNull
  Collection<? extends List<? extends IModelNodeItem<?, ?>>> getModelItems();

  /**
   * Get the collection of model items associated with the instance having the
   * provided {@code name}.
   * <p>
   * The resulting collection may be modified, but such modification is not thread
   * safe
   *
   * @param name
   *          the instance name to get model items for
   * @return the sequence of items associated with the named model instance, or an
   *         empty list if an instance with that name is not present
   */
  @NonNull
  List<? extends IModelNodeItem<?, ?>> getModelItemsByName(@NonNull IEnhancedQName name);

  /**
   * Get the model items (i.e., fields, assemblies) and value data associated this
   * node as a stream.
   *
   * @return the stream of model items or an empty stream if none exist
   */
  @SuppressWarnings("null")
  @NonNull
  default Stream<? extends IModelNodeItem<?, ?>> modelItems() {
    return getModelItems().stream().flatMap(Collection::stream);
  }

  /**
   * Get the resource location information for the node, if known.
   *
   * @return the resource location information, or {@code null} if not known
   */
  @Nullable
  IResourceLocation getLocation();

  /**
   * Get the string value of the node.
   *
   * @return the string value of the node or an empty string if it has no value.
   */
  @NonNull
  String stringValue();

  /**
   * Get the static context to use to query this node item.
   *
   * @return the static context
   */
  @NonNull
  StaticContext getStaticContext();

  @Override
  default void accept(IItemVisitor visitor) {
    visitor.visit(this);
  }
}
