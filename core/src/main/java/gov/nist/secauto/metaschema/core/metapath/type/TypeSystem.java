/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITokenItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

// FIXME: remove this?
@SuppressWarnings("removal")
public final class TypeSystem {
  private static final Map<Class<? extends IItem>, IItemType> ITEM_CLASS_TO_TYPE_MAP;
  private static final Map<Class<? extends IAnyAtomicItem>, IEnhancedQName> ITEM_CLASS_TO_QNAME_MAP;

  static {
    ITEM_CLASS_TO_TYPE_MAP = Collections.unmodifiableMap(Map.ofEntries(
        register(IItem.type()),
        register(INodeItem.type()),
        register(IDocumentNodeItem.type()),
        // register(IItemType.node(IAssemblyNodeItem.class, "assembly-node")),
        // register(IItemType.node(IFieldNodeItem.class, "field-node")),
        // register(IItemType.node(IFlagNodeItem.class, "flag-node")),
        register(IArrayItem.type()),
        register(IMapItem.type()),
        register(IAnyAtomicItem.type()),
        register(INumericItem.type()),
        register(IDurationItem.type()),
        register(IBase64BinaryItem.type()),
        // register(MetaschemaDataTypeProvider.BOOLEAN),
        // register(MetaschemaDataTypeProvider.DATE),
        // register(MetaschemaDataTypeProvider.DATE_TIME),
        // register(MetaschemaDataTypeProvider.IP_V4_ADDRESS),
        // register(MetaschemaDataTypeProvider.IP_V6_ADDRESS),
        // register(MetaschemaDataTypeProvider.URI),
        // register(MetaschemaDataTypeProvider.URI_REFERENCE),
        // register(MetaschemaDataTypeProvider.UUID),
        // register(MetaschemaDataTypeProvider.DAY_TIME_DURATION),
        // register(MetaschemaDataTypeProvider.YEAR_MONTH_DURATION),
        // register(MetaschemaDataTypeProvider.DECIMAL),
        // register(MetaschemaDataTypeProvider.INTEGER),
        // register(MetaschemaDataTypeProvider.NON_NEGATIVE_INTEGER),
        // register(MetaschemaDataTypeProvider.POSITIVE_INTEGER),
        // register(MetaschemaDataTypeProvider.EMAIL_ADDRESS),
        // register(MetaschemaDataTypeProvider.HOSTNAME),
        // register(MetaschemaDataTypeProvider.NCNAME),
        // register(MetaschemaDataTypeProvider.STRING),
        register(ITokenItem.type())));

    Stream<Map.Entry<Class<? extends IAnyAtomicItem>, IEnhancedQName>> basicAdapterStream
        = new MetaschemaDataTypeProvider().getJavaTypeAdapters().stream()
            .map(adapter -> Map.entry(adapter.getItemType().getItemClass(), adapter.getPreferredName()));
    Stream<Map.Entry<Class<? extends IAnyAtomicItem>, IEnhancedQName>> markupAdapterStream
        = new MarkupDataTypeProvider().getJavaTypeAdapters().stream()
            .map(adapter -> Map.entry(adapter.getItemType().getItemClass(), adapter.getPreferredName()));

    ITEM_CLASS_TO_QNAME_MAP = CollectionUtil.unmodifiableMap(
        Stream.concat(basicAdapterStream, markupAdapterStream)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, LinkedHashMap::new)));
  }

  private static Map.Entry<Class<? extends IItem>, IItemType>
      register(@NonNull IItemType type) {
    return Map.entry(type.getItemClass(), type);
  }

  @NonNull
  private static Stream<Class<? extends IItem>> getItemInterfaces(@NonNull Class<?> clazz) {
    @SuppressWarnings("unchecked")
    Stream<Class<? extends IItem>> retval = IItem.class.isAssignableFrom(clazz)
        ? Stream.of((Class<? extends IItem>) clazz)
        : Stream.empty();

    Class<?>[] interfaces = clazz.getInterfaces();
    if (interfaces.length > 0) {
      retval = Stream.concat(retval, Arrays.stream(interfaces).flatMap(TypeSystem::getItemInterfaces));
    }

    return ObjectUtils.notNull(retval);
  }

  /**
   * Get the human-friendly data type name for the provided Metapath item class.
   *
   * @param itemClass
   *          the Metapath item class to get the name for
   * @return the name or {@code null} if no name is registered for the item class
   */
  public static String getName(@NonNull Class<? extends IItem> itemClass) {
    Class<? extends IItem> supportedItemClass = getItemInterfaces(itemClass).findFirst().orElse(null);

    IEnhancedQName qname = ITEM_CLASS_TO_QNAME_MAP.get(supportedItemClass);
    return qname == null ? supportedItemClass.getName() : qname.toEQName(null);
  }

  private TypeSystem() {
    // disable construction
  }
}
