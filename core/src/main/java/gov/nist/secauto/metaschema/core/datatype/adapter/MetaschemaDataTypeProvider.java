/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.google.auto.service.AutoService;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.IDataTypeProvider;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides runtime discovery of built-in implementations of the core Metaschema
 * data types.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
@AutoService(IDataTypeProvider.class)
public final class MetaschemaDataTypeProvider // NOPMD - Used for service initialization
    extends AbstractDataTypeProvider {
  @NonNull
  public static final Base64Adapter BASE64 = new Base64Adapter();
  @NonNull
  public static final BooleanAdapter BOOLEAN = new BooleanAdapter();
  @NonNull
  public static final DateAdapter DATE = new DateAdapter();
  @NonNull
  public static final DateWithTZAdapter DATE_WITH_TZ = new DateWithTZAdapter();
  @NonNull
  public static final DateTimeAdapter DATE_TIME = new DateTimeAdapter();
  @NonNull
  public static final DateTimeWithTZAdapter DATE_TIME_WITH_TZ = new DateTimeWithTZAdapter();
  @NonNull
  public static final IPv4AddressAdapter IP_V4_ADDRESS = new IPv4AddressAdapter();
  @NonNull
  public static final IPv6AddressAdapter IP_V6_ADDRESS = new IPv6AddressAdapter();
  @NonNull
  public static final UriAdapter URI = new UriAdapter();
  @NonNull
  public static final UriReferenceAdapter URI_REFERENCE = new UriReferenceAdapter();
  @NonNull
  public static final UuidAdapter UUID = new UuidAdapter();

  @NonNull
  public static final DayTimeAdapter DAY_TIME_DURATION = new DayTimeAdapter();
  @NonNull
  public static final YearMonthAdapter YEAR_MONTH_DURATION = new YearMonthAdapter();

  @NonNull
  public static final DecimalAdapter DECIMAL = new DecimalAdapter();
  @NonNull
  public static final IntegerAdapter INTEGER = new IntegerAdapter();
  @NonNull
  public static final NonNegativeIntegerAdapter NON_NEGATIVE_INTEGER = new NonNegativeIntegerAdapter();
  @NonNull
  public static final PositiveIntegerAdapter POSITIVE_INTEGER = new PositiveIntegerAdapter();

  @NonNull
  public static final EmailAddressAdapter EMAIL_ADDRESS = new EmailAddressAdapter();
  @NonNull
  public static final HostnameAdapter HOSTNAME = new HostnameAdapter();
  @Deprecated(forRemoval = true, since = "0.7.0")
  @NonNull
  public static final NcNameAdapter NCNAME = new NcNameAdapter();
  @NonNull
  public static final StringAdapter STRING = new StringAdapter();
  @NonNull
  public static final TokenAdapter TOKEN = new TokenAdapter();

  @NonNull
  public static final StringAdapter DEFAULT_DATA_TYPE = STRING;

  /**
   * Initialize the built-in data types.
   */
  public MetaschemaDataTypeProvider() {
    // The data type "string" must be first since this is the default data type for
    // the {@link String}
    // Java type. This ensures that when a data type is resolved that this data type
    // is matched first
    // before other String-based data types.
    registerDatatype(STRING);

    registerDatatype(BASE64);
    registerDatatype(BOOLEAN);
    registerDatatype(DATE);
    registerDatatype(DATE_WITH_TZ);
    registerDatatype(DATE_TIME);
    registerDatatype(DATE_TIME_WITH_TZ);
    registerDatatype(DAY_TIME_DURATION);
    registerDatatype(DECIMAL);
    registerDatatype(EMAIL_ADDRESS);
    registerDatatype(HOSTNAME);
    registerDatatype(INTEGER);
    registerDatatype(IP_V4_ADDRESS);
    registerDatatype(IP_V6_ADDRESS);

    registerDatatype(NON_NEGATIVE_INTEGER);
    registerDatatype(POSITIVE_INTEGER);
    registerDatatype(TOKEN);
    registerDatatype(URI);
    registerDatatype(URI_REFERENCE);
    registerDatatype(UUID);
    registerDatatype(YEAR_MONTH_DURATION);
  }
}
