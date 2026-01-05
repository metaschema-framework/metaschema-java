/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Provides support for reading and writing Metaschema instance data in XML
 * format.
 * <p>
 * This package contains XML-specific implementations of the serialization and
 * deserialization interfaces, including:
 * <ul>
 * <li>XML deserializer for reading XML into bound objects</li>
 * <li>XML serializer for writing bound objects to XML</li>
 * <li>XML-specific problem handlers for error recovery</li>
 * <li>XML parsing and writing context interfaces</li>
 * </ul>
 * <p>
 * The XML implementation uses StAX (Streaming API for XML) for XML processing,
 * specifically the Woodstox implementation.
 *
 * @see dev.metaschema.databind.io.xml.DefaultXmlDeserializer
 * @see dev.metaschema.databind.io.xml.DefaultXmlSerializer
 * @see dev.metaschema.databind.io.xml.IXmlProblemHandler
 */

package dev.metaschema.databind.io.xml;
