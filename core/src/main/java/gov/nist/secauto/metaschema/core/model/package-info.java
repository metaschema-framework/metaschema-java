/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Core Metaschema model interfaces and implementations.
 * <p>
 * This package defines the fundamental object model for Metaschema modules,
 * including definitions (assemblies, fields, flags), instances, and their
 * relationships. It provides both the API contracts and abstract base
 * implementations for representing Metaschema structures.
 * <h2>Key Interfaces</h2>
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.model.IModule} - Represents a
 * Metaschema module containing definitions</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.IAssemblyDefinition} -
 * Defines a complex assembly structure</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.IFieldDefinition} - Defines
 * a field with optional value and flags</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.IFlagDefinition} - Defines
 * a flag (simple name-value pair)</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.IModelInstance} -
 * Represents an instance of a definition within a model</li>
 * <li>{@link gov.nist.secauto.metaschema.core.model.IContainerModel} -
 * Represents a container that can hold model instances</li>
 * </ul>
 * <h2>Usage Context</h2>
 * <p>
 * This package is used by:
 * <ul>
 * <li>Metaschema module loaders to construct in-memory representations of
 * modules</li>
 * <li>Databinding implementations to map between Java objects and Metaschema
 * structures</li>
 * <li>Code generators to produce Java classes from Metaschema definitions</li>
 * <li>Validation and constraint processing to enforce structural rules</li>
 * </ul>
 *
 * @see gov.nist.secauto.metaschema.core.model.constraint
 * @see gov.nist.secauto.metaschema.core.model.validation
 */

package gov.nist.secauto.metaschema.core.model;
