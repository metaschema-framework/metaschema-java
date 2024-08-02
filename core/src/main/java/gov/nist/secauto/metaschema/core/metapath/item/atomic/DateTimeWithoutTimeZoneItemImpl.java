/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.DateTimeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.DateTime;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

class DateTimeWithoutTimeZoneItemImpl
    extends AbstractDateTimeItem<DateTime> {

  public DateTimeWithoutTimeZoneItemImpl(@NonNull DateTime value) {
    super(value);
  }

  @Override
  public ZonedDateTime asZonedDateTime() {
    return getValue().getValue();
  }

  @Override
  public DateTimeAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.DATE_TIME;
  }

}
