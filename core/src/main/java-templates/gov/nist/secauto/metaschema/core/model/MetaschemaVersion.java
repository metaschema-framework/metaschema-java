

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.IVersionInfo;

/**
 * Provides version information for the underlying Metaschema implementation used by this library.
 */
public class MetaschemaVersion implements IVersionInfo {

  private static final String NAME = "metaschema";
  private static final String BUILD_VERSION = "${project.version}";
  private static final String BUILD_TIMESTAMP = "${timestamp}";
  private static final String COMMIT = "@metaschema-git.commit.id.abbrev@";
  private static final String BRANCH = "@metaschema-git.branch@";
  private static final String CLOSEST_TAG = "@metaschema-git.closest.tag.name@";
  private static final String ORIGIN = "@metaschema-git.remote.origin.url@";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getVersion() {
    return BUILD_VERSION;
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
