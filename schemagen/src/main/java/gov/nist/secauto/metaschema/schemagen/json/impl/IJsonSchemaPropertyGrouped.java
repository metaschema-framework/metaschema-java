
package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IJsonSchemaPropertyGrouped extends IJsonSchemaProperty, IJsonSchemaModelDefinition {
  @NonNull
  INamedModelInstanceGrouped getInstance();
}
