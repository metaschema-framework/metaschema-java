/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This model walker can be used to gather metaschema definitions that are
 * defined globally.
 */
public class UsedDefinitionModelWalker
    extends DefinitionCollectingModelWalker {
  private static final Function<IDefinition, Boolean> FILTER = (def) -> {
    return true;
    // return def.isGlobal();
    // return def.isGlobal() || (def instanceof AssemblyDefinition &&
    // ((AssemblyDefinition)def).getRootName() != null);
  };

  /**
   * Get the collection of all definitions used directly and transitively by the
   * provided definitions.
   *
   * @param definitions
   *          a collection of definitions to generate used definitions from
   * @return the collection of used definitions
   */
  @NonNull
  public static Collection<? extends IDefinition>
      collectUsedDefinitions(Collection<? extends IAssemblyDefinition> definitions) {
    UsedDefinitionModelWalker walker = new UsedDefinitionModelWalker();
    for (IAssemblyDefinition definition : definitions) {
      assert definition != null;
      walker.walk(definition);
    }
    return walker.getDefinitions();
  }

  /**
   * Collect the globally defined Metaschema definitions from the provided
   * Metaschema modules, and any Metaschema modules imported directly or
   * indirectly by these modules.
   *
   * @param modules
   *          the Metaschema modules to analyze
   * @return a collection of matching definitions
   */
  @NonNull
  public static Collection<? extends IDefinition> collectUsedDefinitionsFromModule(
      @NonNull Collection<? extends IModule> modules) {
    Set<IAssemblyDefinition> definitions = new HashSet<>();
    for (IModule module : modules) {
      // get local roots in case they are scope=local
      for (IAssemblyDefinition rootDef : module.getRootAssemblyDefinitions()) {
        definitions.add(rootDef);
      }

      // get roots from exported
      for (IAssemblyDefinition assembly : module.getExportedAssemblyDefinitions()) {
        if (assembly.isRoot()) {
          definitions.add(assembly);
        }
      }
    }
    return collectUsedDefinitions(definitions);
  }

  /**
   * Collect the globally defined Metaschema definitions from the provided
   * Metaschema module, and any Metaschema modules imported directly or indirectly
   * by this module.
   *
   * @param module
   *          the metaschema module to analyze
   * @return a collection of matching definitions
   */
  @NonNull
  public static Collection<? extends IDefinition> collectUsedDefinitionsFromModule(
      @NonNull IModule module) {
    return collectUsedDefinitionsFromModule(CollectionUtil.singleton(module));
  }

  /**
   * Construct a new walker.
   */
  protected UsedDefinitionModelWalker() {
    super(FILTER);
  }
}
