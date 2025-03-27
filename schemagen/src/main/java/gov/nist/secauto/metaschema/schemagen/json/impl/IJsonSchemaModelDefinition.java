
package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IJsonSchemaModelDefinition extends IJsonSchemaDefinition {
  @Nullable
  default IEnhancedQName getJsonKeyFlagName() {
    IFlagInstance jsonKey = getJsonKeyFlag();
    return jsonKey == null ? null : jsonKey.getQName();
  }

  @Nullable
  IFlagInstance getJsonKeyFlag();

  @NonNull
  List<IJsonSchemaPropertyFlag> getFlagProperties();
}
