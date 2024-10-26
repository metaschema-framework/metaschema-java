/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.constraint.impl.ExternalSource;
import gov.nist.secauto.metaschema.core.model.constraint.impl.InternalModelSource;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A descriptor that identifies where a given constraint was defined.
 */
public interface ISource {
  enum SourceType {
    /**
     * A constraint embedded in a model.
     */
    MODEL,
    /**
     * A constraint defined externally from a model.
     */
    EXTERNAL;
  }

  /**
   * Get the descriptor for a
   * {@link gov.nist.secauto.metaschema.core.model.constraint.ISource.SourceType#MODEL}
   * source with as associated resource.
   *
   * @param module
   *          the Metaschema module the constraint was defined in
   * @return the source descriptor
   */
  @NonNull
  static ISource modelSource(@NonNull IModule module) {
    return InternalModelSource.instance(module);
  }

  /**
   * Get the descriptor for a
   * {@link gov.nist.secauto.metaschema.core.model.constraint.ISource.SourceType#EXTERNAL}
   * source with as associated resource.
   * <p>
   * The provided static context idenfies the location of this source based on the
   * {@link StaticContext#getBaseUri()} method.
   *
   * @param staticContext
   *          the static Metapath context to use for compiling Metapath
   *          expressions in this source
   *
   * @return the source descriptor
   */
  @NonNull
  static ISource externalSource(@NonNull StaticContext staticContext) {
    if (staticContext.getBaseUri() == null) {
      throw new IllegalArgumentException("The static content must define a baseUri identifing the source resource.");
    }
    return ExternalSource.instance(staticContext);
  }

  /**
   * Get the type of source.
   *
   * @return the type
   */
  @NonNull
  ISource.SourceType getSourceType();

  /**
   * Get the resource where the constraint was defined, if known.
   *
   * @return the resource or {@code null} if the resource is not known
   */
  @Nullable
  URI getSource();

  /**
   * Get the static Metapath context to use when compiling Metapath expressions.
   *
   * @return the static Metapath context
   */
  @NonNull
  StaticContext getStaticContext();
}
