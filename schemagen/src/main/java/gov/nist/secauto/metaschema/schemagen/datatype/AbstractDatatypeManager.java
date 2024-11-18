/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.datatype;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractDatatypeManager implements IDatatypeManager {
  @NonNull
  private static final Map<String, String> DATATYPE_TRANSLATION_MAP // NOPMD - intentional
      = new LinkedHashMap<>();

  static {
    DATATYPE_TRANSLATION_MAP.put("base64", "Base64Datatype");
    DATATYPE_TRANSLATION_MAP.put("boolean", "BooleanDatatype");
    DATATYPE_TRANSLATION_MAP.put("date", "DateDatatype");
    DATATYPE_TRANSLATION_MAP.put("date-with-timezone", "DateWithTimezoneDatatype");
    DATATYPE_TRANSLATION_MAP.put("date-time", "DateTimeDatatype");
    DATATYPE_TRANSLATION_MAP.put("date-time-with-timezone", "DateTimeWithTimezoneDatatype");
    DATATYPE_TRANSLATION_MAP.put("day-time-duration", "DayTimeDurationDatatype");
    DATATYPE_TRANSLATION_MAP.put("decimal", "DecimalDatatype");
    DATATYPE_TRANSLATION_MAP.put("email-address", "EmailAddressDatatype");
    DATATYPE_TRANSLATION_MAP.put("hostname", "HostnameDatatype");
    DATATYPE_TRANSLATION_MAP.put("integer", "IntegerDatatype");
    DATATYPE_TRANSLATION_MAP.put("ip-v4-address", "IPV4AddressDatatype");
    DATATYPE_TRANSLATION_MAP.put("ip-v6-address", "IPV6AddressDatatype");
    DATATYPE_TRANSLATION_MAP.put("markup-line", "MarkupLineDatatype");
    DATATYPE_TRANSLATION_MAP.put("markup-multiline", "MarkupMultilineDatatype");
    DATATYPE_TRANSLATION_MAP.put("non-negative-integer", "NonNegativeIntegerDatatype");
    DATATYPE_TRANSLATION_MAP.put("positive-integer", "PositiveIntegerDatatype");
    DATATYPE_TRANSLATION_MAP.put("string", "StringDatatype");
    DATATYPE_TRANSLATION_MAP.put("token", "TokenDatatype");
    DATATYPE_TRANSLATION_MAP.put("uri", "URIDatatype");
    DATATYPE_TRANSLATION_MAP.put("uri-reference", "URIReferenceDatatype");
    DATATYPE_TRANSLATION_MAP.put("uuid", "UUIDDatatype");
    DATATYPE_TRANSLATION_MAP.put("year-month-duration", "YearMonthDurationDatatype");
  }

  @NonNull
  private final Map<IDataTypeAdapter<?>, String> datatypeToTypeMap = new ConcurrentHashMap<>(); // NOPMD - intentional

  @SuppressWarnings("null")
  @NonNull
  protected static Map<String, String> getDatatypeTranslationMap() {
    return Collections.unmodifiableMap(DATATYPE_TRANSLATION_MAP);
  }

  @Override
  public Set<String> getUsedTypes() {
    return new HashSet<>(datatypeToTypeMap.values());
  }

  @SuppressWarnings("null")
  @Override
  @NonNull
  public String getTypeNameForDatatype(@NonNull IDataTypeAdapter<?> datatype) {
    return datatypeToTypeMap.computeIfAbsent(
        datatype,
        key -> getDatatypeTranslationMap().get(key.getPreferredName().getLocalName()));
  }
}
