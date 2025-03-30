/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.CastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IQNameItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * /** Implements <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-function-lookup">fn:function-lookup</a>
 * functions.
 */
public final class FnQName {
  @NonNull
  private static final String NAME = "QName";

  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextIndependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("paramURI")
          .type(IStringItem.type())
          .one()
          .build())
      .argument(IArgument.builder()
          .name("paramQName")
          .type(IStringItem.type())
          .zeroOrOne()
          .build())
      .returnType(IQNameItem.type())
      .returnOne()
      .functionHandler(FnQName::execute)
      .build();

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IQNameItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    IStringItem paramUri = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0).getFirstItem(true)));
    IStringItem paramQName = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(1).getFirstItem(true)));
    
    if (paramUri.asString() == "") {
	      throw new CastFunctionException(
	              CastFunctionException.INVALID_LEXICAL_VALUE,
	              null,
	              String.format("paramURI is an empty string and not a valid URI to form a QName."));		
	}
    
    try {
        URI uri = URI.create(paramUri.asString());
	} catch (IllegalArgumentException ex) {
	      throw new CastFunctionException(
	              CastFunctionException.INVALID_LEXICAL_VALUE,
	              paramUri,
	              String.format("paramURI '%s' is not a valid URI to form a QName.", paramUri.asString()),
	              ex);
	}
    
    try {
        return ISequence.of(IQNameItem.valueOf(IEnhancedQName.of(paramUri.asString(), paramQName.asString())));
	} catch (Exception ex) {
	      throw new CastFunctionException(
	              CastFunctionException.INVALID_LEXICAL_VALUE,
	              paramUri,
	              String.format("paramQName '%s' is not a valid URI to form QName.", paramQName.asString()),
	              ex);
	}
  }

  private FnQName() {
    // disable construction
  }
}
