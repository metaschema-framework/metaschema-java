
package gov.nist.secauto.metaschema.core.util;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDiagramNodeVisitor {
  void visit(@NonNull DefaultDiagramNode.ModelEdge edge);

  void visit(@NonNull DefaultDiagramNode.ChoiceEdge choiceGroupEdge);

  void visit(@NonNull DefaultDiagramNode.ChoiceGroupEdge choiceGroupEdge);

}
