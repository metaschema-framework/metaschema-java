
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
   */
  public JsonSchemaModule(
      @NonNull IModule module,
      @NonNull IJsonGenerationState state) {
    this.module = module;
    this.roots = module.getExportedRootAssemblyDefinitions().stream()
        .map(root -> state.newRootAssemblyDefinition(root))
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public boolean isInline(IJsonGenerationState state) {
    // always
    return true;
  }

  @NonNull
  public Stream<IJsonSchemaDefinable> collectDefinitions(IJsonGenerationState state) {
    return roots.stream()
        .flatMap(root -> root.collectDefinitions(Set.of(), state));
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

    // ensure all definitions are recorded
    // TODO: use this result
    collectDefinitions(state)
        .collect(Collectors.toUnmodifiableSet());

    node.set("definitions", state.generateDefinitions());

    if (roots.size() == 1) {
      generateRoot(node, roots.iterator().next(), state);
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

  private static void generateRoot(
      @NonNull ObjectNode node,
      @NonNull IJsonSchemaDefinitionAssembly schema,
      @NonNull IJsonGenerationState state) {
    ObjectNode propertiesObj = node.putObject("properties");

    propertiesObj.putObject("$schema")
        .put("type", "string")
        .put("format", "uri-reference");

    String name = schema.getDefinition().getRootJsonName();

    schema.generateJsonSchemaOrDefinitionRef(
        propertiesObj.putObject(name), state);

    node.putArray("required")
        .add(name);
    node.put("additionalProperties", false);
  }
}
