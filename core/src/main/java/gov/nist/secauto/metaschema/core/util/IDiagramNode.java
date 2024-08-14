
package gov.nist.secauto.metaschema.core.util;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModelInstance;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDiagramNode {
  @NonNull
  IModelDefinition getDefinition();

  @NonNull
  String getName();

  @NonNull
  default String getLabel() {
    return getDefinition().getEffectiveName();
    // IModelDefinition definition = getDefinition();
    //
    // String formalName = definition.getEffectiveFormalName();
    // return formalName == null ? definition.getEffectiveName() : formalName;
  }

  @NonNull
  List<IAttribute> getAttributes();

  @NonNull
  List<IEdge> getEdges();

  interface IAttribute {
    @NonNull
    IDiagramNode getSubjectNode();

    @NonNull
    String getName();

    @NonNull
    IDataTypeAdapter<?> getDataType();
  }

  interface IEdge {
    @NonNull
    IDiagramNode getSubjectNode();

    @NonNull
    Relationship getRelationship();

    @NonNull
    IModelInstance getInstance();

    void accept(@NonNull IDiagramNodeVisitor visitor);
  }

  enum Relationship {
    ZERO_OR_ONE("|o", "o|"),
    ONE("||", "||"),
    ZERO_OR_MORE("}o", "o{"),
    ONE_OR_MORE("}|", "|{");

    @NonNull
    private final String left;
    @NonNull
    private final String right;

    Relationship(@NonNull String left, @NonNull String right) {
      this.left = left;
      this.right = right;
    }

    @NonNull
    public String getLeft() {
      return left;
    }

    @NonNull
    public String getRight() {
      return right;
    }

    @NonNull
    public static Relationship toRelationship(@NonNull IGroupable groupable) {
      return toRelationship(groupable.getMinOccurs(), groupable.getMaxOccurs());
    }

    @NonNull
    public static Relationship toRelationship(int minOccurs, int maxOccurs) {
      return minOccurs < 1
          ? maxOccurs == 1
              ? ZERO_OR_ONE
              : ZERO_OR_MORE
          : maxOccurs == 1
              ? ONE
              : ONE_OR_MORE;
    }

    public String generate() {
      return getLeft() + "--" + getRight();
    }
  }
}
