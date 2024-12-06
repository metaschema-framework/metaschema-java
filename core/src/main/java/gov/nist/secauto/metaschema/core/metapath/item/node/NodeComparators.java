
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides methods for comparing nodes according to the
 * <a href="https://www.w3.org/TR/xpath-functions-31/#func-deep-equal">Metapath
 * specification</a>.
 */
public final class NodeComparators {
  private static final Comparator<IFlagNodeItem> FLAG_SORT
      = Comparator.comparing(IFlagNodeItem::getQName, IEnhancedQName::compareTo)
          .thenComparing(IFlagNodeItem::toAtomicItem, IAnyAtomicItem::compareTo);
  private static final Comparator<INodeItem> NODE_ITEM
      = Comparator.comparing(INodeItem::getNodeType)
          .thenComparing(INodeItem::getFlags, NodeComparators::compareFlags)
          .thenComparing(INodeItem::getModelItems, NodeComparators::compareModelItems);
  private static final Comparator<IFieldNodeItem> FIELD_NODE_ITEM
      = Comparator.comparing(IFieldNodeItem::toAtomicItem, IAnyAtomicItem::compareTo);

  /**
   * Compare two node items for equality.
   *
   * @param item1
   *          the first item to compare
   * @param item2
   *          the second item to compare
   * @return a negative integer, zero, or a positive integer if the first argument
   *         is less than, equal to, or greater than the second.
   */
  public static int compareNodeItem(@NonNull INodeItem item1, @NonNull INodeItem item2) {
    return NODE_ITEM.compare(item1, item2);
  }

  /**
   * Compare two node items for equality.
   *
   * @param item1
   *          the first item to compare
   * @param item2
   *          the second item to compare
   * @return a negative integer, zero, or a positive integer if the first argument
   *         is less than, equal to, or greater than the second.
   */
  public static int compareNodeItem(@NonNull IFlagNodeItem item1, @NonNull IFlagNodeItem item2) {
    return FLAG_SORT.compare(item1, item2);
  }

  /**
   * Compare two node items for equality.
   *
   * @param item1
   *          the first item to compare
   * @param item2
   *          the second item to compare
   * @return a negative integer, zero, or a positive integer if the first argument
   *         is less than, equal to, or greater than the second.
   */
  public static int compareNodeItem(@NonNull IModelNodeItem<?, ?> item1, @NonNull IModelNodeItem<?, ?> item2) {
    return getComparator(item1).compare(item1, item2);
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  private static int compareFlags(
      @NonNull Collection<? extends IFlagNodeItem> flags1,
      @NonNull Collection<? extends IFlagNodeItem> flags2) {

    Comparator<Collection<? extends IFlagNodeItem>> bySize = Comparator.comparingInt(Collection::size);
    int delta = bySize.compare(flags1, flags2);
    if (delta != 0) {
      return delta;
    }

    // sort the collections to compare in an order independent way
    List<IFlagNodeItem> list1 = new ArrayList<>(flags1);
    List<IFlagNodeItem> list2 = new ArrayList<>(flags2);
    Collections.sort(list1, FLAG_SORT);
    Collections.sort(list2, FLAG_SORT);

    // compare the results
    for (int i = 0; i < list1.size(); i++) {
      int compareResult = compareNodeItem(
          ObjectUtils.requireNonNull(list1.get(i)),
          ObjectUtils.requireNonNull(list2.get(i)));
      if (compareResult != 0) {
        return compareResult;
      }
    }
    return 0;
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  private static int compareModelItems(
      @NonNull Collection<? extends List<? extends IModelNodeItem<?, ?>>> items1,
      @NonNull Collection<? extends List<? extends IModelNodeItem<?, ?>>> items2) {

    Comparator<Collection<? extends List<? extends IModelNodeItem<?, ?>>>> bySize
        = Comparator.comparingInt(Collection::size);
    int delta = bySize.compare(items1, items2);
    if (delta != 0) {
      return delta;
    }

    Iterator<? extends List<? extends IModelNodeItem<?, ?>>> thisIterator = items1.iterator();
    Iterator<? extends List<? extends IModelNodeItem<?, ?>>> otherIterator = items2.iterator();
    while (thisIterator.hasNext() && otherIterator.hasNext()) {
      List<? extends IModelNodeItem<?, ?>> l1 = thisIterator.next();
      List<? extends IModelNodeItem<?, ?>> l2 = otherIterator.next();

      Iterator<? extends IModelNodeItem<?, ?>> il1 = l1.iterator();
      Iterator<? extends IModelNodeItem<?, ?>> il2 = l2.iterator();
      while (thisIterator.hasNext() && otherIterator.hasNext()) {
        IModelNodeItem<?, ?> item1 = ObjectUtils.requireNonNull(il1.next());
        IModelNodeItem<?, ?> item2 = ObjectUtils.requireNonNull(il2.next());

        int result = compareNodeItem(item1, item2);
        if (result != 0) {
          return result;
        }
      }
    }
    return 0;
  }

  @NonNull
  private static Comparator<IModelNodeItem<?, ?>> getComparator(@NonNull IModelNodeItem<?, ?> item) {
    Comparator<IModelNodeItem<?, ?>> retval;
    if (item instanceof IAssemblyNodeItem) {
      retval = NodeComparators::compareAsAssembly;
    } else if (item instanceof IFieldNodeItem) {
      retval = NodeComparators::compareAsField;
    } else {
      throw new UnsupportedOperationException("Unsupported model node item type: " + item.getClass().getName());
    }
    return retval;
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  private static int compareAsField(@NonNull IModelNodeItem<?, ?> item1, @NonNull IModelNodeItem<?, ?> item2) {
    int result = NODE_ITEM.compare(item1, item2);
    if (result != 0) {
      return result;
    }
    return FIELD_NODE_ITEM.compare((IFieldNodeItem) item1, (IFieldNodeItem) item2);
  }

  private static int compareAsAssembly(@NonNull IModelNodeItem<?, ?> item1, @NonNull IModelNodeItem<?, ?> item2) {
    return NODE_ITEM.compare(item1, item2);
  }

  private NodeComparators() {
    // disable construction
  }
}
