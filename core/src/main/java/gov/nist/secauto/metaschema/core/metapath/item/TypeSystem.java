/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IEmailAddressItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IHostnameItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIPv4AddressItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIPv6AddressItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INcNameItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INonNegativeIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IPositiveIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITokenItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUriReferenceItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUuidItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings({ "removal", "deprecation" })
public final class TypeSystem {
  private static final Map<Class<? extends IItem>, QName> ITEM_CLASS_TO_QNAME_MAP;

  static {
    ITEM_CLASS_TO_QNAME_MAP = Collections.unmodifiableMap(Map.ofEntries(
        register(IItem.class, "item"),
        register(INodeItem.class, "node"),
        register(IDocumentNodeItem.class, "document-node"),
        register(IAssemblyNodeItem.class, "assembly-node"),
        register(IFieldNodeItem.class, "field-node"),
        register(IFlagNodeItem.class, "flag-node"),
        register(IArrayItem.class, "array"),
        register(IMapItem.class, "map"),
        register(IAnyAtomicItem.class, "any-atomic-type"),
        register(INumericItem.class, "numeric"),
        register(IDurationItem.class, "duration"),
        register(IBase64BinaryItem.class, MetaschemaDataTypeProvider.BASE64),
        register(IBooleanItem.class, MetaschemaDataTypeProvider.BOOLEAN),
        register(IDateItem.class, MetaschemaDataTypeProvider.DATE),
        // register(IDate.class, MetaschemaDataTypeProvider.DATE_WITH_TZ),
        register(IDateTimeItem.class, MetaschemaDataTypeProvider.DATE_TIME),
        // register(IBooleanItem.class, MetaschemaDataTypeProvider.DATE_TIME_WITH_TZ),
        register(IIPv4AddressItem.class, MetaschemaDataTypeProvider.IP_V4_ADDRESS),
        register(IIPv6AddressItem.class, MetaschemaDataTypeProvider.IP_V6_ADDRESS),
        register(IAnyUriItem.class, MetaschemaDataTypeProvider.URI),
        register(IUriReferenceItem.class, MetaschemaDataTypeProvider.URI_REFERENCE),
        register(IUuidItem.class, MetaschemaDataTypeProvider.UUID),
        register(IDayTimeDurationItem.class, MetaschemaDataTypeProvider.DAY_TIME_DURATION),
        register(IYearMonthDurationItem.class, MetaschemaDataTypeProvider.YEAR_MONTH_DURATION),
        register(IDecimalItem.class, MetaschemaDataTypeProvider.DECIMAL),
        register(IIntegerItem.class, MetaschemaDataTypeProvider.INTEGER),
        register(INonNegativeIntegerItem.class, MetaschemaDataTypeProvider.NON_NEGATIVE_INTEGER),
        register(IPositiveIntegerItem.class, MetaschemaDataTypeProvider.POSITIVE_INTEGER),
        register(IEmailAddressItem.class, MetaschemaDataTypeProvider.EMAIL_ADDRESS),
        register(IHostnameItem.class, MetaschemaDataTypeProvider.HOSTNAME),
        register(INcNameItem.class, MetaschemaDataTypeProvider.NCNAME),
        register(IStringItem.class, MetaschemaDataTypeProvider.STRING),
        register(ITokenItem.class, MetaschemaDataTypeProvider.TOKEN)));
  }

  private static Map.Entry<Class<? extends IItem>, QName> register(
      @NonNull Class<? extends IItem> clazz,
      @NonNull IDataTypeAdapter<?> adapter) {
    return Map.entry(clazz, adapter.getPreferredName());
  }

  private static Map.Entry<Class<? extends IItem>, QName> register(
      @NonNull Class<? extends IItem> clazz,
      @NonNull String typeName) {
    return Map.entry(clazz, new QName(MetapathConstants.NS_METAPATH.toASCIIString(), typeName));
  }

  @NonNull
  private static Stream<Class<? extends IItem>> getItemInterfaces(@NonNull Class<?> clazz) {
    @SuppressWarnings("unchecked") Stream<Class<? extends IItem>> retval = IItem.class.isAssignableFrom(clazz)
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
   * @param clazz
   *          the Metapath item class to get the name for
   * @return the name or {@code null} if no name is registered for the item class
   */
  public static String getName(@NonNull Class<? extends IItem> clazz) {
    Class<? extends IItem> itemClass = getItemInterfaces(clazz).findFirst().orElse(null);

    QName qname = ITEM_CLASS_TO_QNAME_MAP.get(itemClass);
    return qname == null ? clazz.getName() : asPrefixedName(qname);
  }

  private static String asPrefixedName(@NonNull QName qname) {
    String namespace = qname.getNamespaceURI();
    String prefix = namespace.isEmpty() ? null : StaticContext.getWellKnownPrefixForUri(namespace);
    return prefix == null ? qname.toString() : prefix + ":" + qname.getLocalPart();
  }

  private TypeSystem() {
    // disable construction
  }
}
