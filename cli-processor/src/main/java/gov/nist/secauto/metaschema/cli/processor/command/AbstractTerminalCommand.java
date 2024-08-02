/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractTerminalCommand implements ICommand {

  @SuppressWarnings("null")
  @Override
  public Collection<ICommand> getSubCommands() {
    return Collections.emptyList();
  }

  @Override
  public boolean isSubCommandRequired() {
    return false;
  }

  protected static Path resolvePathAgainstCWD(@NonNull Path path) {
    return Paths.get("").toAbsolutePath().resolve(path).normalize();
  }
}
