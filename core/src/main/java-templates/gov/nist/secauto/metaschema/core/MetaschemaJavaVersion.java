
package gov.nist.secauto.metaschema.core;

import gov.nist.secauto.metaschema.core.util.IVersionInfo;

public class MetaschemaJavaVersion implements IVersionInfo {

  public static final String NAME = "metaschema-java";
  public static final String VERSION = "${project.version}";
  public static final String BUILD_TIMESTAMP = "${timestamp}";
  public static final String COMMIT = "@git.commit.id.abbrev@";
  public static final String BRANCH = "@git.branch@";
  public static final String CLOSEST_TAG = "@git.closest.tag.name@";
  public static final String ORIGIN = "@git.remote.origin.url@";

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
