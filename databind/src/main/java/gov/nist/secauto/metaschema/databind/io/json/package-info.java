/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides support for reading and writing Metaschema instance data in JSON
 * format.
 * <p>
 * This package contains JSON-specific implementations of the serialization and
 * deserialization interfaces, including:
 * <ul>
 * <li>JSON deserializer for reading JSON into bound objects</li>
 * <li>JSON serializer for writing bound objects to JSON</li>
 * <li>JSON-specific problem handlers for error recovery</li>
 * <li>JSON parsing and writing context interfaces</li>
 * </ul>
 * <p>
 * The JSON implementation uses Jackson for JSON processing.
 *
 * @see gov.nist.secauto.metaschema.databind.io.json.DefaultJsonDeserializer
 * @see gov.nist.secauto.metaschema.databind.io.json.DefaultJsonSerializer
 * @see gov.nist.secauto.metaschema.databind.io.json.IJsonProblemHandler
 */

package gov.nist.secauto.metaschema.databind.io.json;
