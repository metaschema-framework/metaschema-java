/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Support for parsing Metaschema module-based data using Java class bindings.
 * <p>
 * Two methods are supported
 * <ol>
 * <li>A Java annotation based approach
 * ({@link gov.nist.secauto.metaschema.databind.model}) using annotations
 * ({@link gov.nist.secauto.metaschema.databind.model.annotations}). The
 * {@link gov.nist.secauto.metaschema.databind.DefaultBindingContext} is used to
 * load a bound Java class.</li>
 * <li>A metaschema-specific binding, based on the first method, that is capable
 * of representing a Metaschema module
 * ({@link gov.nist.secauto.metaschema.databind.model.metaschema.binding}). The
 * {@link gov.nist.secauto.metaschema.databind.model.metaschema.BindingConstraintLoader}
 * can be used to load any Metaschema module using this method. Once loaded, the
 * module can be registered with the binding context.
 * </ol>
 */

package gov.nist.secauto.metaschema.databind;
