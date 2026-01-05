/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor;

import dev.metaschema.core.util.IVersionInfo;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A simple version info implementation for testing.
 */
class TestVersionInfo implements IVersionInfo {

  @Override
  @NonNull
  public String getName() {
    return "test-cli";
  }

  @Override
  @NonNull
  public String getVersion() {
    return "1.0.0-test";
  }

  @Override
  @NonNull
  public String getBuildTimestamp() {
    return "2025-01-01T00:00:00Z";
  }

  @Override
  @NonNull
  public String getGitOriginUrl() {
    return "https://example.com/test.git";
  }

  @Override
  @NonNull
  public String getGitBranch() {
    return "test-branch";
  }

  @Override
  @NonNull
  public String getGitCommit() {
    return "abc1234";
  }

  @Override
  @NonNull
  public String getGitClosestTag() {
    return "v1.0.0";
  }
}
