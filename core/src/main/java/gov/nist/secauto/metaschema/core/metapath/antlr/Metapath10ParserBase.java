/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.antlr;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

public abstract class Metapath10ParserBase
    extends Parser {
  /**
   * Construct a new parser base.
   *
   * @param input
   *          the input token stream
   */
  protected Metapath10ParserBase(TokenStream input) {
    super(input);
  }

  /**
   * Check if functional call name does not include a keyword.
   *
   * @return {@code true} if the function call name is free of keywords, or
   *         {@code false} otherwise
   */
  protected boolean isFuncCall() {
    return !(getInputStream().LA(1) == Metapath10.KW_ARRAY
        || getInputStream().LA(1) == Metapath10.KW_FLAG
        || getInputStream().LA(1) == Metapath10.KW_COMMENT
        || getInputStream().LA(1) == Metapath10.KW_DOCUMENT_NODE
        || getInputStream().LA(1) == Metapath10.KW_ELEMENT
        || getInputStream().LA(1) == Metapath10.KW_EMPTY_SEQUENCE
        || getInputStream().LA(1) == Metapath10.KW_FUNCTION
        || getInputStream().LA(1) == Metapath10.KW_IF
        || getInputStream().LA(1) == Metapath10.KW_ITEM
        || getInputStream().LA(1) == Metapath10.KW_MAP
        || getInputStream().LA(1) == Metapath10.KW_NAMESPACE_NODE
        || getInputStream().LA(1) == Metapath10.KW_NODE
        || getInputStream().LA(1) == Metapath10.KW_PROCESSING_INSTRUCTION
        || getInputStream().LA(1) == Metapath10.KW_SCHEMA_ATTRIBUTE
        || getInputStream().LA(1) == Metapath10.KW_SCHEMA_ELEMENT
        || getInputStream().LA(1) == Metapath10.KW_TEXT);
  }
}
