

package gov.nist.secauto.metaschema.cli.processor;

import static org.fusesource.jansi.Ansi.ansi;

import java.io.PrintStream;

public class Version implements VersionInfo {

  public static final String VERSION = "${project.version}";
  public static final String BUILD_TIMESTAMP = "${timestamp}";
  public static final String COMMIT = "@git.commit.id.abbrev@";

  public Version() {
  }

  @Override
  public String getVersion() {
    return VERSION;
  }

  @Override
  public String getBuildTime() {
    return BUILD_TIMESTAMP;
  }

  @Override
  public String getCommit() {
    return COMMIT;
  }
  
  @Override
  public void generateExtraInfo(PrintStream out) {
    out.println(ansi()
        .a("Metaschema version ").bold().a(getVersion()).boldOff()
        .a(" on commit ").bold().a(getCommit()).boldOff()
        .a(" built at ").bold().a( getBuildTime()).reset());
  }
}
