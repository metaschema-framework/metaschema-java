/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathConstants;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.IArgument;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a new Metapath function that decodes provided encoded text using
 * base64.
 *
 */
public final class MpBase64Decode {
  private static final String NAME = "base64-decode-text";

  @NonNull
  static final IFunction SIGNATURE_ONE_ARG = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS_EXTENDED)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("encodedText")
          .type(IBase64BinaryItem.type())
          .one()
          .build())
      .returnType(IStringItem.type())
      .returnOne()
      .functionHandler(MpBase64Decode::executeOneArg)
      .build();

  private MpBase64Decode() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IStringItem> executeOneArg(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IBase64BinaryItem encodedText
        = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0).getFirstItem(true)));
    return ISequence.of(encodedText.decodeAsString());
  }
}
