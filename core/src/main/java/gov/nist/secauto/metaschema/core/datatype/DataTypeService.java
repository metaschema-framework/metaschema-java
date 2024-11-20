/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.qname.QNameCache;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * This class provides a singleton service to allow data types to be discovered
 * within the system based on an SPI provided by {@link IDataTypeProvider}.
 */
public final class DataTypeService {
  private static final Logger LOGGER = LogManager.getLogger(DataTypeService.class);
  private static final Lazy<DataTypeService> INSTANCE = Lazy.lazy(DataTypeService::new);

  @NonNull
  private final Map<Integer, IAtomicOrUnionType> typeByQNameIndex;
  @NonNull
  private final Map<Class<? extends IDataTypeAdapter<?>>, IDataTypeAdapter<?>> typeByAdapterClass;
  @NonNull
  private final Map<Class<? extends IAnyAtomicItem>, IAtomicOrUnionType> typeByItemClass;

  /**
   * Get the singleton service instance, which will be lazy constructed on first
   * access.
   *
   * @return the service instance
   */
  @SuppressWarnings("null")
  @NonNull
  public static DataTypeService instance() {
    return INSTANCE.get();
  }

  private DataTypeService() {
    ServiceLoader<IDataTypeProvider> loader = ServiceLoader.load(IDataTypeProvider.class);

    this.typeByAdapterClass = CollectionUtil.unmodifiableMap(ObjectUtils.notNull(
        loader.stream()
            .map(Provider<IDataTypeProvider>::get)
            .flatMap(provider -> provider.getJavaTypeAdapters().stream())
            .collect(CustomCollectors.toMap(
                dataType -> ObjectUtils.asNullableType(dataType.getClass()),
                CustomCollectors.identity(),
                (key, v1, v2) -> {
                  if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Duplicate data type class '{}'. Using the first.",
                        key.getClass().getName());
                  }
                  return v1;
                },
                LinkedHashMap::new))));

    Stream<Map.Entry<Integer, IAtomicOrUnionType>> adapterStream = this.typeByAdapterClass.values().stream()
        .flatMap(dataType -> dataType.getNames().stream()
            .map(qname -> Map.entry(qname.getIndexPosition(), dataType.getItemType())));

    Stream<Map.Entry<Integer, IAtomicOrUnionType>> abstractStream = loader.stream()
        .map(Provider<IDataTypeProvider>::get)
        .flatMap(provider -> provider.getAbstractTypes().stream())
        .map(dataType -> Map.entry(dataType.getQName().getIndexPosition(), dataType));

    this.typeByQNameIndex = CollectionUtil.unmodifiableMap(ObjectUtils.notNull(
        Stream.concat(abstractStream, adapterStream)
            .collect(CustomCollectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (key, v1, v2) -> {
                  if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Duplicate data types '{}' and '{}' with name '{}'. Using the first.",
                        v1.getClass().getName(),
                        v2.getClass().getName(),
                        key);
                  }
                  return v1;
                },
                ConcurrentHashMap::new))));

    this.typeByItemClass = CollectionUtil.unmodifiableMap(ObjectUtils.notNull(
        this.typeByQNameIndex.values().stream()
            .collect(CustomCollectors.toMap(
                type -> ObjectUtils.asNullableType(type.getItemClass()),
                CustomCollectors.identity(),
                (key, v1, v2) -> {
                  if (!Objects.equals(v1, v2) && LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Duplicate item class '{}' declared by types '{}' and '{}'. Using the first.",
                        key.getName(),
                        v1.toSignature(),
                        v2.toSignature());
                  }
                  return v1;
                },
                ConcurrentHashMap::new))));
  }

  /**
   * Lookup a specific {@link IAtomicOrUnionType} by its QName.
   *
   * @param qname
   *          the qualified name of data type adapter to get the instance for
   * @return the data type or {@code null} if the data type is unknown to the type
   *         system
   */
  @Nullable
  public IAtomicOrUnionType getDataTypeByQName(@NonNull QName qname) {
    IEnhancedQName result = QNameCache.instance().get(qname);
    return result == null ? null : getDataTypeByQNameIndex(result.getIndexPosition());
  }

  /**
   * Lookup a specific {@link IAtomicOrUnionType} by its QName index position.
   *
   * @param qnameIndexPosition
   *          the position in the global QName index for the qualified name of
   *          data type adapter to get the instance for
   * @return the data type or {@code null} if the data type is unknown to the type
   *         system
   */
  @Nullable
  public IAtomicOrUnionType getDataTypeByQNameIndex(int qnameIndexPosition) {
    return typeByQNameIndex.get(qnameIndexPosition);
  }

  /**
   * Lookup a specific {@link IAtomicOrUnionType} by its item class.
   *
   * @param clazz
   *          the adapter class to get the instance for
   * @return the data type or {@code null} if the data type is unknown to the type
   *         system
   */
  @Nullable
  public IAtomicOrUnionType getDataTypeByItemClass(Class<? extends IAnyAtomicItem> clazz) {
    return typeByItemClass.get(clazz);
  }

  /**
   * Lookup a specific {@link IDataTypeAdapter} by its adapter class.
   *
   * @param clazz
   *          the adapter class to get the instance for
   * @param <TYPE>
   *          the type of the requested adapter
   * @return the adapter or {@code null} if the adapter is unknown to the type
   *         system
   */
  @SuppressWarnings("unchecked")
  @Nullable
  public <TYPE extends IDataTypeAdapter<?>> TYPE getDataTypeByAdapterClass(
      @NonNull Class<TYPE> clazz) {
    return (TYPE) typeByAdapterClass.get(clazz);
  }
}
