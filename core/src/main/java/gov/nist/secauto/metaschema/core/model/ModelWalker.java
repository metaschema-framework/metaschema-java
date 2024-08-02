/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Walks a Metaschema model. The "visit" methods can be implemented by child
 * classes to perform processing on a visited node.
 *
 * @param <DATA>
 *          state information that is carried through the walk
 */
public abstract class ModelWalker<DATA> {
  /**
   * Generate default state information.
   *
   * @return the state information
   */
  protected abstract DATA getDefaultData();

  /**
   * Will visit the provided Metaschema module flag definition.
   *
   * @param flag
   *          the Metaschema module flag definition to walk
   */
  public void walk(@NonNull IFlagDefinition flag) {
    walk(flag, getDefaultData());
  }

  /**
   * Will visit the provided Metaschema module flag definition.
   *
   * @param flag
   *          the Metaschema module flag definition to walk
   * @param data
   *          additional state information to operate on
   */
  public void walk(@NonNull IFlagDefinition flag, DATA data) {
    visit(flag, data);
  }

  /**
   * Will visit the provided Metaschema module field definition, and then walk the
   * associated flag instances.
   *
   * @param field
   *          the Metaschema module field definition to walk
   */
  public void walk(@NonNull IFieldDefinition field) {
    walk(field, getDefaultData());
  }

  /**
   * Will visit the provided Metaschema module field definition, and then walk the
   * associated flag instances.
   *
   * @param field
   *          the Metaschema module field definition to walk
   * @param data
   *          additional state information to operate on
   */
  public void walk(@NonNull IFieldDefinition field, DATA data) {
    if (visit(field, data)) {
      walkFlagInstances(field.getFlagInstances(), data);
    }
  }

  /**
   * Will visit the provided Metaschema module assembly definition, and then walk
   * the associated flag and model instances.
   *
   * @param assembly
   *          the Metaschema module assembly definition to walk
   */
  public void walk(@NonNull IAssemblyDefinition assembly) {
    walk(assembly, getDefaultData());
  }

  /**
   * Will visit the provided Metaschema module assembly definition, and then walk
   * the associated flag and model instances.
   *
   * @param assembly
   *          the Metaschema module assembly definition to walk
   * @param data
   *          additional state information to operate on
   */
  public void walk(@NonNull IAssemblyDefinition assembly, DATA data) {
    if (visit(assembly, data)) {
      walkFlagInstances(assembly.getFlagInstances(), data);
      walkModelInstances(assembly.getModelInstances(), data);
    }
  }

  /**
   * Will visit the provided Metaschema module flag instance, and then walk the
   * associated flag definition.
   *
   * @param instance
   *          the Metaschema module flag instance to walk
   * @param data
   *          additional state information to operate on
   */
  public void walk(@NonNull IFlagInstance instance, DATA data) {
    if (visit(instance, data)) {
      walk(instance.getDefinition(), data);
    }
  }

  /**
   * Will visit the provided Metaschema module field instance, and then walk the
   * associated field definition.
   *
   * @param instance
   *          the Metaschema module field instance to walk
   * @param data
   *          additional state information to operate on
   */
  public void walk(@NonNull IFieldInstance instance, DATA data) {
    if (visit(instance, data)) {
      walk(instance.getDefinition(), data);
    }
  }

  /**
   * Will visit the provided Metaschema module assembly instance, and then walk
   * the associated assembly definition.
   *
   * @param instance
   *          the Metaschema module assembly instance to walk
   * @param data
   *          additional state information to operate on
   */
  public void walk(@NonNull IAssemblyInstance instance, DATA data) {
    if (visit(instance, data)) {
      walk(instance.getDefinition(), data);
    }
  }

  /**
   * Will visit the provided Metaschema module choice instance, and then walk the
   * choice's child model instances.
   *
   * @param instance
   *          the Metaschema module choice instance to walk
   * @param data
   *          additional state information to operate on
   */
  public void walk(@NonNull IChoiceInstance instance, DATA data) {
    if (visit(instance, data)) {
      walkModelInstances(instance.getModelInstances(), data);
    }
  }

  /**
   * Will visit the provided Metaschema module choice group instance, and then
   * walk the choice's child model instances.
   *
   * @param instance
   *          the Metaschema module choice instance to walk
   * @param data
   *          additional state information to operate on
   */
  public void walk(@NonNull IChoiceGroupInstance instance, DATA data) {
    if (visit(instance, data)) {
      walkModelInstances(instance.getModelInstances(), data);
    }
  }

  /**
   * Will walk the provided model definition.
   *
   * @param definition
   *          the definition to walk
   */
  public void walkDefinition(@NonNull IDefinition definition) {
    walkDefinition(definition, getDefaultData());
  }

  /**
   * Will walk the provided model definition.
   *
   * @param definition
   *          the definition to walk
   * @param data
   *          additional state information to operate on
   */
  public void walkDefinition(@NonNull IDefinition definition, DATA data) {
    if (definition instanceof IAssemblyDefinition) {
      walk((IAssemblyDefinition) definition, data);
    } else if (definition instanceof IFieldDefinition) {
      walk((IFieldDefinition) definition, data);
    } else if (definition instanceof IFlagDefinition) {
      walk((IFlagDefinition) definition, data);
    }
  }

  /**
   * Will walk each of the provided flag instances.
   *
   * @param instances
   *          a collection of flag instances to visit
   * @param data
   *          additional state information to operate on
   */
  protected void walkFlagInstances(@NonNull Collection<? extends IFlagInstance> instances, DATA data) {
    for (IFlagInstance instance : instances) {
      assert instance != null;
      walk(instance, data);
    }
  }

  /**
   * Will walk each of the provided model instances.
   *
   * @param instances
   *          a collection of model instances to visit
   * @param data
   *          additional state information to operate on
   */
  protected void walkModelInstances(@NonNull Collection<? extends IModelInstance> instances, DATA data) {
    for (IModelInstance instance : instances) {
      assert instance != null;
      walkModelInstance(instance, data);
    }
  }

  /**
   * Will walk the provided model instance.
   *
   * @param instance
   *          the instance to walk
   * @param data
   *          additional state information to operate on
   */
  protected void walkModelInstance(@NonNull IModelInstance instance, DATA data) {
    if (instance instanceof IAssemblyInstance) {
      walk((IAssemblyInstance) instance, data);
    } else if (instance instanceof IFieldInstance) {
      walk((IFieldInstance) instance, data);
    } else if (instance instanceof IChoiceGroupInstance) {
      walk((IChoiceGroupInstance) instance, data);
    } else if (instance instanceof IChoiceInstance) {
      walk((IChoiceInstance) instance, data);
    }
  }

  /**
   * Will visit the provided model definition.
   *
   * @param definition
   *          the definition to visit
   * @param data
   *          additional state information to operate on
   */
  protected void visitDefinition(@NonNull IDefinition definition, DATA data) {
    if (definition instanceof IAssemblyDefinition) {
      visit((IAssemblyDefinition) definition, data);
    } else if (definition instanceof IFieldDefinition) {
      visit((IFieldDefinition) definition, data);
    } else if (definition instanceof IFlagDefinition) {
      visit((IFlagDefinition) definition, data);
    }
  }

  /**
   * Called when the provided definition is walked. This can be overridden by
   * child classes to enable processing of the visited definition.
   *
   * @param def
   *          the definition that is visited
   * @param data
   *          additional state information to operate on
   */
  protected abstract void visit(@NonNull IFlagDefinition def, DATA data);

  /**
   * Called when the provided definition is walked. This can be overridden by
   * child classes to enable processing of the visited definition.
   *
   * @param def
   *          the definition that is visited
   * @param data
   *          additional state information to operate on
   * @return {@code true} if child instances are to be walked, or {@code false}
   *         otherwise
   */
  protected boolean visit(@NonNull IFieldDefinition def, DATA data) {
    return true;
  }

  /**
   * Called when the provided definition is walked. This can be overridden by
   * child classes to enable processing of the visited definition.
   *
   * @param def
   *          the definition that is visited
   * @param data
   *          additional state information to operate on
   * @return {@code true} if child instances are to be walked, or {@code false}
   *         otherwise
   */
  protected boolean visit(@NonNull IAssemblyDefinition def, DATA data) {
    return true;
  }

  /**
   * Called when the provided instance is walked. This can be overridden by child
   * classes to enable processing of the visited instance.
   *
   * @param instance
   *          the instance that is visited
   * @param data
   *          additional state information to operate on
   * @return {@code true} if the associated definition is to be walked, or
   *         {@code false} otherwise
   */
  protected boolean visit(@NonNull IFlagInstance instance, DATA data) {
    return true;
  }

  /**
   * Called when the provided instance is walked. This can be overridden by child
   * classes to enable processing of the visited instance.
   *
   * @param instance
   *          the instance that is visited
   * @param data
   *          additional state information to operate on
   * @return {@code true} if the associated definition is to be walked, or
   *         {@code false} otherwise
   */
  protected boolean visit(@NonNull IFieldInstance instance, DATA data) {
    return true;
  }

  /**
   * Called when the provided instance is walked. This can be overridden by child
   * classes to enable processing of the visited instance.
   *
   * @param instance
   *          the instance that is visited
   * @param data
   *          additional state information to operate on
   * @return {@code true} if the associated definition is to be walked, or
   *         {@code false} otherwise
   */
  protected boolean visit(@NonNull IAssemblyInstance instance, DATA data) {
    return true;
  }

  /**
   * Called when the provided instance is walked. This can be overridden by child
   * classes to enable processing of the visited instance.
   *
   * @param instance
   *          the instance that is visited
   * @param data
   *          additional state information to operate on
   * @return {@code true} if the child instances are to be walked, or
   *         {@code false} otherwise
   */
  protected boolean visit(@NonNull IChoiceInstance instance, DATA data) {
    return true;
  }

  /**
   * Called when the provided instance is walked. This can be overridden by child
   * classes to enable processing of the visited instance.
   *
   * @param instance
   *          the instance that is visited
   * @param data
   *          additional state information to operate on
   * @return {@code true} if the child instances are to be walked, or
   *         {@code false} otherwise
   */
  protected boolean visit(@NonNull IChoiceGroupInstance instance, DATA data) {
    return true;
  }
}
