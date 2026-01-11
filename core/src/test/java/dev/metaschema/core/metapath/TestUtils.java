/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import dev.metaschema.core.datatype.adapter.DecimalAdapter;
import dev.metaschema.core.metapath.item.ICollectionValue;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.atomic.IAnyUriItem;
import dev.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import dev.metaschema.core.metapath.item.atomic.IBooleanItem;
import dev.metaschema.core.metapath.item.atomic.IDateItem;
import dev.metaschema.core.metapath.item.atomic.IDateTimeItem;
import dev.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import dev.metaschema.core.metapath.item.atomic.IDecimalItem;
import dev.metaschema.core.metapath.item.atomic.IDurationItem;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.metapath.item.atomic.IQNameItem;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.item.atomic.ITimeItem;
import dev.metaschema.core.metapath.item.atomic.IUuidItem;
import dev.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import dev.metaschema.core.metapath.item.function.IArrayItem;
import dev.metaschema.core.metapath.item.function.IMapItem;
import dev.metaschema.core.metapath.item.function.IMapKey;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

public final class TestUtils {
  /**
   * Create an empty sequence.
   *
   * @param <T>
   *          the item Java type contained within the sequence
   * @return the sequence
   */
  @NonNull
  public static <T extends IItem> ISequence<T> sequence() {
    return ISequence.of();
  }

  /**
   * Create a sequence containing the provided items.
   *
   * @param <T>
   *          the item Java type contained within the sequence
   * @param items
   *          the items to add to the sequence
   * @return the sequence
   */
  @SafeVarargs
  @NonNull
  public static <T extends IItem> ISequence<T> sequence(@NonNull T... items) {
    return ISequence.of(items);
  }

  /**
   * Create an empty array item.
   *
   * @param <T>
   *          the item Java type contained within the array item
   * @return the array item
   */
  @NonNull
  public static <T extends ICollectionValue> IArrayItem<T> array() {
    return IArrayItem.of();
  }

  /**
   * Create a array item containing the provided items.
   *
   * @param <T>
   *          the item Java type contained within the array item
   * @param items
   *          the items to add to the array item
   * @return the array item
   */
  @SafeVarargs
  @NonNull
  public static <T extends ICollectionValue> IArrayItem<T> array(@NonNull T... items) {
    return IArrayItem.of(items);
  }

  /**
   * Create a map item containing the provided entries.
   *
   * @param <T>
   *          the entry value Java type contained within the map item
   * @param entries
   *          the entries to add to the map item
   * @return the map item
   */
  @SafeVarargs
  @NonNull
  public static <T extends ICollectionValue> IMapItem<T> map(@NonNull Map.Entry<IMapKey, T>... entries) {
    return IMapItem.ofEntries(entries);
  }

  /**
   * Create a map entry using the provided key and value.
   *
   * @param <T>
   *          the entry value Java type
   * @param key
   *          the entry key
   * @param value
   *          the entry value
   * @return the map entry
   */
  @NonNull
  public static <T extends ICollectionValue> Map.Entry<IMapKey, T> entry(
      @NonNull IAnyAtomicItem key,
      @NonNull T value) {
    return IMapItem.entry(key, value);
  }

  /**
   * Create a base64 item using the provided value.
   *
   * @param value
   *          the value
   * @return the boolean item
   */
  @NonNull
  public static IBase64BinaryItem base64(@NonNull String value) {
    return IBase64BinaryItem.valueOf(value);
  }

  /**
   * Create a boolean item using the provided value.
   *
   * @param value
   *          the boolean value
   * @return the boolean item
   */
  @NonNull
  public static IBooleanItem bool(boolean value) {
    return IBooleanItem.valueOf(value);
  }

  /**
   * Create a decimal item using the provided value.
   *
   * @param value
   *          the decimal value
   * @return the decimal item
   */
  public static IDecimalItem decimal(@NonNull String value) {
    return IDecimalItem.valueOf(new BigDecimal(value, DecimalAdapter.mathContext()));
  }

  /**
   * Create a decimal item using the provided value.
   *
   * @param value
   *          the decimal value
   * @return the decimal item
   */
  @NonNull
  public static IDecimalItem decimal(int value) {
    return IDecimalItem.valueOf(value);
  }

  /**
   * Create a decimal item using the provided value.
   *
   * @param value
   *          the decimal value
   * @return the decimal item
   */
  @NonNull
  public static IDecimalItem decimal(double value) {
    return IDecimalItem.valueOf(value);
  }

  /**
   * Create an integer item using the provided value.
   *
   * @param value
   *          the integer value
   * @return the integer item
   */
  @NonNull
  public static IIntegerItem integer(int value) {
    return IIntegerItem.valueOf(ObjectUtils.notNull(BigInteger.valueOf(value)));
  }

  /**
   * Create a string item using the provided value.
   *
   * @param value
   *          the string value
   * @return the string item
   */
  @NonNull
  public static IStringItem string(@NonNull String value) {
    return IStringItem.valueOf(value);
  }

  /**
   * Create a uuid item using the provided value.
   *
   * @param value
   *          the uuid value
   * @return the uuid item
   */
  @NonNull
  public static IUuidItem uuid(@NonNull String value) {
    return IUuidItem.valueOf(value);
  }

  /**
   * Create a uri item using the provided value.
   *
   * @param value
   *          the uri value
   * @return the uri item
   */
  @NonNull
  public static IAnyUriItem uri(@NonNull String value) {
    return IAnyUriItem.valueOf(value);
  }

  /**
   * Create a date item using the provided value.
   *
   * @param value
   *          the date value
   * @return the date item
   */
  @NonNull
  public static IDateItem date(@NonNull String value) {
    return IDateItem.valueOf(value);
  }

  /**
   * Create a date/time item using the provided value.
   *
   * @param value
   *          the date/time value
   * @return the date/time item
   */
  @NonNull
  public static IDateTimeItem dateTime(@NonNull String value) {
    return IDateTimeItem.valueOf(value);
  }

  /**
   * Create a time item using the provided value.
   *
   * @param value
   *          the time value
   * @return the time item
   */
  @NonNull
  public static ITimeItem time(@NonNull String value) {
    return ITimeItem.valueOf(value);
  }

  /**
   * Create a duration item using the provided value indicating the years, months,
   * and days of the duration.
   *
   * @param value
   *          the duration value
   * @return the duration item
   */
  @NonNull
  public static IDurationItem duration(@NonNull String value) {
    return IDurationItem.valueOf(value);
  }

  /**
   * Create a duration item using the provided value indicating the years, months,
   * and days of the duration.
   *
   * @param value
   *          the duration value
   * @return the duration item
   */
  @NonNull
  public static IYearMonthDurationItem yearMonthDuration(@NonNull String value) {
    return IYearMonthDurationItem.valueOf(value);
  }

  /**
   * Create a duration item using the provided value indicating the seconds and
   * nanoseconds of the duration.
   *
   * @param value
   *          the duration value
   * @return the duration item
   */
  @NonNull
  public static IDayTimeDurationItem dayTimeDuration(@NonNull String value) {
    return IDayTimeDurationItem.valueOf(value);
  }

  /**
   * Create an enhanced QName with the given namespace and local name.
   *
   * @param namespace
   *          the namespace URI
   * @param localname
   *          the local name
   * @return the enhanced QName
   */
  @NonNull
  public static IEnhancedQName eqname(@NonNull String namespace, @NonNull String localname) {
    return IEnhancedQName.of(namespace, localname);
  }

  /**
   * Create a QName item with the given namespace and local name.
   *
   * @param namespace
   *          the namespace URI
   * @param localname
   *          the local name
   * @return the QName item
   */
  @NonNull
  public static IQNameItem qname(@NonNull String namespace, @NonNull String localname) {
    return IQNameItem.valueOf(eqname(namespace, localname));
  }

  private TestUtils() {
    // disable construction
  }
}
