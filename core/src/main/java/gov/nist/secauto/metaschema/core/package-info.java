/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Core support for handling Metaschema-based models.
 * <p>
 * This package provides the foundation for working with Metaschema definitions
 * and data. Metaschema is a framework for defining structured data models with
 * rich validation constraints and multiple serialization formats (XML, JSON,
 * YAML).
 * <p>
 * The core module is organized into several key subpackages:
 * <ul>
 * <li>{@code model} - Metaschema model interfaces ({@code IModule},
 * {@code IAssemblyDefinition}, {@code IFieldDefinition},
 * {@code IFlagDefinition})</li>
 * <li>{@code metapath} - Metapath expression language (XPath 3.1 implementation
 * for Metaschema)</li>
 * <li>{@code datatype} - Data type adapters for Metaschema types (string,
 * integer, date-time, etc.)</li>
 * <li>{@code mdm} - Metaschema Document Model node items</li>
 * <li>{@code configuration} - Configuration management for processors and
 * parsers</li>
 * <li>{@code util} - Common utility classes</li>
 * </ul>
 * <p>
 * Key class in this package:
 * <ul>
 * <li>{@link gov.nist.secauto.metaschema.core.MetaschemaConstants} - Metaschema
 * namespace and related constants</li>
 * </ul>
 *
 * @see <a href="https://pages.nist.gov/metaschema/">Metaschema Project</a>
 */

package gov.nist.secauto.metaschema.core;
