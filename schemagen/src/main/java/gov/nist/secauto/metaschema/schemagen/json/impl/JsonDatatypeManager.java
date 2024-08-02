/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.datatype.AbstractDatatypeManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for managing Metaschema module data type implementations aligned with
 * the JSON schema format for use in schema generation.
 */
public class JsonDatatypeManager
    extends AbstractDatatypeManager {
  private static final Map<String, List<String>> DATATYPE_DEPENDENCY_MAP = new ConcurrentHashMap<>();
  private static final Pattern DEFINITION_REF_PATTERN = Pattern.compile("^#/definitions/(.+)$");
  private static final Map<String, JsonNode> JSON_DATATYPES = new ConcurrentHashMap<>();

  static {
    JsonNode jsonData;
    try (InputStream is
        = ModuleLoader.class.getResourceAsStream("/schema/json/metaschema-datatypes.json")) {
      ObjectMapper objectMapper = new ObjectMapper();
      jsonData = objectMapper.readTree(is);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }

    // analyze datatypes for dependencies
    for (String ref : getDatatypeTranslationMap().values()) {
      JsonNode refNode = jsonData.at("/definitions/" + ref);
      if (!refNode.isMissingNode()) {
        JSON_DATATYPES.put(ref, refNode);

        List<String> dependencies = getDependencies(refNode).collect(Collectors.toList());
        if (!dependencies.isEmpty()) {
          DATATYPE_DEPENDENCY_MAP.put(ref, dependencies);
        }
      }
    }
  }

  private static Stream<String> getDependencies(@NonNull JsonNode node) {
    Stream<String> retval = Stream.empty();
    for (Map.Entry<String, JsonNode> entry : CollectionUtil.toIterable(ObjectUtils.notNull(node.fields()))) {
      JsonNode value = entry.getValue();
      assert value != null;
      if ("$ref".equals(entry.getKey())) {
        Matcher matcher = DEFINITION_REF_PATTERN.matcher(value.asText());
        if (matcher.matches()) {
          String dependency = matcher.group(1);
          retval = Stream.concat(retval, Stream.of(dependency));
        }
      }

      if (value.isArray()) {
        for (JsonNode child : CollectionUtil.toIterable(ObjectUtils.notNull(value.elements()))) {
          assert child != null;
          retval = Stream.concat(retval, getDependencies(child));
        }
      }
    }
    return retval;
  }

  public void generateDatatypes(@NonNull ObjectNode definitionsObject) {
    Set<String> requiredJsonDatatypes = getUsedTypes();
    // resolve dependencies
    for (String datatype : CollectionUtil.toIterable(ObjectUtils.notNull(
        requiredJsonDatatypes.stream()
            .flatMap(datatype -> {
              Stream<String> result;
              List<String> dependencies = DATATYPE_DEPENDENCY_MAP.get(datatype);
              if (dependencies == null) {
                result = Stream.of(datatype);
              } else {
                result = Stream.concat(Stream.of(datatype), dependencies.stream());
              }
              return result;
            }).distinct()
            .sorted()
            .iterator()))) {

      JsonNode definition = JSON_DATATYPES.get(datatype);
      if (definition == null) {
        throw new SchemaGenerationException("Missing JSON datatype definition for: /definitions/" + datatype);
      }
      definitionsObject.set(datatype, definition);
    }
  }

}
