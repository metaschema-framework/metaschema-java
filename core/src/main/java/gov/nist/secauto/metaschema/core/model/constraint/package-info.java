/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Metaschema constraint definitions and validation framework.
 * <p>
 * This package provides the constraint system for Metaschema, which allows
 * defining and enforcing validation rules on model instances. Constraints can
 * restrict allowed values, enforce cardinality, ensure uniqueness, validate
 * patterns, and establish referential integrity through index relationships.
 * <h2>Constraint Types</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint}
 * - Restricts values to a defined set</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.ICardinalityConstraint}
 * - Enforces occurrence requirements (min/max)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.IExpectConstraint}
 * - Validates that a condition is true</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.IMatchesConstraint}
 * - Validates values against regex patterns</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.IUniqueConstraint}
 * - Ensures uniqueness of key field combinations</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.IIndexConstraint}
 * - Creates an index over items for referential integrity</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.constraint.IIndexHasKeyConstraint}
 * - Verifies that references exist in an index</li>
 * </ul>
 * <h2>Validation</h2>
 * <p>
 * The
 * {@link gov.nist.secauto.metaschema.core.model.constraint.DefaultConstraintValidator}
 * provides the main entry point for validating model instances against their
 * constraints. Validation results are reported through
 * {@link gov.nist.secauto.metaschema.core.model.constraint.IConstraintValidationHandler}
 * implementations.
 * <h2>Usage Context</h2>
 * <p>
 * Constraints can be:
 * <ul>
 * <li>Defined inline within Metaschema definitions</li>
 * <li>Applied externally through constraint sets</li>
 * <li>Evaluated during content validation and data binding</li>
 * <li>Used to generate validation rules for JSON Schema and XML Schema</li>
 * </ul>
 *
 * @see gov.nist.secauto.metaschema.core.model.constraint.impl
 */

package gov.nist.secauto.metaschema.core.model.constraint;
