/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.schemagen.SchemaGenerationException;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a JSON schema for a Metaschema-based module.
 */
public class JsonSchemaModule
    implements IJsonSchema {
  private final IModule module;
  private final List<IJsonSchemaDefinitionAssembly> roots;

  /**
   * Construct a new JSON schema definition.
   *
   * @param module
   *          the associated Metaschema-based module
   * @param state
   *          the JSON generation state
   */
  public JsonSchemaModule(
      @NonNull IModule module,
      @NonNull IJsonGenerationState state) {
    this.module = module;
    this.roots = module.getExportedRootAssemblyDefinitions().stream()
        .map(root -> state.getAssemblyDefinition(ObjectUtils.notNull(root), null))
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public boolean isInline(IJsonGenerationState state) {
    // always
    return true;
  }

  /**
   * Get the schemas for referenced definitions for model objects and data types
   * used within this module schema.
   *
   * @param state
   *          the JSON generation state used for context
   * @return a stream containing the referenced definitions
   */
  @NonNull
  public Stream<IJsonSchemaDefinable> collectDefinitions(@NonNull IJsonGenerationState state) {
    return ObjectUtils.notNull(roots.stream()
        .flatMap(root -> root.collectDefinitions(CollectionUtil.emptySet(), state)));
  }

  @Override
  public void generateInlineJsonSchema(ObjectNode node, IJsonGenerationState state) {
    node.put("$schema", "http://json-schema.org/draft-07/schema#");
    node.put("$id",
        String.format("%s/%s-%s-schema.json",
            module.getXmlNamespace(),
            module.getShortName(),
            module.getVersion()));
    node.put("$comment", module.getName().toMarkdown());
    node.put("type", "object");

    if (roots.isEmpty()) {
      throw new SchemaGenerationException("No root definitions found");
    }

    node.set("definitions", generateDefinitions(state));

    if (roots.size() == 1) {
      generateRoot(node, ObjectUtils.notNull(roots.iterator().next()), state);
    } else {
      ArrayNode oneOfNode = node.putArray("oneOf");
      roots.forEach(root -> {
        assert root != null;
        ObjectNode rootNode = ObjectUtils.notNull(oneOfNode.addObject());
        assert rootNode != null;

        generateRoot(rootNode, root, state);
      });
    }
  }

  /**
   * Generate the referenced JSON schema definitions used in this JSON schema.
   *
   * @param state
   *          the JSON generation state used for context
   * @return the definitions JSON schema node
   */
  private ObjectNode generateDefinitions(@NonNull IJsonGenerationState state) {

    // ensure all definitions are recorded
    Set<IJsonSchemaDefinable> usedDefinitions = ObjectUtils.notNull(collectDefinitions(state)
        .collect(Collectors.toUnmodifiableSet()));

    ObjectNode definitionsNode = ObjectUtils.notNull(state.getJsonNodeFactory().objectNode());

    usedDefinitions.stream()
        .filter(definition -> !definition.isInline(state))
        .distinct()
        .sorted(JsonSchemaHelper.DEFINABLE_NAME_COMPARATOR)
        .forEach(definition -> {
          ObjectNode definitionNode = definitionsNode.putObject(definition.getDefinitionName());
          assert definitionNode != null;
          definition.generateDefinitionJsonSchema(definitionNode, state);
        });

    state.generateDataTypeDefinitions(definitionsNode);

    return definitionsNode;
  }

  private static void generateRoot(
      @NonNull ObjectNode node,
      @NonNull IJsonSchemaDefinitionAssembly schema,
      @NonNull IJsonGenerationState state) {
    ObjectNode propertiesObj = node.putObject("properties");

    propertiesObj.putObject("$schema")
        .put("type", "string")
        .put("format", "uri-reference");

    String name = schema.getDefinition().getRootJsonName();

    schema.generateJsonSchemaOrDefinitionRef(ObjectUtils.notNull(propertiesObj.putObject(name)), state);

    node.putArray("required")
        .add(name);
    node.put("additionalProperties", false);
  }
}
