/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.constraint.ISource;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements a
 * {@link gov.nist.secauto.metaschema.core.model.constraint.ISource.SourceType#MODEL}
 * source with no associated resource.
 */
public final class InternalModelSource implements ISource {
  @NonNull
  private static final Map<IModule, InternalModelSource> sources = new HashMap<>(); // NOPMD - intentional
  @NonNull
  private static final Lock SOURCE_LOCK = new ReentrantLock();
  @NonNull
  private final IModule module;

  /**
   * Get a new instance of an external source associated with a resource
   * {@code location}.
   *
   * @param module
   *          the Metaschema module containing a constraint
   * @return the source
   */
  @NonNull
  public static ISource instance(@NonNull IModule module) {
    SOURCE_LOCK.lock();
    try {
      return ObjectUtils.notNull(sources.computeIfAbsent(
          module,
          uri -> new InternalModelSource(module)));
    } finally {
      SOURCE_LOCK.unlock();
    }
  }

  private InternalModelSource(@NonNull IModule module) {
    this.module = module;
  }

  @Override
  public SourceType getSourceType() {
    return SourceType.MODEL;
  }

  @Override
  public URI getSource() {
    return module.getLocation();
  }

  @Override
  public String toString() {
    return "internal:" + getSource();
  }

  @Override
  public StaticContext getStaticContext() {
    return module.getModuleStaticContext();
  }
}
