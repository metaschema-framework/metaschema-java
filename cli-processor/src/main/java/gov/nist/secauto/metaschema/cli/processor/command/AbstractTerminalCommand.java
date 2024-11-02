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
import java.util.Collection;
import java.util.Collections;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public abstract class AbstractTerminalCommand implements ICommand {
  private static Lazy<Path> currentWorkingDirectory = Lazy.lazy(() -> Paths.get("").toAbsolutePath());

  @SuppressWarnings("null")
  @Override
  public Collection<ICommand> getSubCommands() {
    return Collections.emptyList();
  }

  @Override
  public boolean isSubCommandRequired() {
    return false;
  }

  @NonNull
  protected static Path getCurrentWorkingDirectory() {
    return ObjectUtils.notNull(currentWorkingDirectory.get());
  }

  @NonNull
  protected static Path resolveAgainstCWD(@NonNull Path path) {
    return ObjectUtils.notNull(getCurrentWorkingDirectory().resolve(path).normalize());
  }

  @NonNull
  protected static URI resolveAgainstCWD(@NonNull URI uri) {
    return ObjectUtils.notNull(getCurrentWorkingDirectory().toUri().resolve(uri.normalize()));
  }

  @NonNull
  protected static URI resolveAgainstCWD(@NonNull String uri) throws URISyntaxException {
    return UriUtils.toUri(uri, ObjectUtils.notNull(getCurrentWorkingDirectory().toUri()));
  }
}
