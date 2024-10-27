/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractKeyConstraintBuilder<
    T extends AbstractKeyConstraintBuilder<T, R>,
    R extends IKeyConstraint>
    extends AbstractConfigurableMessageConstraintBuilder<T, R> {
  @NonNull
  private final List<IKeyField> keyFields = new LinkedList<>();

  /**
   * Add a key field to the list of key fields.
   *
   * @param keyField
   *          the key field to add
   * @return this builder
   */
  @NonNull
  public T keyField(@NonNull IKeyField keyField) {
    this.keyFields.add(keyField);
    return getThis();
  }

  /**
   * Get the list of key fields set on this builder.
   *
   * @return the list of key fields
   */
  @NonNull
  protected List<IKeyField> getKeyFields() {
    return keyFields;
  }

  @Override
  protected void validate() {
    super.validate();

    CollectionUtil.requireNonEmpty(getKeyFields());
  }
}
