/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.DocumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class FnDocumentAvailable {
  private static final String NAME = "doc-available";
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name(NAME)
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .deterministic()
      .contextDependent()
      .focusIndependent()
      .argument(IArgument.builder()
          .name("arg1")
          .type(IStringItem.class)
          .zeroOrOne()
          .build())
      .returnType(IBooleanItem.class)
      .returnOne()
      .functionHandler(FnDocumentAvailable::execute)
      .build();

  private FnDocumentAvailable() {
    // disable construction
  }

  @SuppressWarnings("unused")
  @NonNull
  private static ISequence<IBooleanItem> execute(@NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments, @NonNull DynamicContext dynamicContext,
      IItem focus) {
    ISequence<? extends IStringItem> arg = FunctionUtils.asType(ObjectUtils.requireNonNull(arguments.get(0)));

    IStringItem item = arg.getFirstItem(true);

    return item == null ? ISequence.empty() : ISequence.of(fnDocAvailable(item, dynamicContext));
  }

  /**
   * Test if the document associated with the URI is retrievable.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-doc-available">fn:doc</a>
   * function.
   *
   * @param documentUri
   *          the resource to load the data from
   * @param context
   *          the Metapath dynamic context
   * @return if the document is retrievable
   */
  public static IBooleanItem fnDocAvailable(@NonNull IStringItem documentUri, @NonNull DynamicContext context) {
    IAnyUriItem uri;
    try {
      uri = IAnyUriItem.cast(documentUri);
    } catch (MetapathException ex) {
      throw new DocumentFunctionException(DocumentFunctionException.INVALID_ARGUMENT,
          String.format("Invalid URI argument '%s' to fn:doc or fn:doc-available.", documentUri.asString()), ex);
    }

    return fnDocAvailable(uri, context);
  }

  /**
   * Test if the document associated with the URI is retrievable.
   * <p>
   * Based on the XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-doc-available">fn:doc</a>
   * function.
   *
   * @param documentUri
   *          the resource to load the data from
   * @param context
   *          the Metapath dynamic context
   * @return if the document is retrievable
   */
  public static IBooleanItem fnDocAvailable(@NonNull IAnyUriItem documentUri, @NonNull DynamicContext context) {
    // resolve if possible
    IAnyUriItem uri = FnResolveUri.fnResolveUri(documentUri, null, context);
    if (!uri.isAbsolute() && !uri.isOpaque()) {
      throw new DocumentFunctionException(DocumentFunctionException.ERROR_RETRIEVING_RESOURCE, String
          .format("No base-uri is available in the static context to resolve the URI '%s'.", documentUri.toString()));
    }

    boolean retval;
    try {
      URLConnection connection = uri.asUri().toURL().openConnection();

      if (connection instanceof HttpURLConnection) {
        HttpURLConnection httpConnection = (HttpURLConnection) connection;
        httpConnection.setRequestMethod("HEAD");
        httpConnection.connect();
        retval = HttpURLConnection.HTTP_OK == httpConnection.getResponseCode();
        httpConnection.disconnect();
      } else {
        connection.connect();
        retval = true;
      }
      return IBooleanItem.valueOf(retval);
    } catch (IOException ex) {
      throw new DocumentFunctionException(DocumentFunctionException.ERROR_RETRIEVING_RESOURCE, String
          .format("Unable to retrieve the resource identified by the URI '%s'.", documentUri.toString()), ex);
    }
  }
}
