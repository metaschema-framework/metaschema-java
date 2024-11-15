/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUuidItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#uuid">uuid</a>
 * data type.
 */
public class UuidAdapter
    extends AbstractDataTypeAdapter<UUID, IUuidItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "uuid")));

  /**
   * A regular expression that matches a valid UUID.
   */
  public static final Pattern UUID_PATTERN
      = Pattern.compile("^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[45][0-9A-Fa-f]{3}-[89ABab][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}$");

  UuidAdapter() {
    super(UUID.class);
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @SuppressWarnings("null")
  @Override
  public UUID parse(String value) {
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException(
          String.format("Value '%s' is not a valid UUID.", value),
          ex);
    }
  }

  @Override
  public UUID copy(Object obj) {
    // a UUID is immutable
    return (UUID) obj;
  }

  @Override
  public Class<IUuidItem> getItemClass() {
    return IUuidItem.class;
  }

  @Override
  public IUuidItem newItem(Object value) {
    UUID item = toValue(value);
    return IUuidItem.valueOf(item);
  }
}
