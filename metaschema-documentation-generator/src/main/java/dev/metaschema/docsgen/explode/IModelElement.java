
package dev.metaschema.docsgen.explode;

import dev.metaschema.docsgen.explode.IModelElementVisitor.IModelElementVisitable;
import dev.metaschema.model.common.IDefinition;
import dev.metaschema.model.common.INamedInstance;
import dev.metaschema.model.common.constraint.IConstraint;
import dev.metaschema.model.common.metapath.item.IDefinitionNodeItem;

import java.util.List;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A marker interface that identifies a Metaschema construct as part of a definition's model.
 */
public interface IModelElement extends IModelElementVisitable {
  @NonNull
  IDefinitionNodeItem getNodeItem();

  @NonNull
  List<IModelElement> getFlags();

  @NonNull
  List<IModelElement> getModelItems();

  void addConstraint(@NonNull IConstraint constraint);

  @NonNull
  Set<IConstraint> getConstraints();

  @Nullable
  INamedInstance getInstance();

  @NonNull
  IDefinition getDefinition();
}
