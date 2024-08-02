/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides version information for a runtime dependency or application.
 */
public interface IVersionInfo {
  /**
   * The subject's name.
   *
   * @return the name
   */
  @NonNull
  String getName();

  /**
   * The subject's version.
   *
   * @return the version
   */
  @NonNull
  String getVersion();

  /**
   * The time the subject was last built.
   *
   * @return the build time
   */
  @NonNull
  String getBuildTimestamp();

  /**
   * The git repository URL used to retrieve the branch.
   *
   * @return the git repository URL
   */
  @NonNull
  String getGitOriginUrl();

  /**
   * The last git commit hash.
   *
   * @return the commit hash
   */
  @NonNull
  String getGitCommit();

  /**
   * The current git branch.
   *
   * @return the git branch
   */
  @NonNull
  String getGitBranch();

  /**
   * The closest tag in the git commit history.
   *
   * @return a tag name
   */
  @NonNull
  String getGitClosestTag();
}
