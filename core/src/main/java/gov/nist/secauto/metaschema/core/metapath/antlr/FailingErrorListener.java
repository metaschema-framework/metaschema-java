/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.antlr;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * An ANTLR error listener that throws a {@link ParseCancellationException} when
 * a syntax error is found.
 * <p>
 * The exception message contains details around the line and character position
 * where the error occurred.
 */
public class FailingErrorListener
    extends BaseErrorListener {
  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
      String msg, RecognitionException ex) {
    throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
  }
}
