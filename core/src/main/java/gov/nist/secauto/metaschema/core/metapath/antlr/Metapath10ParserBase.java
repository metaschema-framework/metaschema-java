/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.antlr;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

import java.util.Set;

/**
 * Base class for Metapath ANTLR-based parsing operations. This class extends
 * the ANTLR Parser to provide specialized processing for Metapath expressions.
 * <p>
 * ANTLR (ANother Tool for Language Recognition) is used here to generate the
 * parser for Metapath expressions. This base class provides common
 * functionality that can be used by the generated parser implementation.
 * <p>
 * Implementations should extend this class to define specific parsing rules and
 * behaviors for Metapath processing.
 */
public abstract class Metapath10ParserBase
    extends Parser {

  private static final Set<Integer> KEYWORD_TOKENS = Set.of(
      Metapath10.KW_ARRAY,
      Metapath10.KW_FLAG,
      Metapath10.KW_COMMENT,
      Metapath10.KW_DOCUMENT_NODE,
      Metapath10.KW_FIELD,
      Metapath10.KW_ASSEMBLY,
      Metapath10.KW_EMPTY_SEQUENCE,
      Metapath10.KW_FUNCTION,
      Metapath10.KW_IF,
      Metapath10.KW_ITEM,
      Metapath10.KW_MAP,
      Metapath10.KW_NAMESPACE_NODE,
      Metapath10.KW_NODE,
      Metapath10.KW_PROCESSING_INSTRUCTION,
      Metapath10.KW_SCHEMA_ATTRIBUTE,
      Metapath10.KW_SCHEMA_ELEMENT,
      Metapath10.KW_TEXT);

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
    int nextToken = getInputStream().LA(1);
    return !KEYWORD_TOKENS.contains(nextToken);
  }
}
