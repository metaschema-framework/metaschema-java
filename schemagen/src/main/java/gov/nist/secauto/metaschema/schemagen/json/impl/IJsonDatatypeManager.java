
package gov.nist.secauto.metaschema.schemagen.json.impl;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IJsonDatatypeManager {
  @NonNull
  List<IJsonSchemaDefinition> getDefinitionSchemas();
}
