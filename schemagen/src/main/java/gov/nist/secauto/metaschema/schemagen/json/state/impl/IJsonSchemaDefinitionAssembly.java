
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IJsonSchemaDefinitionAssembly extends IJsonSchemaModelDefinition {
  @Override
  IAssemblyDefinition getDefinition();

  @NonNull
  List<JsonSchemaHelper.Choice> getChoices();
}
