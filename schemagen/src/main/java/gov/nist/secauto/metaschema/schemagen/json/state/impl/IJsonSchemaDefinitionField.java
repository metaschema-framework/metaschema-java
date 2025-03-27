
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IJsonSchemaDefinitionField extends IJsonSchemaModelDefinition {
  @Override
  IFieldDefinition getDefinition();

  @NonNull
  List<? extends IJsonSchemaPropertyNamed> getNonValueProperties();

  @NonNull
  IDataTypeJsonSchema getFieldValue();
}
