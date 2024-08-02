/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import java.io.PrintStream;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface VersionInfo {
  @NonNull
  String getVersion();

  @NonNull
  String getBuildTime();

  @NonNull
  String getCommit();

  void generateExtraInfo(@NonNull PrintStream out);
}
