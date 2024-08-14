/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.ModelWalker;
import gov.nist.secauto.metaschema.core.util.DefaultDiagramNode.ChoiceEdge;
import gov.nist.secauto.metaschema.core.util.DefaultDiagramNode.ChoiceGroupEdge;
import gov.nist.secauto.metaschema.core.util.DefaultDiagramNode.ModelEdge;
import gov.nist.secauto.metaschema.core.util.IDiagramNode.IEdge;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class MermaidErDiagramGenerator {

  public void generate(@NonNull IModule module, @NonNull PrintWriter writer) throws IOException {
    IModuleNodeItem moduleItem = INodeItemFactory.instance().newModuleNodeItem(module);

    DiagramNodeModelVisitor visitor = new DiagramNodeModelVisitor();

    writer.println("erDiagram");

    for (IAssemblyDefinition root : module.getExportedRootAssemblyDefinitions()) {
      visitor.walk(root);
    }

    MermaidNodeVistor mermaidVisitor = new MermaidNodeVistor(visitor, writer);
    for (IDiagramNode node : visitor.getNodes()) {
      writer.format("  %s[\"%s\"] {%n", node.getName(), node.getLabel());

      for (IDiagramNode.IAttribute attribute : node.getAttributes()) {
        writer.format("    %s %s%n",
            attribute.getDataType().getPreferredName().getLocalPart(),
            attribute.getName());
      }
      writer.format("  }%n");
      for (IEdge edge : node.getEdges()) {
        writer.flush();
        edge.accept(mermaidVisitor);
      }
    }

    // writer.print(visitor.getDiagram());
  }

  private final class MermaidNodeVistor implements IDiagramNodeVisitor {
    @NonNull
    private final DiagramNodeModelVisitor nodeVisitor;
    @NonNull
    private final PrintWriter writer;

    private MermaidNodeVistor(
        @NonNull DiagramNodeModelVisitor nodeVisitor,
        @NonNull PrintWriter writer) {
      this.nodeVisitor = nodeVisitor;
      this.writer = writer;
    }

    @NonNull
    public DiagramNodeModelVisitor getNodeVisitor() {
      return nodeVisitor;
    }

    @NonNull
    public PrintWriter getWriter() {
      return writer;
    }

    @Override
    public void visit(ModelEdge edge) {
      INamedModelInstanceAbsolute instance = edge.getInstance();
      IModelDefinition definition = instance.getDefinition();
      writeRelationship(
          edge.getSubjectNode(),
          ObjectUtils.requireNonNull(getNodeVisitor().lookup(definition)),
          edge.getRelationship(),
          instance.getEffectiveName());
    }

    @Override
    public void visit(ChoiceEdge edge) {
      INamedModelInstanceAbsolute instance = edge.getInstance();
      IModelDefinition definition = instance.getDefinition();
      writeRelationship(
          edge.getSubjectNode(),
          ObjectUtils.requireNonNull(getNodeVisitor().lookup(definition)),
          edge.getRelationship(),
          "Choice: " + instance.getEffectiveName());
    }

    @Override
    public void visit(ChoiceGroupEdge edge) {
      INamedModelInstanceGrouped instance = edge.getInstance();
      IModelDefinition definition = instance.getDefinition();
      writeRelationship(
          edge.getSubjectNode(),
          ObjectUtils.requireNonNull(getNodeVisitor().lookup(definition)),
          edge.getRelationship(),
          "ChoiceGroup: " + edge.getInstance().getEffectiveDisciminatorValue() + ": " + instance.getEffectiveName());
    }

    private void writeRelationship(
        @NonNull IDiagramNode left,
        @NonNull IDiagramNode right,
        @NonNull IDiagramNode.Relationship relationship,
        @NonNull String label) {
      getWriter().format("  %s %s %s : \"%s\"%n",
          left.getName(),
          relationship.generate(),
          right.getName(),
          label);
    }

  }

  private static final class DiagramNodeModelVisitor
      extends ModelWalker<Void> {
    private final Map<IModelDefinition, IDiagramNode> nodeMap = new LinkedHashMap<>();

    public Collection<IDiagramNode> getNodes() {
      return CollectionUtil.unmodifiableCollection(nodeMap.values());
    }

    @Nullable
    public IDiagramNode lookup(@NonNull IModelDefinition definition) {
      return nodeMap.get(definition);
    }

    @Override
    protected Void getDefaultData() {
      return null;
    }

    @Override
    protected void visit(IFlagDefinition def, Void data) {
      // do nothing
    }

    @Override
    protected boolean visit(IFieldDefinition def, Void data) {
      return !def.getFlagInstances().isEmpty() && handleDefinition(def);
    }

    @Override
    protected boolean visit(IAssemblyDefinition def, Void data) {
      return handleDefinition(def);
    }

    private boolean handleDefinition(@NonNull IModelDefinition definition) {
      boolean exists = nodeMap.containsKey(definition);
      if (!exists) {
        nodeMap.put(definition, new DefaultDiagramNode(definition, this::lookup));
      }
      return !exists;
    }
  }
}
