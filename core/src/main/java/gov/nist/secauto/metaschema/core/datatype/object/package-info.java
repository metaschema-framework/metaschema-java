/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Specialized data objects for ambiguous temporal values in the Metaschema type
 * system.
 * <p>
 * This package provides custom Java types for representing temporal values
 * (dates, times, and datetimes) that may or may not include timezone
 * information. These ambiguous temporal types preserve the original timezone
 * presence/absence from parsed data, ensuring round-trip fidelity when
 * serializing back to XML or JSON.
 * <h2>Ambiguous Temporal Types</h2>
 * <ul>
 * <li>{@link AmbiguousDate} - Represents a date value that may lack timezone
 * information</li>
 * <li>{@link AmbiguousTime} - Represents a time value that may lack timezone
 * information</li>
 * <li>{@link AmbiguousDateTime} - Represents a datetime value that may lack
 * timezone information</li>
 * <li>{@link AbstractAmbiguousTemporal} - Base class providing common
 * functionality for ambiguous temporal values</li>
 * </ul>
 * <h2>Usage Context</h2>
 * <p>
 * These types are used by the temporal data type adapters in
 * {@link gov.nist.secauto.metaschema.core.datatype.adapter} when parsing dates,
 * times, and datetimes from Metaschema documents. The ambiguous temporal types:
 * <ul>
 * <li>Preserve whether the original value included a timezone</li>
 * <li>Store the underlying temporal value as a {@link java.time.ZonedDateTime}
 * or similar</li>
 * <li>Support comparison operations while respecting timezone ambiguity</li>
 * <li>Enable accurate serialization that matches the original input format</li>
 * </ul>
 * <p>
 * This approach ensures that Metaschema documents with mixed
 * timezone/non-timezone temporal values maintain their original semantics
 * through parse-serialize cycles.
 */

package gov.nist.secauto.metaschema.core.datatype.object;
