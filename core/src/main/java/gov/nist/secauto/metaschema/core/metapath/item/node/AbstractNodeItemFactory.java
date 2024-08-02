
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAbsolute;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstance;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractNodeItemFactory implements INodeItemFactory, INodeItemGenerator {
  @Override
  public IDocumentNodeItem newDocumentNodeItem(
      IAssemblyDefinition definition,
      URI documentUri,
      Object value) {
    return new DocumentNodeItemImpl(
        definition,
        value,
        documentUri,
        this);
  }

  @Override
  public IModuleNodeItem newModuleNodeItem(IModule module) {
    return new ModuleNodeItemImpl(
        module,
        this);
  }

  @Override
  public IFieldNodeItem newFieldNodeItem(
      IFieldDefinition definition,
      IModuleNodeItem module) {
    return new FieldGlobalDefinitionNodeItemImpl(
        definition,
        module,
        this);
  }

  @Override
  public IFieldNodeItem newFieldNodeItem(
      IFieldDefinition definition,
      URI baseUri) {
    return new FieldOrphanedDefinitionNodeItemImpl(
        definition,
        baseUri,
        this);
  }

  @Override
  public IFieldNodeItem newFieldNodeItem(
      IFieldInstance instance,
      IAssemblyNodeItem parent) {
    return new FieldInstanceNoValueNodeItemImpl(instance, parent, this);
  }

  @Override
  public IFieldNodeItem newFieldNodeItem(
      IFieldInstance instance,
      IAssemblyNodeItem parent,
      int position,
      Object value) {
    return new FieldInstanceNodeItemImpl(instance, parent, position, value, this);
  }

  @Override
  public IAssemblyNodeItem newAssemblyNodeItem(
      IAssemblyDefinition definition,
      IModuleNodeItem module) {
    return new AssemblyGlobalDefinitionNodeItemImpl(
        definition,
        module,
        this);
  }

  @Override
  public IAssemblyNodeItem newAssemblyNodeItem(
      IAssemblyDefinition definition,
      URI baseUri) {
    return new AssemblyOrphanedDefinitionNodeItemImpl(
        definition,
        baseUri,
        this);
  }

  @Override
  public IAssemblyNodeItem newAssemblyNodeItem(
      IAssemblyDefinition definition,
      URI baseUri,
      Object value) {
    return new AssemblyOrphanedDefinitionDataNodeItemImpl(
        definition,
        baseUri,
        value,
        this);
  }

  @Override
  public IAssemblyNodeItem newAssemblyNodeItem(
      IAssemblyInstance instance,
      IAssemblyNodeItem parent) {
    IAssemblyNodeItem retval = null;
    if (!instance.getDefinition().isInline()) {
      // if not inline, need to check for a cycle
      IAssemblyNodeItem cycle = getCycledInstance(instance.getXmlQName(), instance.getDefinition(), parent);
      if (cycle != null) {
        // generate a cycle wrapper of the original node item
        retval = new CycledAssemblyInstanceNodeItemImpl(instance, parent, cycle);
      }
    }

    if (retval == null) {
      retval = new AssemblyInstanceNoValueNodeItemImpl(instance, parent, this);
    }
    return retval;
  }

  @Override
  public IAssemblyNodeItem newAssemblyNodeItem(
      IAssemblyInstance instance,
      IAssemblyNodeItem parent,
      int position,
      Object value) {
    return new AssemblyInstanceNodeItemImpl(instance, parent, position, value, this);
  }

  @Nullable
  private IAssemblyNodeItem getCycledInstance(
      @NonNull QName name,
      @NonNull IAssemblyDefinition definition,
      @NonNull IAssemblyNodeItem parent) {
    IAssemblyNodeItem retval = null;

    IAssemblyDefinition parentDefinition = parent.getDefinition();
    if (parent.getQName().equals(name) && parentDefinition.equals(definition)) {
      retval = parent;
    } else {
      IAssemblyNodeItem ancestor = parent.getParentContentNodeItem();
      if (ancestor != null) {
        retval = getCycledInstance(name, definition, ancestor);
      }
    }
    return retval;
  }

  /**
   * Create a new {@link IModelNodeItem} based on the provided {@code instance}
   * that is a child of the provided {@code parent}. This new item will have the
   * provided {@code value}.
   *
   * @param instance
   *          the model instance to create the node for
   * @param parent
   *          the item to use as the parent item for the created node item
   * @param position
   *          the data item's position in the sequence of data items for the
   *          instance, which is {@code 0} based
   * @param value
   *          the data item's value
   * @return the created node item
   */
  @NonNull
  protected IModelNodeItem<?, ?> newModelItem(
      @NonNull INamedModelInstance instance,
      @NonNull IAssemblyNodeItem parent,
      int position,
      @NonNull Object value) {
    @NonNull IModelNodeItem<?, ?> item;
    if (instance instanceof IAssemblyInstance) {
      item = newAssemblyNodeItem((IAssemblyInstance) instance, parent, position, value);
    } else if (instance instanceof IFieldInstance) {
      item = newFieldNodeItem((IFieldInstance) instance, parent, position, value);
    } else {
      throw new UnsupportedOperationException("unsupported instance type: " + instance.getClass().getName());
    }
    return item;
  }

  /**
   * Create a new {@link IModelNodeItem} based on the provided {@code instance}
   * that is a child of the provided {@code parent}. This new item will have no
   * associated value.
   *
   * @param instance
   *          the model instance to create the node for
   * @param parent
   *          the item to use as the parent item for the created node item
   * @return the created node item
   */
  @NonNull
  protected IModelNodeItem<?, ?> newModelItem(
      @NonNull INamedModelInstance instance,
      @NonNull IAssemblyNodeItem parent) {
    @NonNull IModelNodeItem<?, ?> item;
    if (instance instanceof IAssemblyInstance) {
      item = newAssemblyNodeItem((IAssemblyInstance) instance, parent);
    } else if (instance instanceof IFieldInstance) {
      item = newFieldNodeItem((IFieldInstance) instance, parent);
    } else {
      throw new UnsupportedOperationException("unsupported instance type: " + instance.getClass().getName());
    }
    return item;
  }

  /**
   * Get the descendant model instances of the provided {@code container}.
   *
   * @param container
   *          the container to get descendant instances for
   * @return the stream of descendant instances
   */
  @NonNull
  protected Stream<? extends INamedModelInstance> getNamedModelInstances(@NonNull IContainerModelAbsolute container) {
    return ObjectUtils.notNull(container.getModelInstances().stream()
        .flatMap(instance -> {
          Stream<? extends INamedModelInstance> retval;
          if (instance instanceof IAssemblyInstanceAbsolute || instance instanceof IFieldInstanceAbsolute) {
            retval = Stream.of((INamedModelInstanceAbsolute) instance);
          } else if (instance instanceof IChoiceInstance) {
            // descend into the choice
            retval = getNamedModelInstances((IChoiceInstance) instance);
          } else if (instance instanceof IChoiceGroupInstance) {
            IChoiceGroupInstance choiceGroupInstance = (IChoiceGroupInstance) instance;
            retval = choiceGroupInstance.getNamedModelInstances().stream();
          } else {
            throw new UnsupportedOperationException("unsupported instance type: " + instance.getClass().getName());
          }
          return retval;
        }));
  }

  /**
   * Get the descendant model instances of the provided {@code container}.
   *
   * @param container
   *          the container to get descendant instances for
   * @return the stream of descendant instances
   */
  @NonNull
  protected Stream<? extends IModelInstance> getValuedModelInstances(@NonNull IContainerModelAbsolute container) {
    return ObjectUtils.notNull(container.getModelInstances().stream()
        .flatMap(instance -> {
          Stream<? extends IModelInstance> retval;
          if (instance instanceof IAssemblyInstanceAbsolute || instance instanceof IFieldInstanceAbsolute) {
            retval = Stream.of((INamedModelInstanceAbsolute) instance);
          } else if (instance instanceof IChoiceInstance) {
            // descend into the choice
            retval = getNamedModelInstances((IChoiceInstance) instance);
          } else if (instance instanceof IChoiceGroupInstance) {
            retval = Stream.of((IChoiceGroupInstance) instance);
          } else {
            throw new UnsupportedOperationException("unsupported instance type: " + instance.getClass().getName());
          }
          return retval;
        }));
  }
}
