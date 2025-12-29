/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides schema generation capabilities for Metaschema modules.
 * <p>
 * This package contains the core API and implementations for generating XML
 * Schema (XSD) and JSON Schema from Metaschema module definitions. The main
 * entry point is
 * {@link gov.nist.secauto.metaschema.schemagen.ISchemaGenerator}, which
 * provides methods to generate schemas in different formats.
 * <p>
 * Key classes:
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.schemagen.ISchemaGenerator} - Main
 * interface for schema generation</li>
 * <li>{@link gov.nist.secauto.metaschema.schemagen.IGenerationState} - Manages
 * state during schema generation</li>
 * <li>{@link gov.nist.secauto.metaschema.schemagen.IInlineStrategy} - Controls
 * definition inlining behavior</li>
 * <li>{@link gov.nist.secauto.metaschema.schemagen.ModuleIndex} - Indexes
 * definitions across modules</li>
 * </ul>
 */

package gov.nist.secauto.metaschema.schemagen;
