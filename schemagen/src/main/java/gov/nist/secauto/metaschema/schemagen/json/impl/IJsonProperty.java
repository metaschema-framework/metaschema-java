/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema.IKey;
import gov.nist.secauto.metaschema.schemagen.json.IDefinitionJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IJsonProperty<I extends IInstance> {
  @NonNull
  I getInstance();

  @NonNull
  String getName();

  boolean isRequired();

  void gatherDefinitions(
      @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions,
      @NonNull IJsonGenerationState state);

  /**
   * Generate the schema type.
   *
   * @param properties
   *          the containing property context to add the property to
   * @param state
   *          the schema generation state used for context and writing
   * @param jsonKeyFlagName
   *          the name of the flag to use as the JSON key, or @{code null} if no
   *          flag is used as the JSON key
   * @param discriminator
   *          the name to use as the choice group discriminator, or @{code null}
   *          if no choice group discriminator is used
   * @throws SchemaGenerationException
   *           if an error occurred while writing the type
   */
  void generateProperty(
      @NonNull PropertyCollection properties,
      @NonNull IJsonGenerationState state);

  class PropertyCollection {
    private final Map<String, ObjectNode> properties;
    private final Set<String> required;

    public PropertyCollection() {
      this(new LinkedHashMap<>(), new LinkedHashSet<>());
    }

    protected PropertyCollection(@NonNull Map<String, ObjectNode> properties, @NonNull Set<String> required) {
      this.properties = properties;
      this.required = required;
    }

    public Map<String, ObjectNode> getProperties() {
      return Collections.unmodifiableMap(properties);
    }

    public Set<String> getRequired() {
      return Collections.unmodifiableSet(required);
    }

    public void addProperty(@NonNull String name, @NonNull ObjectNode def) {
      properties.put(name, def);
    }

    public void addRequired(@NonNull String name) {
      required.add(name);
    }

    public PropertyCollection copy() {
      return new PropertyCollection(new LinkedHashMap<>(properties), new LinkedHashSet<>(required));
    }

    public void generate(@NonNull ObjectNode obj) {
      if (!properties.isEmpty()) {
        ObjectNode propertiesNode = ObjectUtils.notNull(JsonNodeFactory.instance.objectNode());
        for (Map.Entry<String, ObjectNode> entry : properties.entrySet()) {
          propertiesNode.set(entry.getKey(), entry.getValue());
        }
        obj.set("properties", propertiesNode);

        if (!required.isEmpty()) {
          ArrayNode requiredNode = ObjectUtils.notNull(JsonNodeFactory.instance.arrayNode());
          for (String requiredProperty : required) {
            requiredNode.add(requiredProperty);
          }
          obj.set("required", requiredNode);
        }
      }
    }
  }
}
