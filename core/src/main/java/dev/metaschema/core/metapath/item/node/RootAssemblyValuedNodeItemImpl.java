
package dev.metaschema.core.metapath.item.node;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.util.ObjectUtils;

import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

class RootAssemblyValuedNodeItemImpl
    extends AbstractNodeItem
    implements IRootAssemblyNodeItem, IFeatureModelContainerItem {
  @NonNull
  private final IAssemblyDefinition definition;
  @NonNull
  private final IDocumentNodeItem parent;

  @NonNull
  private final Lazy<ModelContainer> model;
  @NonNull
  private final Object value;

  public RootAssemblyValuedNodeItemImpl(
      @NonNull IAssemblyDefinition definition,
      @NonNull IDocumentNodeItem parent,
      @NonNull Object value,
      @NonNull INodeItemGenerator generator) {
    this.definition = definition;
    this.parent = parent;
    this.model = ObjectUtils.notNull(Lazy.of(generator.newDataModelSupplier((IAssemblyNodeItem) this)));
    this.value = value;
  }

  @Override
  public IAssemblyDefinition getDefinition() {
    return definition;
  }

  @Override
  public IDocumentNodeItem getDocumentNodeItem() {
    return parent;
  }

  @Override
  public IAssemblyNodeItem getParentContentNodeItem() {
    // there is no parent assembly
    return null;
  }

  @Override
  @NonNull
  public Object getValue() {
    return value;
  }

  @SuppressWarnings("null")
  @Override
  public ModelContainer getModel() {
    return model.get();
  }

  @Override
  public String stringValue() {
    return ObjectUtils.notNull(modelItems()
        .map(INodeItem::stringValue)
        .collect(Collectors.joining()));
  }

  @Override
  protected String getValueSignature() {
    return null;
  }
}
