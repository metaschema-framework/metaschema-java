
package gov.nist.secauto.metaschema.core.metapath.node;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractNodeItem implements INodeItem {

  /**
   * Generates a string signature for this node item in the format:
   * {@code type\u2ABBlocation_metapath\u2ABC} or
   * {@code type\u2ABBlocation_metapath\u2ABC(value)} where:
   * <ul>
   * <li>type: The node type signature</li>
   * <li>location_metapath: A Metapath for the node's location in the
   * document</li>
   * <li>value: Optional value signature if a value is present</li>
   * </ul>
   * The special characters \u2ABB and \u2ABC are used as delimiters to clearly
   * separate the type from the location Metapath expression.
   *
   * @return the string signature of this node item
   */
  @Override
  public final String toSignature() {
    StringBuilder builder = new StringBuilder()
        .append(getType().toSignature())
        .append('\u2ABB')
        .append(getMetapath())
        .append('\u2ABC');
    String value = getValueSignature();
    if (value != null) {
      builder.append('(')
          .append(value)
          .append(')');
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
