/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testsupport.builder;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A reference to a field definition by name. This class implements
 * {@link IModelBuilder} to allow it to be used in model instance lists, but the
 * actual instance is created during module resolution when the referenced
 * definition is available.
 *
 * <p>
 * This enables assembly structures that reference fields defined elsewhere in
 * the module.
 */
final class FieldReference
    implements IModelBuilder<FieldReference>, IModelReference {

  private final String referencedName;
  private ISource source;

  /**
   * Construct a new field reference.
   *
   * @param referencedName
   *          the local name of the referenced field definition
   */
  FieldReference(@NonNull String referencedName) {
    this.referencedName = referencedName;
  }

  @Override
  @NonNull
  public String getReferencedName() {
    return referencedName;
  }

  @Override
  public FieldReference reset() {
    this.source = null;
    return this;
  }

  @Override
  @NonNull
  public FieldReference namespace(@NonNull String name) {
    // References use the name from construction; namespace is set by ModuleBuilder
    return this;
  }

  @Override
  @NonNull
  public FieldReference name(@NonNull String name) {
    // References use the name from construction
    return this;
  }

  @Override
  @NonNull
  public FieldReference qname(@NonNull IEnhancedQName qname) {
    // References use the name from construction
    return this;
  }

  @Override
  @NonNull
  public FieldReference source(@NonNull ISource source) {
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
  public FieldReference flags(@Nullable List<IFlagBuilder> flags) {
    // References don't support flags - they reference existing definitions
    throw new UnsupportedOperationException("Field references cannot have flags");
  }

  /**
   * This method should not be called directly. Field references are resolved
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
        "Field references must be resolved during module construction. "
            + "Use ModuleBuilder to build the module, which will resolve references.");
  }
}
