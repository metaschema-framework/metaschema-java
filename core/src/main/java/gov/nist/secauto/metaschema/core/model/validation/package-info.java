/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Schema-based content validation for Metaschema instances.
 * <p>
 * This package provides validators for checking Metaschema-based content
 * against generated XML Schema and JSON Schema documents. It supports
 * validating content files to ensure they conform to the structure defined by a
 * Metaschema module.
 * <h2>Key Interfaces and Classes</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.model.validation.IContentValidator}
 * - Common interface for content validators</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.validation.XmlSchemaContentValidator}
 * - Validates XML content against XML Schema (XSD)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.validation.JsonSchemaContentValidator}
 * - Validates JSON content against JSON Schema</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.validation.IValidationResult}
 * - Represents the outcome of a validation operation</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.validation.IValidationFinding}
 * - Represents an individual validation issue or error</li>
 * </ul>
 * <h2>Usage Context</h2>
 * <p>
 * This validation framework differs from constraint validation
 * ({@link gov.nist.secauto.metaschema.core.model.constraint}) in that it:
 * <ul>
 * <li>Validates against schema documents (XSD/JSON Schema) rather than
 * constraint rules</li>
 * <li>Checks structural conformance and data type compliance</li>
 * <li>Is typically used for validating external content files before
 * databinding</li>
 * <li>Provides lower-level validation compared to Metaschema constraint
 * validation</li>
 * </ul>
 * <p>
 * For constraint-based validation (e.g., allowed values, uniqueness,
 * cardinality), use the
 * {@link gov.nist.secauto.metaschema.core.model.constraint} package instead.
 *
 * @see gov.nist.secauto.metaschema.core.model.constraint
 */

package gov.nist.secauto.metaschema.core.model.validation;
