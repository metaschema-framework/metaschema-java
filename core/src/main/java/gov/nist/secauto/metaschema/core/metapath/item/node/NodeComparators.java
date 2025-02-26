
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
import java.util.function.BiPredicate;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides methods for comparing nodes according to the
 * <a href="https://www.w3.org/TR/xpath-functions-31/#func-deep-equal">Metapath
 * specification</a>.
 */
public final class NodeComparators {
  private static final Comparator<IFlagNodeItem> FLAG_SORT
      = Comparator.comparing(IFlagNodeItem::getQName, IEnhancedQName::compareTo);

  /**
   * Compare two node items for equality.
   *
   * @param item1
   *          the first item to compare
   * @param item2
   *          the second item to compare
   * @return {@code true} if both node items are the same type and have the same
   *         flag and model members, or {@code false} otherwise
   */
  public static boolean compareNodeItem(@NonNull INodeItem item1, @NonNull INodeItem item2) {
    return item1.getNodeType().equals(item2.getNodeType())
        && compareFlags(item1.getFlags(), item2.getFlags())
        && compareModelItems(item1.getModelItems(), item2.getModelItems());
  }

  private static boolean compareAtomics(@Nullable IAnyAtomicItem atomic1, @Nullable IAnyAtomicItem atomic2) {
    return (atomic1 == null && atomic2 == null) || (atomic1 != null && atomic1.deepEquals(atomic2));
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
  public static boolean compareModelNodeItem(@NonNull IModelNodeItem<?, ?> item1, @NonNull IModelNodeItem<?, ?> item2) {
    return getComparator(item1).test(item1, item2);
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  private static boolean compareFlags(
      @NonNull Collection<? extends IFlagNodeItem> flags1,
      @NonNull Collection<? extends IFlagNodeItem> flags2) {

    Comparator<Collection<? extends IFlagNodeItem>> bySize = Comparator.comparingInt(Collection::size);
    int delta = bySize.compare(flags1, flags2);
    if (delta != 0) {
      return false;
    }

    // sort the collections to compare in an order independent way
    List<IFlagNodeItem> list1 = new ArrayList<>(flags1);
    List<IFlagNodeItem> list2 = new ArrayList<>(flags2);
    Collections.sort(list1, FLAG_SORT);
    Collections.sort(list2, FLAG_SORT);

    // compare the results
    for (int i = 0; i < list1.size(); i++) {
      if (!compareAsFlag(
          ObjectUtils.requireNonNull(list1.get(i)),
          ObjectUtils.requireNonNull(list2.get(i)))) {
        return false;
      }
    }
    return true;
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  private static boolean compareModelItems(
      @NonNull Collection<? extends List<? extends IModelNodeItem<?, ?>>> items1,
      @NonNull Collection<? extends List<? extends IModelNodeItem<?, ?>>> items2) {

    Comparator<Collection<? extends List<? extends IModelNodeItem<?, ?>>>> bySize
        = Comparator.comparingInt(Collection::size);
    int delta = bySize.compare(items1, items2);
    if (delta != 0) {
      return false;
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

        if (!compareModelNodeItem(item1, item2)) {
          return false;
        }
      }
    }
    return true;
  }

  @NonNull
  private static BiPredicate<IModelNodeItem<?, ?>, IModelNodeItem<?, ?>>
      getComparator(@NonNull IModelNodeItem<?, ?> item) {
    BiPredicate<IModelNodeItem<?, ?>, IModelNodeItem<?, ?>> retval;
    if (item instanceof IAssemblyNodeItem) {
      retval = NodeComparators::compareAsAssembly;
    } else if (item instanceof IFieldNodeItem) {
      retval = NodeComparators::compareAsField;
    } else {
      throw new UnsupportedOperationException("Unsupported model node item type: " + item.getClass().getName());
    }
    return retval;
  }

  /**
   * Compare two flag node items for equality.
   *
   * @param item1
   *          the first item to compare
   * @param item2
   *          the second item to compare
   * @return {@code true} if both flags have the same name and value, or
   *         {@code false} otherwise
   */
  public static boolean compareAsFlag(@NonNull IFlagNodeItem item1, @NonNull IFlagNodeItem item2) {
    return item1.getQName().equals(item2.getQName())
        && compareAtomics(item1.toAtomicItem(), item2.toAtomicItem());
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  private static boolean compareAsField(@NonNull IModelNodeItem<?, ?> item1, @NonNull IModelNodeItem<?, ?> item2) {
    return compareNodeItem(item1, item2)
        && compareAtomics(item1.toAtomicItem(), item2.toAtomicItem());
  }

  private static boolean compareAsAssembly(@NonNull IModelNodeItem<?, ?> item1, @NonNull IModelNodeItem<?, ?> item2) {
    return compareNodeItem(item1, item2);
  }

  private NodeComparators() {
    // disable construction
  }
}
