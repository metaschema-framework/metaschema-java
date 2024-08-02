/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.yaml.impl;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.DeserializationFeature;
import gov.nist.secauto.metaschema.databind.io.SerializationFeature;
import gov.nist.secauto.metaschema.databind.io.json.JsonFactoryFactory;

import org.yaml.snakeyaml.LoaderOptions;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class YamlFactoryFactory {
  private YamlFactoryFactory() {
    // disable construction
  }

  /**
   * Create a new {@link YAMLFactory} configured to parse YAML.
   *
   * @param config
   *          the deserialization configuration
   *
   * @return the factory
   */
  @NonNull
  public static YAMLFactory newParserFactoryInstance(
      @NonNull IConfiguration<DeserializationFeature<?>> config) {
    YAMLFactoryBuilder builder = YAMLFactory.builder();
    LoaderOptions loaderOptions = builder.loaderOptions();
    if (loaderOptions == null) {
      loaderOptions = new LoaderOptions();
    }

    int codePointLimit = config.get(DeserializationFeature.YAML_CODEPOINT_LIMIT);
    loaderOptions.setCodePointLimit(codePointLimit);
    builder.loaderOptions(loaderOptions);

    YAMLFactory retval = ObjectUtils.notNull(builder.build());
    JsonFactoryFactory.configureJsonFactory(retval);
    return retval;
  }

  /**
   * Create a new {@link YAMLFactory} configured to generate YAML.
   *
   * @param config
   *          the serialization configuration
   *
   * @return the factory
   */
  @NonNull
  public static YAMLFactory newGeneratorFactoryInstance(
      @NonNull IMutableConfiguration<SerializationFeature<?>> config) {
    YAMLFactoryBuilder builder = YAMLFactory.builder();
    YAMLFactory retval = ObjectUtils.notNull(builder
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        .enable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
        .disable(YAMLGenerator.Feature.SPLIT_LINES)
        .build());
    JsonFactoryFactory.configureJsonFactory(retval);
    return retval;
  }
}
