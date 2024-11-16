

package gov.nist.secauto.metaschema.cli.processor;

import gov.nist.secauto.metaschema.core.util.IVersionInfo;

/**
 * Provides version information for this library.
 * <p>
 * This class exposes build-time metadata including version numbers, build
 * timestamps, and Git repository information.
 */
public class ProcessorVersion implements IVersionInfo {

  private static final String NAME = "${project.name}";
  private static final String VERSION = "${project.version}";
  private static final String BUILD_TIMESTAMP = "${timestamp}";
  private static final String COMMIT = "@git.commit.id.abbrev@";
  private static final String BRANCH = "@git.branch@";
  private static final String CLOSEST_TAG = "@git.closest.tag.name@";
  private static final String ORIGIN = "@git.remote.origin.url@";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getVersion() {
    return VERSION;
  }

  @Override
  public String getBuildTimestamp() {
    return BUILD_TIMESTAMP;
  }

  @Override
  public String getGitOriginUrl() {
    return ORIGIN;
  }

  @Override
  public String getGitCommit() {
    return COMMIT;
  }

  @Override
  public String getGitBranch() {
    return BRANCH;
  }

  @Override
  public String getGitClosestTag() {
    return CLOSEST_TAG;
  }
}
