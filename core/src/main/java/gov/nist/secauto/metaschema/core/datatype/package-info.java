/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * The Metaschema and Metapath data type system API.
 * <p>
 * This package provides the core framework for the Metaschema type system,
 * enabling type definitions, conversions, and serialization across XML, JSON,
 * and YAML formats. The type system supports both built-in Metaschema types and
 * custom type extensions.
 * <h2>Key Interfaces</h2>
 * <ul>
 * <li>{@link IDataTypeAdapter} - Defines conversion operations between Java
 * types and Metaschema data representations, including parsing, serialization,
 * and validation.</li>
 * <li>{@link IDataTypeProvider} - Service provider interface for registering
 * data type adapters and abstract types, supporting pluggable type
 * extensions.</li>
 * <li>{@link ICustomJavaDataType} - Marker interface for custom Java types that
 * require specialized handling beyond simple primitives.</li>
 * </ul>
 * <h2>Key Classes</h2>
 * <ul>
 * <li>{@link DataTypeService} - Central registry for data type discovery using
 * Java SPI, managing all available type adapters.</li>
 * <li>{@link AbstractDataTypeAdapter} - Base implementation providing common
 * adapter functionality for concrete type implementations.</li>
 * <li>{@link AbstractDataTypeProvider} - Base implementation for type providers
 * with support for registering multiple adapters.</li>
 * </ul>
 * <h2>Usage Context</h2>
 * <p>
 * The data type system is used throughout the Metaschema framework for:
 * <ul>
 * <li>Parsing and validating field values in Metaschema documents</li>
 * <li>Converting between Java objects and XML/JSON/YAML representations</li>
 * <li>Supporting Metapath expressions with typed atomic values</li>
 * <li>Enabling custom type extensions through the SPI mechanism</li>
 * </ul>
 * <p>
 * Concrete type implementations are provided in the {@code adapter} subpackage,
 * while specialized types for markup and temporal values are in their
 * respective subpackages.
 *
 * @see gov.nist.secauto.metaschema.core.datatype.adapter
 * @see gov.nist.secauto.metaschema.core.datatype.markup
 * @see gov.nist.secauto.metaschema.core.datatype.object
 */

package gov.nist.secauto.metaschema.core.datatype;
