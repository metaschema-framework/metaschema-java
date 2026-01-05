/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.testsupport.builder;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.INamedModelInstanceAbsolute;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.qname.IEnhancedQName;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A reference to an assembly definition by name. This class implements
 * {@link IModelBuilder} to allow it to be used in model instance lists, but the
 * actual instance is created during module resolution when the referenced
 * definition is available.
 *
 * <p>
 * This enables recursive assembly structures where an assembly can contain
 * instances of itself or other assemblies that are defined later in the module.
 */
final class AssemblyReference
    implements IModelBuilder<AssemblyReference>, IModelReference {

  private final String referencedName;
  private ISource source;

  /**
   * Construct a new assembly reference.
   *
   * @param referencedName
   *          the local name of the referenced assembly definition
   */
  AssemblyReference(@NonNull String referencedName) {
    this.referencedName = referencedName;
  }

  @Override
  @NonNull
  public String getReferencedName() {
    return referencedName;
  }

  @Override
  public AssemblyReference reset() {
    this.source = null;
    return this;
  }

  @Override
  @NonNull
  public AssemblyReference namespace(@NonNull String name) {
    // References use the name from construction; namespace is set by ModuleBuilder
    return this;
  }

  @Override
  @NonNull
  public AssemblyReference name(@NonNull String name) {
    // References use the name from construction
    return this;
  }

  @Override
  @NonNull
  public AssemblyReference qname(@NonNull IEnhancedQName qname) {
    // References use the name from construction
    return this;
  }

  @Override
  @NonNull
  public AssemblyReference source(@NonNull ISource source) {
    this.source = source;
    return this;
  }

  /**
   * Get the source associated with this reference.
   *
   * @return the source, or {@code null} if not set
   */
  @Nullable
  protected ISource getSource() {
    return source;
  }

  @Override
  @NonNull
  public AssemblyReference flags(@Nullable List<IFlagBuilder> flags) {
    // References don't support flags - they reference existing definitions
    throw new UnsupportedOperationException("Assembly references cannot have flags");
  }

  /**
   * This method should not be called directly. Assembly references are resolved
   * during module construction.
   *
   * @param parent
   *          ignored
   * @return never returns normally
   * @throws UnsupportedOperationException
   *           always, as references must be resolved during module construction
   */
  @Override
  @NonNull
  public INamedModelInstanceAbsolute toInstance(@NonNull IAssemblyDefinition parent) {
    throw new UnsupportedOperationException(
        "Assembly references must be resolved during module construction. "
            + "Use ModuleBuilder to build the module, which will resolve references.");
  }
}
