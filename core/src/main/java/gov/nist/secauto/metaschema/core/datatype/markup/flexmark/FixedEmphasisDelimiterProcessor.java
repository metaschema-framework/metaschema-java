/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.markup.flexmark;

import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.parser.core.delimiter.AsteriskDelimiterProcessor;
import com.vladsch.flexmark.parser.core.delimiter.Delimiter;
import com.vladsch.flexmark.parser.core.delimiter.EmphasisDelimiterProcessor;
import com.vladsch.flexmark.parser.delimiter.DelimiterRun;
import com.vladsch.flexmark.util.ast.DelimitedNode;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.flexmark.util.sequence.BasedSequence;

/**
 * Provides a temporary fix for the broken {@link EmphasisDelimiterProcessor} in
 * Flexmark.
 */
public class FixedEmphasisDelimiterProcessor
    extends AsteriskDelimiterProcessor {
  // TODO: remove this class once vsch/flexmark-java#580 is merged
  private final int multipleUse;

  /**
   * Construct a new delimiter processor.
   *
   * @param strongWrapsEmphasis
   *          when {@code true} strong will wrap emphasis, otherwise emphasis will
   *          wrap strong
   */
  public FixedEmphasisDelimiterProcessor(boolean strongWrapsEmphasis) {
    super(strongWrapsEmphasis);
    this.multipleUse = strongWrapsEmphasis ? 1 : 2;
  }

  @SuppressWarnings("PMD.OnlyOneReturn") // for readability
  @Override
  public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
    // "multiple of 3" rule for internal delimiter runs
    if ((opener.canClose() || closer.canOpen()) && (opener.length() + closer.length()) % 3 == 0) {
      if (opener.length() % 3 == 0 && closer.length() % 3 == 0) {
        return this.multipleUse; // if they are each a multiple of 3, then emphasis can be created
      }
      return 0;
    }

    // calculate actual number of delimiters used from this closer
    if (opener.length() < 3 || closer.length() < 3) {
      return Utils.min(closer.length(), opener.length());
    }
    // default to latest spec
    return closer.length() % 2 == 0 ? 2 : multipleUse;
  }

  @Override
  public void process(Delimiter opener, Delimiter closer, int delimitersUsed) {
    DelimitedNode emphasis = delimitersUsed == 1
        ? new Emphasis(opener.getTailChars(delimitersUsed), BasedSequence.NULL, closer.getLeadChars(delimitersUsed))
        : new StrongEmphasis(opener.getTailChars(delimitersUsed), BasedSequence.NULL,
            closer.getLeadChars(delimitersUsed));

    opener.moveNodesBetweenDelimitersTo(emphasis, closer);
  }

}
