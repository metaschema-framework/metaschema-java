/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * The API for accessing information about a given Metaschema module.
 * <p>
 * A Metaschem module may import another Metaschema module. This import graph
 * can be accessed using {@link #getImportedModules()}.
 * <p>
 * Global scoped Metaschema definitions can be accessed using
 * {@link #getScopedAssemblyDefinitionByName(Integer)},
 * {@link #getScopedFieldDefinitionByName(Integer)}, and
 * {@link #getScopedFlagDefinitionByName(IEnhancedQName)}. These methods take
 * into consideration the import order to provide the global definitions that
 * are in scope within the given Metschema module.
 * <p>
 * Global scoped definitions exported by this Metaschema module, available for
 * use by importing Metaschema modules, can be accessed using
 * {@link #getExportedAssemblyDefinitions()},
 * {@link #getExportedFieldDefinitions()}, and
 * {@link #getExportedFlagDefinitions()}.
 * <p>
 * Global scoped definitions defined directly within the given Metaschema module
 * can be accessed using {@link #getAssemblyDefinitions()},
 * {@link #getFieldDefinitions()}, and {@link #getFlagDefinitions()}, along with
 * similarly named access methods.
 *
 * @param <M>
 *          the imported module Java type
 * @param <D>
 *          the model definition Java type
 * @param <FL>
 *          the flag definition Java type
 * @param <FI>
 *          the field definition Java type
 * @param <A>
 *          the assembly definition Java type
 */
public interface IModuleExtended<
    M extends IModuleExtended<M, D, FL, FI, A>,
    D extends IModelDefinition,
    FL extends IFlagDefinition,
    FI extends IFieldDefinition,
    A extends IAssemblyDefinition> extends IModule {

  /**
   * Get a filter that will match all definitions that are not locally defined.
   *
   * @param <DEF>
   *          the type of definition
   * @return a predicate implementing the filter
   */
  static <DEF extends IDefinition> Predicate<DEF> allNonLocalDefinitions() {
    return definition -> IDefinition.ModuleScope.PUBLIC.equals(definition.getModuleScope())
        || ModelType.ASSEMBLY.equals(definition.getModelType())
            && ((IAssemblyDefinition) definition).isRoot();
  }

  /**
   * Get a filter that will match all definitions that are root assemblies.
   *
   * @param <DEF>
   *          the type of definition
   * @return a predicate implementing the filter
   */
  static <DEF extends IDefinition> Predicate<DEF> allRootAssemblyDefinitions() {
    return definition -> ModelType.ASSEMBLY.equals(definition.getModelType())
        && ((IAssemblyDefinition) definition).isRoot();
  }

  @Override
  @NonNull
  List<? extends M> getImportedModules();

  @Override
  @Nullable
  M getImportedModuleByShortName(String name);

  @Override
  @NonNull
  Collection<FL> getFlagDefinitions();

  @Override
  @Nullable
  FL getFlagDefinitionByName(@NonNull IEnhancedQName name);

  @Override
  @NonNull
  Collection<A> getAssemblyDefinitions();

  @Override
  @Nullable
  A getAssemblyDefinitionByName(@NonNull Integer name);

  @Override
  @NonNull
  Collection<FI> getFieldDefinitions();

  @Override
  @Nullable
  FI getFieldDefinitionByName(@NonNull Integer name);

  @Override
  @SuppressWarnings("unchecked")
  @NonNull
  default List<D> getAssemblyAndFieldDefinitions() {
    return ObjectUtils.notNull(
        Stream.concat(
            (Stream<D>) getAssemblyDefinitions().stream(),
            (Stream<D>) getFieldDefinitions().stream())
            .collect(Collectors.toList()));
  }

  @Override
  @Nullable
  default A getScopedAssemblyDefinitionByName(@NonNull Integer name) {
    // first try local/global top-level definitions from current metaschema module
    A retval = getAssemblyDefinitionByName(name);
    if (retval == null) {
      // try global definitions from imported Metaschema modules
      retval = getExportedAssemblyDefinitionByName(name);
    }
    return retval;
  }

  @Override
  @Nullable
  default FI getScopedFieldDefinitionByName(@NonNull Integer name) {
    // first try local/global top-level definitions from current metaschema module
    FI retval = getFieldDefinitionByName(name);
    if (retval == null) {
      // try global definitions from imported metaschema modules
      retval = getExportedFieldDefinitionByName(name);
    }
    return retval;
  }

  @Override
  @Nullable
  default FL getScopedFlagDefinitionByName(@NonNull IEnhancedQName name) {
    // first try local/global top-level definitions from current metaschema module
    FL retval = getFlagDefinitionByName(name);
    if (retval == null) {
      // try global definitions from imported metaschema modules
      retval = getExportedFlagDefinitionByName(name);
    }
    return retval;
  }

  @Override
  @NonNull
  default Collection<? extends A> getExportedRootAssemblyDefinitions() {
    return ObjectUtils.notNull(getExportedAssemblyDefinitions().stream()
        .filter(allRootAssemblyDefinitions())
        .collect(Collectors.toList()));
  }

  @Override
  @NonNull
  default Collection<? extends A> getRootAssemblyDefinitions() {
    return ObjectUtils.notNull(getAssemblyDefinitions().stream()
        .filter(allRootAssemblyDefinitions())
        .collect(Collectors.toList()));
  }

  @Override
  @NonNull
  Collection<? extends FL> getExportedFlagDefinitions();

  @Override
  @Nullable
  FL getExportedFlagDefinitionByName(IEnhancedQName name);

  @Override
  @NonNull
  Collection<? extends FI> getExportedFieldDefinitions();

  @Override
  @Nullable
  FI getExportedFieldDefinitionByName(Integer name);

  @Override
  @NonNull
  Collection<? extends A> getExportedAssemblyDefinitions();

  @Override
  @Nullable
  A getExportedAssemblyDefinitionByName(Integer name);
}
