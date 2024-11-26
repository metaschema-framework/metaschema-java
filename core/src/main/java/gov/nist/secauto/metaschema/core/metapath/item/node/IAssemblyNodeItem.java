
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidTypeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A Metapath node valued item representing a Metaschema module assembly.
 */
public interface IAssemblyNodeItem extends IModelNodeItem<IAssemblyDefinition, IAssemblyInstance> {
  @NonNull
  static IItemType type() {
    return IItemType.assembly();
  }

  @Override
  default IKindTest<IAssemblyNodeItem> getType() {
    StaticContext staticContext = getStaticContext();
    return IItemType.assembly(
        getQName(),
        getDefinition().getDefinitionQName().toEQName(staticContext),
        staticContext);
  }

  @Override
  default NodeItemKind getNodeItemKind() {
    return NodeItemKind.ASSEMBLY;
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
  default IAnyAtomicItem toAtomicItem() {
    throw new InvalidTypeFunctionException(InvalidTypeFunctionException.DATA_ITEM_IS_FUNCTION, this);
  }

  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull INodeItemVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitAssembly(this, context);
  }
}
