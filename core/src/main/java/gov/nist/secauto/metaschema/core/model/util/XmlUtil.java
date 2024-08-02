/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.util;

import java.io.IOException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class XmlUtil {

  private XmlUtil() {
    // disable construction
  }

  /**
   * Create a {@link Source} based on the provided {@code url}.
   * <p>
   * The caller of this method must ensure that the stream associated with this
   * source is closed.
   *
   * @param url
   *          the URL to use for the source
   * @return a new source
   * @throws IOException
   *           if an error occurred while creating the underlying stream
   */
  @SuppressWarnings("resource") // user of source is expected to close
  @NonNull
  public static StreamSource getStreamSource(@NonNull URL url) throws IOException {
    return new StreamSource(url.openStream(), url.toString());
  }
}
