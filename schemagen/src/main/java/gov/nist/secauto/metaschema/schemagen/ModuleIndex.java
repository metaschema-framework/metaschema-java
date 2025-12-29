/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.ModelWalker;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Indexes definitions from a Metaschema module for use in schema generation.
 * <p>
 * This class maintains an ordered index of all definitions that are reachable
 * from root assembly definitions, tracking their reference counts, inline
 * status, and other usage patterns relevant to schema generation.
 */
public class ModuleIndex {
  // needs to be ordered
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private final Map<IDefinition, DefinitionEntry> index = new LinkedHashMap<>();

  /**
   * Creates an index of all definitions reachable from the module's root assembly
   * definitions.
   *
   * @param module
   *          the Metaschema module to index
   * @param inlineStrategy
   *          the strategy for determining which definitions should be inlined
   * @return a new module index containing entries for all reachable definitions
   */
  @NonNull
  public static ModuleIndex indexDefinitions(@NonNull IModule module, @NonNull IInlineStrategy inlineStrategy) {
    Collection<? extends IAssemblyDefinition> definitions = module.getExportedRootAssemblyDefinitions();
    ModuleIndex index = new ModuleIndex();
    if (!definitions.isEmpty()) {
      IndexVisitor visitor = new IndexVisitor(index, inlineStrategy);
      for (IAssemblyDefinition definition : definitions) {
        assert definition != null;

        // // add the root definition to the index
        // index.getEntry(definition).incrementReferenceCount();

        // walk the definition
        visitor.walk(ObjectUtils.requireNonNull(definition));
      }
    }
    return index;
  }

  /**
   * Checks if an entry exists in this index for the specified definition.
   *
   * @param definition
   *          the definition to check
   * @return {@code true} if an entry exists for the definition, {@code false}
   *         otherwise
   */
  public boolean hasEntry(@NonNull IDefinition definition) {
    return index.containsKey(definition);
  }

  /**
   * Retrieves or creates the entry for the specified definition.
   * <p>
   * If no entry exists for the definition, a new entry is created and added to
   * the index.
   *
   * @param definition
   *          the definition to get an entry for
   * @return the existing or newly created entry for the definition
   */
  @NonNull
  public DefinitionEntry getEntry(@NonNull IDefinition definition) {
    return ObjectUtils.notNull(index.computeIfAbsent(
        definition,
        k -> new ModuleIndex.DefinitionEntry(ObjectUtils.notNull(k))));
  }

  /**
   * Retrieves all definition entries in this index.
   *
   * @return an unmodifiable collection of all definition entries, in insertion
   *         order
   */
  @NonNull
  public Collection<DefinitionEntry> getDefinitions() {
    return ObjectUtils.notNull(index.values());
  }

  private static class IndexVisitor
      extends ModelWalker<ModuleIndex> {
    @NonNull
    private final IInlineStrategy inlineStrategy;
    @NonNull
    private final ModuleIndex index;

    public IndexVisitor(@NonNull ModuleIndex index, @NonNull IInlineStrategy inlineStrategy) {
      this.index = index;
      this.inlineStrategy = inlineStrategy;
    }

    @Override
    protected ModuleIndex getDefaultData() {
      return index;
    }

    @Override
    protected boolean visit(IFlagInstance instance, ModuleIndex index) {
      handleInstance(instance);
      return true;
    }

    @Override
    protected boolean visit(IFieldInstance instance, ModuleIndex index) {
      handleInstance(instance);
      return true;
    }

    @Override
    protected boolean visit(IAssemblyInstance instance, ModuleIndex index) {
      handleInstance(instance);
      return true;
    }

    @Override
    protected void visit(IFlagDefinition def, ModuleIndex data) {
      handleDefinition(def);
    }

    // @Override
    // protected boolean visit(IAssemblyDefinition def, ModuleIndex data) {
    // // only walk if the definition hasn't already been visited
    // return !index.hasEntry(def);
    // }

    @Override
    protected boolean visit(IFieldDefinition def, ModuleIndex data) {
      return handleDefinition(def);
    }

    @Override
    protected boolean visit(IAssemblyDefinition def, ModuleIndex data) {
      return handleDefinition(def);
    }

    private boolean handleDefinition(@NonNull IDefinition definition) {
      DefinitionEntry entry = getDefaultData().getEntry(definition);
      boolean visited = entry.isVisited();
      if (!visited) {
        entry.markVisited();

        if (inlineStrategy.isInline(definition, index)) {
          entry.markInline();
        }
      }
      return !visited;
    }

    /**
     * Updates the index entry for the definition associated with the reference.
     *
     * @param instance
     *          the instance to process
     */
    @NonNull
    private DefinitionEntry handleInstance(INamedInstance instance) {
      IDefinition definition = instance.getDefinition();
      // check if this will be a new entry, which needs to be called before getEntry,
      // which will create it
      DefinitionEntry entry = getDefaultData().getEntry(definition);
      entry.addReference(instance);

      if (isChoice(instance)) {
        entry.markUsedAsChoice();
      }

      if (isChoiceSibling(instance)) {
        entry.markAsChoiceSibling();
      }
      return entry;
    }

    private static boolean isChoice(@NonNull INamedInstance instance) {
      return instance.getParentContainer() instanceof IChoiceInstance;
    }

    private static boolean isChoiceSibling(@NonNull INamedInstance instance) {
      IDefinition containingDefinition = instance.getContainingDefinition();
      return containingDefinition instanceof IAssemblyDefinition
          && !((IAssemblyDefinition) containingDefinition).getChoiceInstances().isEmpty();
    }
  }

  /**
   * Represents an entry in the module index for a single definition.
   * <p>
   * Each entry tracks usage information about a definition including its
   * references, inline status, and how it is used within choice groups.
   */
  public static class DefinitionEntry {
    @NonNull
    private final IDefinition definition;
    private final Set<INamedInstance> references = new HashSet<>();
    private final AtomicBoolean inline = new AtomicBoolean(); // false
    private final AtomicBoolean visited = new AtomicBoolean(); // false
    private final AtomicBoolean usedAsChoice = new AtomicBoolean(); // false
    private final AtomicBoolean choiceSibling = new AtomicBoolean(); // false

    /**
     * Constructs a new definition entry for the specified definition.
     *
     * @param definition
     *          the definition this entry represents
     */
    public DefinitionEntry(@NonNull IDefinition definition) {
      this.definition = definition;
    }

    /**
     * Retrieves the definition associated with this entry.
     *
     * @return the definition
     */
    @NonNull
    public IDefinition getDefinition() {
      return definition;
    }

    /**
     * Checks if this definition is a root assembly definition.
     *
     * @return {@code true} if the definition is a root assembly, {@code false}
     *         otherwise
     */
    public boolean isRoot() {
      return definition instanceof IAssemblyDefinition
          && ((IAssemblyDefinition) definition).isRoot();
    }

    /**
     * Checks if this definition is referenced by any instance or is a root
     * definition.
     *
     * @return {@code true} if the definition has references or is a root,
     *         {@code false} otherwise
     */
    public boolean isReferenced() {
      return !references.isEmpty()
          || isRoot();
    }

    /**
     * Retrieves all instances that reference this definition.
     *
     * @return a set of referencing instances
     */
    public Set<INamedInstance> getReferences() {
      return references;
    }

    /**
     * Adds a reference to this definition from the specified instance.
     *
     * @param reference
     *          the instance referencing this definition
     * @return {@code true} if the reference was added, {@code false} if it already
     *         existed
     */
    public boolean addReference(@NonNull INamedInstance reference) {
      return references.add(reference);
    }

    /**
     * Marks this definition as having been visited during indexing.
     */
    public void markVisited() {
      visited.compareAndSet(false, true);
    }

    /**
     * Checks if this definition has been visited during indexing.
     *
     * @return {@code true} if the definition was visited, {@code false} otherwise
     */
    public boolean isVisited() {
      return visited.get();
    }

    /**
     * Marks this definition as being inlined in the generated schema.
     */
    public void markInline() {
      inline.compareAndSet(false, true);
    }

    /**
     * Checks if this definition should be inlined in the generated schema.
     *
     * @return {@code true} if the definition is inlined, {@code false} otherwise
     */
    public boolean isInline() {
      return inline.get();
    }

    /**
     * Marks this definition as being used within a choice group.
     */
    public void markUsedAsChoice() {
      usedAsChoice.compareAndSet(false, true);
    }

    /**
     * Checks if this definition is used within a choice group.
     *
     * @return {@code true} if the definition is used as a choice, {@code false}
     *         otherwise
     */
    public boolean isUsedAsChoice() {
      return usedAsChoice.get();
    }

    /**
     * Marks this definition as having sibling elements in a choice group.
     */
    public void markAsChoiceSibling() {
      choiceSibling.compareAndSet(false, true);
    }

    /**
     * Checks if this definition has sibling elements in a choice group.
     *
     * @return {@code true} if the definition is a choice sibling, {@code false}
     *         otherwise
     */
    public boolean isChoiceSibling() {
      return choiceSibling.get();
    }

    /**
     * Checks if any reference to this definition uses a JSON key flag.
     *
     * @return {@code true} if any reference has a JSON key, {@code false} otherwise
     */
    public boolean isUsedAsJsonKey() {
      return references.stream()
          .anyMatch(ref -> ref instanceof INamedModelInstance
              && ((INamedModelInstance) ref).hasJsonKey());
    }

    /**
     * Checks if this definition is used without a JSON key flag or is a flag
     * definition.
     *
     * @return {@code true} if the definition is a flag or has any references
     *         without a JSON key, {@code false} otherwise
     */
    public boolean isUsedWithoutJsonKey() {
      return definition instanceof IFlagDefinition
          || references.isEmpty()
          || references.stream()
              .anyMatch(ref -> ref instanceof INamedModelInstance
                  && !((INamedModelInstance) ref).hasJsonKey());
    }

    /**
     * Checks if this definition is a member of a choice group.
     *
     * @return {@code true} if any reference is a grouped model instance,
     *         {@code false} otherwise
     */
    public boolean isChoiceGroupMember() {
      return references.stream()
          .anyMatch(INamedModelInstanceGrouped.class::isInstance);
    }
  }
}
