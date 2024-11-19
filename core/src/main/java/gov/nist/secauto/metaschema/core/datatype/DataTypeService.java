/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.qname.QNameCache;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

  private final Map<Integer, IDataTypeAdapter<?>> typeByQNameIndex;
  private final Map<Class<? extends IDataTypeAdapter<?>>, IDataTypeAdapter<?>> typeByAdapterClass;
  private final Map<Class<? extends IAnyAtomicItem>, IDataTypeAdapter<?>> typeByItemClass;

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
    List<IDataTypeAdapter<?>> dataTypes = loader.stream()
        .map(Provider<IDataTypeProvider>::get)
        .flatMap(provider -> provider.getJavaTypeAdapters().stream())
        .collect(Collectors.toList());

    this.typeByQNameIndex = dataTypes.stream()
        .flatMap(dataType -> dataType.getNames().stream()
            .map(qname -> Map.entry(qname.getIndexPosition(), dataType)))
        .collect(CustomCollectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (key, v1, v2) -> {
              if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Data types '{}' and '{}' have duplicate name '{}'. Using the first.",
                    v1.getClass().getName(),
                    v2.getClass().getName(),
                    key);
              }
              return v1;
            },
            ConcurrentHashMap::new));

    this.typeByAdapterClass = dataTypes.stream()
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
            ConcurrentHashMap::new));

    this.typeByItemClass = dataTypes.stream()
        .collect(CustomCollectors.toMap(
            dataType -> ObjectUtils.asNullableType(dataType.getItemType().getItemClass()),
            CustomCollectors.identity(),
            (key, v1, v2) -> {
              if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Duplicate item class '{}' declared by adapters '{}' and '{}'. Using the first.",
                    key.getName(),
                    v1.getClass().getName(),
                    v2.getClass().getName());
              }
              return v1;
            },
            ConcurrentHashMap::new));
  }

  /**
   * Lookup a specific {@link IDataTypeAdapter} instance by its QName.
   *
   * @param qname
   *          the qualified name of data type adapter to get the instance for
   * @return the instance or {@code null} if the instance is unknown to the type
   *         system
   */
  @Nullable
  public IDataTypeAdapter<?> getJavaTypeAdapterByQName(@NonNull QName qname) {
    IEnhancedQName result = QNameCache.instance().get(qname);
    return result == null ? null : getJavaTypeAdapterByQNameIndex(result.getIndexPosition());
  }

  /**
   * Lookup a specific {@link IDataTypeAdapter} instance by its QName index
   * position.
   *
   * @param qnameIndexPosition
   *          the position in the global QName index for the qualified name of
   *          data type adapter to get the instance for
   * @return the instance or {@code null} if the instance is unknown to the type
   *         system
   */
  @Nullable
  public IDataTypeAdapter<?> getJavaTypeAdapterByQNameIndex(int qnameIndexPosition) {
    return typeByQNameIndex.get(qnameIndexPosition);
  }

  /**
   * Lookup a specific {@link IDataTypeAdapter} instance by its class.
   *
   * @param clazz
   *          the adapter class to get the instance for
   * @param <TYPE>
   *          the type of the requested adapter
   * @return the instance or {@code null} if the instance is unknown to the type
   *         system
   */
  @SuppressWarnings("unchecked")
  @Nullable
  public <TYPE extends IDataTypeAdapter<?>> TYPE getJavaTypeAdapterByClass(@NonNull Class<TYPE> clazz) {
    return (TYPE) typeByAdapterClass.get(clazz);
  }

  /**
   * Lookup a specific {@link IDataTypeAdapter} instance by its item class.
   *
   * @param clazz
   *          the adapter class to get the instance for
   * @return the instance or {@code null} if the instance is unknown to the type
   *         system
   */
  @Nullable
  public IDataTypeAdapter<?> getJavaTypeAdapterByItemClass(Class<? extends IAnyAtomicItem> clazz) {
    return typeByItemClass.get(clazz);
  }
}
