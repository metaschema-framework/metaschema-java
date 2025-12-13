/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Default implementations of Metaschema model container support interfaces.
 * <p>
 * This package provides reusable implementations for managing containers of
 * flags, model instances, and choice groups within Metaschema definitions.
 * These implementations are used by higher-level model implementations to
 * handle the common patterns of containing and organizing child elements.
 * <h2>Key Classes</h2>
 * <ul>
 * <li>{@link DefaultContainerFlagSupport} - Manages a collection of flag
 * instances with optional JSON key flag support</li>
 * <li>{@link DefaultContainerModelSupport} - Provides generic container support
 * for model instances (fields and assemblies) with lookup by type and name</li>
 * <li>{@link DefaultContainerModelAssemblySupport} - Specialized container
 * support for assembly instances</li>
 * <li>{@link DefaultContainerModelChoiceGroupSupport} - Container support for
 * choice group model instances</li>
 * <li>{@link EmptyFlagContainer} - Singleton implementation for definitions
 * with no flag instances</li>
 * </ul>
 * <h2>Design Pattern</h2>
 * <p>
 * These classes follow a delegation pattern where Metaschema definition
 * implementations (such as assemblies and fields) delegate container management
 * responsibilities to these specialized support classes. This promotes code
 * reuse and consistent behavior across different definition types.
 * <p>
 * The implementations use {@link java.util.LinkedHashMap} internally to
 * preserve insertion order, which is important for maintaining the declaration
 * order of model elements as they appear in Metaschema modules.
 * <h2>Thread Safety</h2>
 * <p>
 * These container implementations are intended to be immutable once
 * constructed. They are typically initialized during module loading and then
 * used in a read-only manner throughout the lifecycle of the model.
 * <h2>Usage Context</h2>
 * <p>
 * This package is used internally by:
 * <ul>
 * <li>Metaschema module loaders when constructing definition objects</li>
 * <li>Definition implementations in the parent
 * {@link gov.nist.secauto.metaschema.core.model} package</li>
 * <li>Databinding implementations that need to navigate model structures</li>
 * </ul>
 *
 * @see gov.nist.secauto.metaschema.core.model.IContainerFlagSupport
 * @see gov.nist.secauto.metaschema.core.model.IContainerModelSupport
 * @see gov.nist.secauto.metaschema.core.model.AbstractContainerModelSupport
 */

package gov.nist.secauto.metaschema.core.model.impl;
