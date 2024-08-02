/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import gov.nist.secauto.metaschema.core.util.CustomCollectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
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
  private static final Lazy<DataTypeService> INSTANCE = Lazy.lazy(() -> new DataTypeService());

  private final Map<String, IDataTypeAdapter<?>> typeByName;
  private final Map<QName, IDataTypeAdapter<?>> typeByQName;
  private final Map<Class<? extends IDataTypeAdapter<?>>, IDataTypeAdapter<?>> typeByClass;

  /**
   * Get the singleton service instance, which will be lazy constructed on first
   * access.
   *
   * @return the service instance
   */
  @SuppressWarnings("null")
  @NonNull
  public static DataTypeService getInstance() {
    return INSTANCE.get();
  }

  private DataTypeService() {

    ServiceLoader<IDataTypeProvider> loader = ServiceLoader.load(IDataTypeProvider.class);
    List<IDataTypeAdapter<?>> dataTypes = loader.stream()
        .map(Provider<IDataTypeProvider>::get)
        .flatMap(provider -> provider.getJavaTypeAdapters().stream())
        .collect(Collectors.toList());

    Map<String, IDataTypeAdapter<?>> typeByName = dataTypes.stream()
        .flatMap(dataType -> dataType.getNames().stream()
            .map(qname -> Map.entry(qname.getLocalPart(), dataType)))
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

    Map<QName, IDataTypeAdapter<?>> typeByQName = dataTypes.stream()
        .flatMap(dataType -> dataType.getNames().stream()
            .map(qname -> Map.entry(qname, dataType)))
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

    @SuppressWarnings({ "unchecked", "null" }) Map<Class<? extends IDataTypeAdapter<?>>,
        IDataTypeAdapter<?>> typeByClass = dataTypes.stream()
            .collect(CustomCollectors.toMap(
                dataType -> (Class<? extends IDataTypeAdapter<?>>) dataType.getClass(),
                Function.identity(),
                (key, v1, v2) -> {
                  if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Duplicate data type class '{}'. Using the first.",
                        key.getClass().getName());
                  }
                  return v1;
                },
                ConcurrentHashMap::new));
    this.typeByName = typeByName;
    this.typeByQName = typeByQName;
    this.typeByClass = typeByClass;
  }

  /**
   * Lookup a specific {@link IDataTypeAdapter} instance by its name.
   *
   * @param qname
   *          the qualified name of data type adapter to get the instance for
   * @return the instance or {@code null} if the instance is unknown to the type
   *         system
   */
  @Nullable
  public IDataTypeAdapter<?> getJavaTypeAdapterByQName(@NonNull QName qname) {
    return typeByQName.get(qname);
  }

  /**
   * Lookup a specific {@link IDataTypeAdapter} instance by its name.
   *
   * @param name
   *          the name of data type adapter to get the instance for
   * @return the instance or {@code null} if the instance is unknown to the type
   *         system
   */
  @Nullable
  public IDataTypeAdapter<?> getJavaTypeAdapterByName(@NonNull String name) {
    return typeByName.get(name);
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
    return (TYPE) typeByClass.get(clazz);
  }
}
