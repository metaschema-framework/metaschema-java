/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides core functionality for reading and writing Metaschema instance data
 * to and from bound Java objects.
 * <p>
 * This package contains the serialization and deserialization infrastructure
 * for Metaschema-based data binding, including:
 * <ul>
 * <li>Abstract base classes for serializers and deserializers</li>
 * <li>Configuration features for controlling serialization behavior</li>
 * <li>Format detection and model detection utilities</li>
 * <li>Problem handling interfaces for customizing error recovery</li>
 * </ul>
 *
 * @see dev.metaschema.databind.io.ISerializer
 * @see dev.metaschema.databind.io.IDeserializer
 * @see dev.metaschema.databind.io.IBoundLoader
 * @see dev.metaschema.databind.io.Format
 */

package dev.metaschema.databind.io;
