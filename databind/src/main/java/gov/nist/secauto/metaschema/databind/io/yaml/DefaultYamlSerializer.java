/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.yaml;

import com.fasterxml.jackson.core.JsonFactory;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.io.json.DefaultJsonSerializer;
import gov.nist.secauto.metaschema.databind.io.yaml.impl.YamlFactoryFactory;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultYamlSerializer<CLASS extends IBoundObject>
    extends DefaultJsonSerializer<CLASS> {

  /**
   * Construct a new YAML serializer that will generate YAML content based on data
   * in the bound class identified by the {@code classBinding}.
   *
   * @param definition
   *          the bound class information for the Java type this serializer is
   *          operating on
   */
  public DefaultYamlSerializer(@NonNull IBoundDefinitionModelAssembly definition) {
    super(definition);
  }

  @Override
  protected JsonFactory newFactoryInstance() {
    return YamlFactoryFactory.newGeneratorFactoryInstance(getConfiguration());
  }
}
