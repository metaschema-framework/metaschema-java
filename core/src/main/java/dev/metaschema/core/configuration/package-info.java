/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides a type-safe configuration framework for processors and parsers.
 * <p>
 * This package implements a feature-based configuration system inspired by
 * Jackson's configuration model. Configuration options are defined as
 * strongly-typed features that can be queried and modified through immutable or
 * mutable configuration interfaces.
 * <p>
 * Key interfaces and classes:
 * <ul>
 * <li>{@link dev.metaschema.core.configuration.IConfigurationFeature} - Defines
 * a configuration option with a name, type, and default value</li>
 * <li>{@link dev.metaschema.core.configuration.IConfiguration} - Immutable view
 * of configuration state for querying feature values</li>
 * <li>{@link dev.metaschema.core.configuration.IMutableConfiguration} - Mutable
 * view allowing feature values to be modified</li>
 * <li>{@link dev.metaschema.core.configuration.DefaultConfiguration} - Standard
 * implementation of mutable configuration</li>
 * <li>{@link dev.metaschema.core.configuration.AbstractConfigurationFeature} -
 * Base class for implementing configuration features</li>
 * </ul>
 * <p>
 * Usage example:
 *
 * <pre>
 * // Define a feature
 * IConfigurationFeature&lt;Boolean&gt; PRETTY_PRINT = ...;
 *
 * // Create and configure
 * IMutableConfiguration&lt;MyFeature&gt; config = new DefaultConfiguration&lt;&gt;();
 * config.enableFeature(PRETTY_PRINT);
 *
 * // Query configuration
 * boolean enabled = config.isFeatureEnabled(PRETTY_PRINT);
 * </pre>
 *
 * @see dev.metaschema.core.configuration.IConfigurationFeature
 */

package dev.metaschema.core.configuration;
