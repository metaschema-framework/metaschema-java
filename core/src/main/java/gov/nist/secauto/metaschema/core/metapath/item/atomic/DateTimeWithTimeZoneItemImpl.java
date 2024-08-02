/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.DateTimeWithTZAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

class DateTimeWithTimeZoneItemImpl
    extends AbstractDateTimeItem<ZonedDateTime> {

  public DateTimeWithTimeZoneItemImpl(@NonNull ZonedDateTime value) {
    super(value);
  }

  @Override
  public ZonedDateTime asZonedDateTime() {
    return getValue();
  }

  @Override
  public DateTimeWithTZAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.DATE_TIME_WITH_TZ;
  }
}
