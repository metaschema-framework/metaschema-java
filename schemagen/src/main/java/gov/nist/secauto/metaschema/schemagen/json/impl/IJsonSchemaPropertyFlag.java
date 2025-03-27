
package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IJsonSchemaPropertyFlag extends IJsonSchemaPropertyNamed {
  @NonNull
  IFlagInstance getInstance();
}
