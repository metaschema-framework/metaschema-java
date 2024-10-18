
package gov.nist.secauto.metaschema.core.model.xml;

import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Deprecated(since = "1.2.1", forRemoval = true)
@SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class ExternalConstraintsModulePostProcessor
    extends gov.nist.secauto.metaschema.core.model.constraint.ExternalConstraintsModulePostProcessor {

  public ExternalConstraintsModulePostProcessor(@NonNull Collection<IConstraintSet> additionalConstraintSets) {
    super(additionalConstraintSets);
  }

}
