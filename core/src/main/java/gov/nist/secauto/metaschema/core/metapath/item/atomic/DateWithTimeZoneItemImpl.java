/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.DateWithTZAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

class DateWithTimeZoneItemImpl
    extends AbstractDateItem<ZonedDateTime> {

  public DateWithTimeZoneItemImpl(@NonNull ZonedDateTime value) {
    super(value);
  }

  @Override
  public ZonedDateTime asZonedDateTime() {
    return getValue();
  }

  @Override
  public DateWithTZAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.DATE_WITH_TZ;
  }
}
