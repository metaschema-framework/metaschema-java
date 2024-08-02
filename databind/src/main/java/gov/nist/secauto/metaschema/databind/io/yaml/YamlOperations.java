/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.yaml;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class YamlOperations {
  private static final Yaml YAML_PARSER;

  static {
    LoaderOptions loaderOptions = new LoaderOptions();
    loaderOptions.setCodePointLimit(Integer.MAX_VALUE - 1); // 2GB
    Constructor constructor = new Constructor(loaderOptions);
    DumperOptions dumperOptions = new DumperOptions();
    Representer representer = new Representer(dumperOptions);
    YAML_PARSER = new Yaml(constructor, representer, dumperOptions, loaderOptions, new Resolver() {
      @Override
      protected void addImplicitResolvers() {
        addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO");
        addImplicitResolver(Tag.INT, INT, "-+0123456789");
        addImplicitResolver(Tag.FLOAT, FLOAT, "-+0123456789.");
        addImplicitResolver(Tag.MERGE, MERGE, "<");
        addImplicitResolver(Tag.NULL, NULL, "~nN\0");
        addImplicitResolver(Tag.NULL, EMPTY, null);
        // addImplicitResolver(Tag.TIMESTAMP, TIMESTAMP, "0123456789");
      }
    });
  }

  private YamlOperations() {
    // disable construction
  }

  /**
   * Parse the data represented in YAML in the provided {@code target}, producing
   * an mapping of field names to Java object values.
   *
   * @param target
   *          the YAML file to parse
   * @return the mapping of field names to Java object values
   * @throws IOException
   *           if an error occurred while parsing the YAML content
   */
  @SuppressWarnings({ "unchecked", "null" })
  @NonNull
  public static Map<String, Object> parseYaml(URI target) throws IOException {
    try (BufferedInputStream is = new BufferedInputStream(ObjectUtils.notNull(target.toURL().openStream()))) {
      return (Map<String, Object>) YAML_PARSER.load(is);
    }
  }

  /**
   * Converts the provided YAML {@code map} into JSON.
   *
   * @param map
   *          the YAML map
   * @return the JSON object
   * @throws JSONException
   *           if an error occurred while building the JSON tree
   */
  public static JSONObject yamlToJson(@NonNull Map<String, Object> map) {
    return new JSONObject(map);
  }
}
