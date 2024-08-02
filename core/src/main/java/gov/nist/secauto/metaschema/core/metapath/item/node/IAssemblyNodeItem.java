
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IAssemblyNodeItem extends IModelNodeItem<IAssemblyDefinition, IAssemblyInstance> {
  @Override
  default NodeItemType getNodeItemType() {
    return NodeItemType.ASSEMBLY;
  }

  @Override
  default IAssemblyNodeItem getNodeItem() {
    return this;
  }

  @Override
  @Nullable
  default URI getBaseUri() {
    INodeItem parent = getParentNodeItem();
    return parent == null ? null : parent.getBaseUri();
  }

  @Override
  default String format(@NonNull IPathFormatter formatter) {
    return formatter.formatAssembly(this);
  }

  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull INodeItemVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitAssembly(this, context);
  }
}
