/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import dev.metaschema.core.model.IModelElement;

/**
 * A Metaschema model element bound to Java data.
 * <p>
 * This interface extends the core model element interface to provide access to
 * the containing bound module.
 */
public interface IBoundModelElement extends IModelElement {

  @Override
  IBoundModule getContainingModule();
}
