/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.yaml;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.io.json.DefaultJsonDeserializer;
import gov.nist.secauto.metaschema.databind.io.yaml.impl.YamlFactoryFactory;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultYamlDeserializer<CLASS extends IBoundObject>
    extends DefaultJsonDeserializer<CLASS> {

  /**
   * Construct a new YAML deserializer that will parse the bound class identified
   * by the {@code classBinding}.
   *
   * @param definition
   *          the bound class information for the Java type this deserializer is
   *          operating on
   */
  public DefaultYamlDeserializer(@NonNull IBoundDefinitionModelAssembly definition) {
    super(definition);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method provides a YAML version of the JSON factory.
   *
   * @return the factory
   */
  @Override
  protected YAMLFactory newFactoryInstance() {
    return YamlFactoryFactory.newParserFactoryInstance(getConfiguration());
  }

}
