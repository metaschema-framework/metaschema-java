
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModule;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * This factory interface is used to create {@link INodeItem} objects of various
 * types.
 * <p>
 * A singleton instance of this factory can be acquired using the
 * {@link #instance()} method.
 */
public interface INodeItemFactory {

  /**
   * Get the singleton instance of the default node factory.
   *
   * @return the node factory instance
   */
  @NonNull
  static INodeItemFactory instance() {
    return DefaultNodeItemFactory.instance();
  }

  /**
   * Create a new document node item for the provided {@code definition}.
   *
   * @param definition
   *          the root assembly definition to create the document item for.
   * @param documentUri
   *          the uniform resource identifier of the document
   * @param value
   *          the root item's associated data
   * @return the new node item
   */
  @NonNull
  IDocumentNodeItem newDocumentNodeItem(
      @NonNull IAssemblyDefinition definition,
      @NonNull URI documentUri,
      @NonNull Object value);

  /**
   * Create a new Metaschema node item for the provided {@code metaschema}.
   *
   * @param module
   *          the Metaschema to create the item for.
   * @return the new node item
   */
  @NonNull
  IModuleNodeItem newModuleNodeItem(@NonNull IModule module);

  /**
   * Create a new {@link IFlagNodeItem}, with no associated value, based on the
   * provided flag definition.
   *
   * @param definition
   *          the flag definition
   * @param parent
   *          the item for the Metaschema containing the definition
   * @return the new flag node item
   */
  @NonNull
  default IFlagNodeItem newFlagNodeItem(
      @NonNull IFlagDefinition definition,
      @NonNull IModuleNodeItem parent) {
    return new FlagGlobalDefinitionNodeItemImpl(definition, parent);
  }

  /**
   * Create a new {@link IFlagNodeItem} based on the provided flag instance.
   *
   * @param instance
   *          the flag instance
   * @param parent
   *          the node item containing the flag
   * @return the new flag node item
   */
  @NonNull
  default IFlagNodeItem newFlagNodeItem(
      @NonNull IFlagInstance instance,
      @NonNull IModelNodeItem<?, ?> parent) {
    return new FlagInstanceNoValueNodeItemImpl(instance, parent);
  }

  /**
   * Create a new {@link IFlagNodeItem} based on the provided flag instance.
   *
   * @param instance
   *          the flag instance
   * @param parent
   *          the node item containing the flag
   * @param value
   *          the item's associated data
   * @return the new flag node item
   */
  @NonNull
  default IFlagNodeItem newFlagNodeItem(
      @NonNull IFlagInstance instance,
      @NonNull IModelNodeItem<?, ?> parent,
      @NonNull Object value) {
    return new FlagInstanceNodeItemImpl(instance, parent, value);
  }

  /**
   * Create a new {@link IFieldNodeItem} based on the provided definition, which
   * is expected to be a global definition within the provided Metaschema.
   *
   * @param definition
   *          the global definition
   * @param metaschema
   *          the Metaschema containing the definition
   * @return the new field node item
   */
  @NonNull
  IFieldNodeItem newFieldNodeItem(
      @NonNull IFieldDefinition definition,
      @NonNull IModuleNodeItem metaschema);

  /**
   * Create a new {@link IFieldNodeItem} that is detached from a Metaschema.
   *
   * @param definition
   *          the global definition
   * @param baseUri
   *          the base URI to use for this node item when evaluating a
   *          {@link MetapathExpression}
   * @return the new field node item
   */
  @NonNull
  IFieldNodeItem newFieldNodeItem(
      @NonNull IFieldDefinition definition,
      @Nullable URI baseUri);

  /**
   * Create a new {@link IFieldNodeItem} that is based on a Metaschema instance.
   * <p>
   * A single instance of this item is expected to represent the possibility in a
   * metaschema of a series of instance values.
   *
   * @param instance
   *          the Metaschema field instance
   * @param parent
   *          the parent node item
   * @return the new field node item
   */
  @NonNull
  IFieldNodeItem newFieldNodeItem(
      @NonNull IFieldInstance instance,
      @NonNull IAssemblyNodeItem parent);

  /**
   * Create a new {@link IFieldNodeItem} that is based on a Metaschema instance
   * with associated data.
   *
   * @param instance
   *          the Metaschema field instance
   * @param parent
   *          the parent node item
   * @param position
   *          the data item's position in the sequence of data items for the
   *          instance
   * @param value
   *          the item's associated data
   * @return the new field node item
   */
  @NonNull
  IFieldNodeItem newFieldNodeItem(
      @NonNull IFieldInstance instance,
      @NonNull IAssemblyNodeItem parent,
      int position,
      @NonNull Object value);

  /**
   * Create a new {@link IAssemblyNodeItem} that is detached from a Metaschema.
   *
   * @param definition
   *          the global definition
   * @return the new assembly node item
   */
  @NonNull
  default IAssemblyNodeItem newAssemblyNodeItem(
      @NonNull IAssemblyDefinition definition) {
    return newAssemblyNodeItem(definition, (URI) null);
  }

  /**
   * Create a new {@link IAssemblyNodeItem} based on the provided definition,
   * which is expected to be a global definition within the provided Metaschema.
   *
   * @param definition
   *          the global definition
   * @param metaschema
   *          the Metaschema containing the definition
   * @return the new assembly node item
   */
  @NonNull
  IAssemblyNodeItem newAssemblyNodeItem(
      @NonNull IAssemblyDefinition definition,
      @NonNull IModuleNodeItem metaschema);

  /**
   * Create a new {@link IAssemblyNodeItem} that is detached from a Metaschema.
   *
   * @param definition
   *          the global definition
   * @param baseUri
   *          the base URI to use for this node item when evaluating a
   *          {@link MetapathExpression}
   * @return the new assembly node item
   */
  @NonNull
  IAssemblyNodeItem newAssemblyNodeItem(
      @NonNull IAssemblyDefinition definition,
      @Nullable URI baseUri);

  /**
   * Create a new {@link IAssemblyNodeItem} that is detached from a Metaschema,
   * with associated data.
   *
   * @param definition
   *          the global definition
   * @param baseUri
   *          the base URI to use for this node item when evaluating a
   *          {@link MetapathExpression}
   * @param value
   *          the associated data
   * @return the new assembly node item
   */
  @NonNull
  IAssemblyNodeItem newAssemblyNodeItem(
      @NonNull IAssemblyDefinition definition,
      @Nullable URI baseUri,
      @NonNull Object value);

  /**
   * Create a new {@link IAssemblyNodeItem} that is based on a Metaschema
   * instance.
   * <p>
   * A single instance of this item is expected to represent the possibility in a
   * metaschema of a series of instance values.
   *
   * @param instance
   *          the Metaschema assembly instance
   * @param parent
   *          the parent node item
   * @return the new assembly node item
   */
  @NonNull
  IAssemblyNodeItem newAssemblyNodeItem(
      @NonNull IAssemblyInstance instance,
      @NonNull IAssemblyNodeItem parent);

  /**
   * Create a new {@link IAssemblyNodeItem} that is based on a Metaschema instance
   * with associated data.
   *
   * @param instance
   *          the Metaschema assembly instance
   * @param parent
   *          the parent node item
   * @param position
   *          the data item's position in the sequence of data items for the
   *          instance, which is {@code 0} based
   * @param value
   *          the data item's value
   * @return the new assembly node item
   */
  @NonNull
  IAssemblyNodeItem newAssemblyNodeItem(
      @NonNull IAssemblyInstanceGrouped instance,
      @NonNull IAssemblyNodeItem parent,
      int position,
      @NonNull Object value);

  /**
   * Create a new {@link IAssemblyNodeItem} that is based on a Metaschema instance
   * with associated data.
   *
   * @param instance
   *          the Metaschema assembly instance
   * @param parent
   *          the parent node item
   * @param position
   *          the data item's position in the sequence of data items for the
   *          instance, which is {@code 0} based
   * @param value
   *          the data item's value
   * @return the new assembly node item
   */
  @NonNull
  IAssemblyNodeItem newAssemblyNodeItem(
      @NonNull IAssemblyInstance instance,
      @NonNull IAssemblyNodeItem parent,
      int position,
      @NonNull Object value);
}
