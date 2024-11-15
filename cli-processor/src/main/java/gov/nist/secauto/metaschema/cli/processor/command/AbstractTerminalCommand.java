/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * A base class for commands that have no children.
 */
public abstract class AbstractTerminalCommand implements ICommand {
  private static Lazy<Path> currentWorkingDirectory = Lazy.lazy(() -> Paths.get("").toAbsolutePath());

  /**
   * A utility method that can be used to get the current working directory.
   *
   * @return the current working directory
   */
  @NonNull
  protected static Path getCurrentWorkingDirectory() {
    return ObjectUtils.notNull(currentWorkingDirectory.get());
  }

  /**
   * A utility method that can be used to resolve a path against the current
   * working directory.
   *
   * @param path
   *          the path to resolve
   *
   * @return the resolved path
   */
  @NonNull
  protected static Path resolveAgainstCWD(@NonNull Path path) {
    return ObjectUtils.notNull(getCurrentWorkingDirectory().resolve(path).normalize());
  }

  /**
   * A utility method that can be used to resolve a URI against the URI for the
   * current working directory.
   *
   * @param uri
   *          the uri to resolve
   *
   * @return the resolved URI
   */
  @NonNull
  protected static URI resolveAgainstCWD(@NonNull URI uri) {
    return ObjectUtils.notNull(getCurrentWorkingDirectory().toUri().resolve(uri.normalize()));
  }

  /**
   * A utility method that can be used to resolve a URI (as a string) against the
   * URI for the current working directory.
   *
   * @param uri
   *          the uri to resolve
   * @return the resolved URI
   * @throws URISyntaxException
   *           if the provided URI is not a valid URI
   */
  @NonNull
  protected static URI resolveAgainstCWD(@NonNull String uri) throws URISyntaxException {
    return UriUtils.toUri(uri, ObjectUtils.notNull(getCurrentWorkingDirectory().toUri()));
  }
}
