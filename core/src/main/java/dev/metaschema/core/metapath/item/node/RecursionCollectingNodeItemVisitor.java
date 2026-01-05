/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.node;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Analyzes a Metaschema module to identify assembly definitions that are
 * recursive.
 * <p>
 * This visitor traverses the module structure and tracks assembly definitions
 * that contain themselves as descendants, collecting locations where recursion
 * occurs.
 */
public class RecursionCollectingNodeItemVisitor
    extends AbstractRecursionPreventingNodeItemVisitor<Void, Void> {

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private final Map<IAssemblyDefinition, AssemblyRecord> assemblyAnalysis = new LinkedHashMap<>();

  /**
   * Get the identified assembly definitions that recurse.
   *
   * @return the definitions that recurse
   * @see AssemblyRecord#isRecursive()
   */
  @NonNull
  public Set<AssemblyRecord> getRecursiveAssemblyDefinitions() {
    return ObjectUtils.notNull(assemblyAnalysis.values().stream()
        .filter(AssemblyRecord::isRecursive)
        .collect(Collectors.toSet()));
  }

  /**
   * Visit the provided module.
   *
   * @param module
   *          the Metaschema module to visit
   */
  public void visit(@NonNull IModule module) {
    visitMetaschema(INodeItemFactory.instance().newModuleNodeItem(module), null);
  }

  @Override
  public Void visitAssembly(IAssemblyNodeItem item, Void context) {
    IAssemblyDefinition definition = item.getDefinition();

    // get the assembly record from the cache
    AssemblyRecord record = assemblyAnalysis.get(definition);
    if (record == null) {
      record = new AssemblyRecord(definition);
      assemblyAnalysis.put(definition, record);
    } else if (isDecendant(item, definition)) {
      record.markRecursive();
      record.addLocation(item);
    }
    return super.visitAssembly(item, context);
  }

  @Override
  public Void visitAssembly(IAssemblyInstanceGroupedNodeItem item, Void context) {
    return visitAssembly((IAssemblyNodeItem) item, context);
  }

  @Override
  protected Void defaultResult() {
    return null;
  }

  /**
   * Records information about an assembly definition, including whether it is
   * recursive and where it is used.
   */
  public static final class AssemblyRecord {
    @NonNull
    private final IAssemblyDefinition definition;
    private boolean recursive; // false
    @NonNull
    private final List<IDefinitionNodeItem<?, ?>> locations = new LinkedList<>();

    private AssemblyRecord(@NonNull IAssemblyDefinition definition) {
      this.definition = definition;
    }

    /**
     * Get the definition associated with the record.
     *
     * @return the definition
     */
    @NonNull
    public IAssemblyDefinition getDefinition() {
      return definition;
    }

    /**
     * Determine if the definition associated with the record is a descendant of
     * itself.
     *
     * @return {@code true} if the definition is a descendant of itself or
     *         {@code false} otherwise
     */
    public boolean isRecursive() {
      return recursive;
    }

    /**
     * Mark the record as recursive.
     *
     * @see #isRecursive()
     */
    private void markRecursive() {
      recursive = true;
    }

    /**
     * Get the node locations where the definition associated with this record is
     * used.
     *
     * @return the node locations
     */
    @NonNull
    public List<IDefinitionNodeItem<?, ?>> getLocations() {
      return locations;
    }

    /**
     * Associate the provided location with the definition associated with the
     * record.
     *
     * @param location
     *          the location to associate
     */
    public void addLocation(@NonNull IDefinitionNodeItem<?, ?> location) {
      this.locations.add(location);
    }
  }

}
