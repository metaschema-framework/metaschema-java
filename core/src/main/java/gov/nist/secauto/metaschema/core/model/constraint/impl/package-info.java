/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Default implementations of Metaschema constraint types.
 * <p>
 * This package contains the concrete implementations of the constraint
 * interfaces defined in the parent
 * {@link gov.nist.secauto.metaschema.core.model.constraint} package. These
 * implementations provide the runtime behavior for constraint evaluation and
 * validation.
 * <h2>Key Classes</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultAllowedValuesConstraint}
 * - Implementation of allowed values restrictions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultCardinalityConstraint}
 * - Implementation of cardinality enforcement</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultExpectConstraint}
 * - Implementation of expectation validation</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultMatchesConstraint}
 * - Implementation of pattern matching</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultUniqueConstraint}
 * - Implementation of uniqueness checking</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultIndexConstraint}
 * - Implementation of index creation</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultIndexHasKeyConstraint}
 * - Implementation of referential integrity checking</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.impl.AbstractConstraint}
 * - Base class providing common constraint functionality</li>
 * </ul>
 * <h2>Usage Context</h2>
 * <p>
 * These implementations are used internally by:
 * <ul>
 * <li>Metaschema module loaders when parsing constraint definitions</li>
 * <li>The constraint validator during validation operations</li>
 * <li>Builder classes for programmatic constraint construction</li>
 * </ul>
 * <p>
 * External code should generally interact with constraints through the
 * interface types in the parent package rather than directly using these
 * implementation classes.
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;
