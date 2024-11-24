/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * FOTY: Exceptions related to type errors.
 */
public class InvalidTypeFunctionException
    extends AbstractCodedMetapathException {
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOTY0012">err:FOTY0012</a>:
   * Raised by
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-data">fn:data</a>, or
   * by implicit atomization, if applied to a node with no typed value, the main
   * example being an element validated against a complex type that defines it to
   * have element-only content.
   */
  public static final int NODE_HAS_NO_TYPED_VALUE = 12;

  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOTY0013">err:FOTY0013</a>:
   * Raised by
   * <a href="https://www.w3.org/TR/xpath-functions-31/#func-data">fn:data</a>, or
   * by implicit atomization, if the sequence to be atomized contains a function
   * item.
   */
  public static final int DATA_ITEM_IS_FUNCTION = 13;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOTY0014">err:FOTY0014</a>:
   * Raised by fn:string, or by implicit string conversion, if the input sequence
   * contains a function item.
   */
  public static final int ARGUMENT_TO_STRING_IS_FUNCTION = 14;

  /**
   * the serial version UUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the provided {@code code}, {@code item}, and
   * no cause.
   *
   * @param code
   *          the error code value
   * @param item
   *          the item the exception applies to
   */
  public InvalidTypeFunctionException(int code, @NonNull IItem item) {
    super(code, generateMessage(item));
  }

  /**
   * Constructs a new exception with the provided {@code code}, {@code item}, and
   * {@code cause}.
   *
   * @param code
   *          the error code value
   * @param item
   *          the item the exception applies to
   * @param cause
   *          the original exception cause
   */
  public InvalidTypeFunctionException(int code, @NonNull IItem item, Throwable cause) {
    super(code, generateMessage(item), cause);
  }

  private static String generateMessage(@NonNull IItem item) {
    String retval;
    if (item instanceof INodeItem) {
      INodeItem nodeItem = (INodeItem) item;
      retval = String.format("The %s node item at path '%s' has no typed value",
          nodeItem.getNodeItemKind().name().toLowerCase(Locale.ROOT),
          nodeItem.getMetapath());
    } else {
      retval = String.format("Item '%s' has an improperly typed value", item.getClass().getName());
    }
    return retval;
  }

  @Override
  public String getCodePrefix() {
    return "FOTY";
  }
}
