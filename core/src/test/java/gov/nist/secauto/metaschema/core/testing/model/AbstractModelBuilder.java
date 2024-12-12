/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A base class for Metaschema module-based model builders for model elements
 * that support flag children.
 *
 * @param <T>
 *          the Java type of this builder
 */
abstract class AbstractModelBuilder<T extends IModelBuilder<T>>
    extends AbstractMetaschemaBuilder<T> {
  private List<IFlagBuilder> flags = CollectionUtil.emptyList();

  @Override
  public T reset() {
    super.reset();
    this.flags = CollectionUtil.emptyList();
    return ObjectUtils.asType(this);
  }

  /**
   * Get the configured flags.
   *
   * @return the list of flag builders
   */
  protected List<IFlagBuilder> getFlags() {
    return flags;
  }

  /**
   * Use the provided flag instances for definitions produced by this builder.
   *
   * @param flags
   *          the flags to use
   * @return this builder
   */
  public T flags(@Nullable List<IFlagBuilder> flags) {
    this.flags = flags == null ? CollectionUtil.emptyList() : flags;
    return ObjectUtils.asType(this);
  }
}
