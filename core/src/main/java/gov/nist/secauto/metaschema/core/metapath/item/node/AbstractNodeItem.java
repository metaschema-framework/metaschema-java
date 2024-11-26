
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractNodeItem implements INodeItem {

  @Override
  public final String toSignature() {
    StringBuilder builder = new StringBuilder()
        .append(getType().toSignature())
        .append("⪻")
        .append(getMetapath())
        .append("⪼");
    String value = getValueSignature();
    if (value != null) {
      builder.append("(");
      builder.append(value);
      builder.append(")");
    }
    return ObjectUtils.notNull(builder.toString());
  }

  @Nullable
  protected abstract String getValueSignature();

  @Override
  public final String toString() {
    return toSignature();
  }
}
