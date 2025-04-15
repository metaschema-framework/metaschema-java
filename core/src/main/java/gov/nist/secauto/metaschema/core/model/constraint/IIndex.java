/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultIndex;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An index that can support the {@link IIndexConstraint},
 * {@link IIndexHasKeyConstraint}, and {@link IUniqueConstraint}.
 */
public interface IIndex {

  /**
   * Construct a new index using the provided key field components to generate
   * keys.
   *
   * @param keyFields
   *          the key field components to use to generate keys by default
   * @return the new index
   */
  @NonNull
  static IIndex newInstance(@NonNull List<? extends IKeyField> keyFields) {
    return new DefaultIndex(keyFields);
  }

  /**
   * Check if a key contains information other than {@code null} Strings.
   *
   * @param key
   *          the key to check
   * @return {@code true} if the series of key values contains only {@code null}
   *         values, or {@code false} otherwise
   */
  static boolean isAllNulls(@NonNull Iterable<String> key) {
    for (String value : key) {
      if (value != null) {
        return false; // NOPMD readability
      }
    }
    return true;
  }

  /**
   * Retrieve the key field components used to generate a key for this index.
   *
   * @return the key field components
   */
  @NonNull
  List<IKeyField> getKeyFields();

  /**
   * Store the provided item using the provided key.
   *
   * @param item
   *          the item to store
   * @param key
   *          the key to store the item with
   * @return the previous item stored in the index using the key, or {@code null}
   *         otherwise
   */
  @Nullable
  INodeItem put(@NonNull INodeItem item, @NonNull List<String> key);

  /**
   * Retrieve the item from the index that matches the provided key.
   *
   * @param key
   *          the key to use for lookup
   * @return the item with the matching key or {@code null} if no matching item
   *         was found
   */
  INodeItem get(List<String> key);

  /**
   * Construct a key by evaluating the provided key field components against the
   * provided item.
   *
   * @param item
   *          the item to generate the key from
   * @param keyFields
   *          the key field components used to generate the key
   * @param dynamicContext
   *          the Metapath evaluation context
   * @return a new key
   * @throws IllegalArgumentException
   *           if a key field has a configured pattern that fails to match the key
   *           item value or if the pattern is malformed
   * @throws MetapathException
   *           if the evaluation of a key field's metapath resulted in an
   *           unexpected error
   */
  @NonNull
  static List<String> toKey(
      @NonNull INodeItem item,
      @NonNull List<? extends IKeyField> keyFields,
      @NonNull DynamicContext dynamicContext) {
    return CollectionUtil.unmodifiableList(
        ObjectUtils.notNull(keyFields.stream()
            .map(keyField -> {
              assert keyField != null;
              return buildKeyItem(item, keyField, dynamicContext);
            })
            .collect(Collectors.toCollection(ArrayList::new))));
  }

  /**
   * Evaluates the provided key field component against the item to generate a key
   * value.
   *
   * @param item
   *          the item to generate the key value from
   * @param keyField
   *          the key field component used to generate the key value
   * @param dynamicContext
   *          the Metapath evaluation context
   * @return the key value or {@code null} if the evaluation resulted in no value
   * @throws IllegalArgumentException
   *           if the key field has a configured pattern that fails to match the
   *           key item value or if the pattern is malformed
   * @throws MetapathException
   *           if the evaluation of the key metapath resulted in an unexpected
   *           error
   */
  @Nullable
  private static String buildKeyItem(
      @NonNull INodeItem item,
      @NonNull IKeyField keyField,
      @NonNull DynamicContext dynamicContext) {
    IMetapathExpression keyMetapath = keyField.getTarget();

    IItem keyItem = keyMetapath.evaluateAs(item, IMetapathExpression.ResultType.ITEM, dynamicContext);

    String keyValue = null;
    if (keyItem != null) {
      keyValue = keyItem.toAtomicItem().asString();
      assert keyValue != null;
      Pattern pattern = keyField.getPattern();
      if (pattern != null) {
        keyValue = applyPattern(keyMetapath, keyValue, pattern);
      }
    } // else empty key
    return keyValue;
  }

  /**
   * Apply the key value pattern, if configured, to generate the final key value.
   * <p>
   * The provided pattern is expected to have a single matching group, which will
   * contain the final key value on match
   *
   * @param keyItem
   *          the node item used to form the key field
   * @param pattern
   *          the key field pattern configuration from the constraint
   * @param keyValue
   *          the current key value
   * @return the final key value
   * @throws IllegalArgumentException
   *           if the provided key value does not match the provided pattern or if
   *           the pattern is malformed
   */
  @Nullable
  private static String applyPattern(
      @NonNull IMetapathExpression keyMetapath,
      @NonNull String keyValue,
      @NonNull Pattern pattern) {
    Matcher matcher = pattern.matcher(keyValue);
    if (!matcher.matches()) {
      // TODO: use a different exception type?
      throw new IllegalArgumentException(
          String.format("Key field declares the pattern '%s' which does not match the value '%s' of node '%s'",
              pattern.pattern(), keyValue, keyMetapath));
    }

    if (matcher.groupCount() != 1) {
      throw new IllegalArgumentException(
          String.format("The first group was not a match for value '%s' of node '%s' for key field pattern '%s'",
              keyValue, keyMetapath, pattern.pattern()));
    }

    String result = matcher.group(1);

    return result == null || result.isEmpty() ? null : result;
  }
}
