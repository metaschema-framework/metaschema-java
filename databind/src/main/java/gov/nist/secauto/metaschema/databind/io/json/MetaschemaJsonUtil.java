/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.json;

import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModel;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelFieldComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceFlag;
import gov.nist.secauto.metaschema.databind.model.IBoundProperty;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

final class MetaschemaJsonUtil {

  private MetaschemaJsonUtil() {
    // disable construction
  }

  /**
   * Generates a mapping of property names to associated Module instances.
   * <p>
   * If {@code requiresJsonKey} is {@code true} then the instance used as the JSON
   * key is not included in the mapping.
   * <p>
   * If the {@code targetDefinition} is an instance of {@link IFieldDefinition}
   * and a JSON value key property is configured, then the value key flag and
   * value are also omitted from the mapping. Otherwise, the value is included in
   * the mapping.
   *
   * @param targetDefinition
   *          the definition to get JSON instances from
   * @param jsonKey
   *          the flag instance used as the JSON key, or {@code null} otherwise
   * @return a mapping of JSON property to related Module instance
   */
  @NonNull
  public static Map<String, ? extends IBoundProperty<?>> getJsonInstanceMap(
      @NonNull IBoundDefinitionModel<?> targetDefinition,
      @Nullable IBoundInstanceFlag jsonKey) {
    Collection<? extends IBoundInstanceFlag> flags = targetDefinition.getFlagInstances();
    int flagCount = flags.size() - (jsonKey == null ? 0 : 1);

    Stream<? extends IBoundProperty<?>> instanceStream;
    if (targetDefinition instanceof IBoundDefinitionModelAssembly) {
      // use all child instances
      instanceStream = ((IBoundDefinitionModelAssembly) targetDefinition).getModelInstances().stream()
          .map(instance -> (IBoundProperty<?>) instance);
    } else if (targetDefinition instanceof IBoundDefinitionModelFieldComplex) {
      IBoundDefinitionModelFieldComplex targetFieldDefinition = (IBoundDefinitionModelFieldComplex) targetDefinition;

      IBoundInstanceFlag jsonValueKeyFlag = targetFieldDefinition.getJsonValueKeyFlagInstance();
      if (jsonValueKeyFlag == null && flagCount > 0) {
        // the field value is handled as named field
        IBoundFieldValue fieldValue = targetFieldDefinition.getFieldValue();
        instanceStream = Stream.of(fieldValue);
      } else {
        // only the value, with no flags or a JSON value key flag
        instanceStream = Stream.empty();
      }
    } else {
      throw new UnsupportedOperationException(
          String.format("Unsupported class binding type: %s", targetDefinition.getClass().getName()));
    }

    if (jsonKey != null) {
      instanceStream = Stream.concat(
          flags.stream().filter(flag -> !jsonKey.equals(flag)),
          instanceStream);
    } else {
      instanceStream = Stream.concat(
          flags.stream(),
          instanceStream);
    }
    return CollectionUtil.unmodifiableMap(ObjectUtils.notNull(instanceStream.collect(
        Collectors.toMap(
            IBoundProperty::getJsonName,
            Function.identity(),
            (v1, v2) -> v2,
            LinkedHashMap::new))));
  }
}
