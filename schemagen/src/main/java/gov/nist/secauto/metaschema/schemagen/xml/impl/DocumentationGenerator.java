/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelElement;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class DocumentationGenerator {

  private final @Nullable String formalName;
  private final @Nullable MarkupLine description;
  private final @NonNull List<MarkupMultiline> remarks;
  private final @NonNull IModelElement modelElement;

  private DocumentationGenerator(@NonNull IDefinition definition) {
    this.formalName = definition.getEffectiveFormalName();
    this.description = definition.getEffectiveDescription();

    MarkupMultiline remarks = definition.getRemarks();
    this.remarks = remarks == null ? CollectionUtil.emptyList() : CollectionUtil.singletonList(remarks);

    this.modelElement = definition;
  }

  private DocumentationGenerator(@NonNull INamedInstance instance) {
    this.formalName = instance.getEffectiveFormalName();
    this.description = instance.getEffectiveDescription();

    List<MarkupMultiline> remarks = new ArrayList<>(2);
    MarkupMultiline remark = instance.getRemarks();
    if (remark != null) {
      remarks.add(remark);
    }

    remark = instance.getDefinition().getRemarks();
    if (remark != null) {
      remarks.add(remark);
    }

    this.remarks = CollectionUtil.listOrEmpty(remarks);

    this.modelElement = instance;
  }

  @Nullable
  public String getFormalName() {
    return formalName;
  }

  @Nullable
  public MarkupLine getDescription() {
    return description;
  }

  @NonNull
  public List<MarkupMultiline> getRemarks() {
    return remarks;
  }

  @NonNull
  public IModelElement getModelElement() {
    return modelElement;
  }

  private void generate(@NonNull XmlGenerationState state) {
    String formalName = getFormalName();
    MarkupLine description = getDescription();
    List<MarkupMultiline> remarks = getRemarks();

    if (formalName != null || description != null || !remarks.isEmpty()) {
      generateDocumentation(formalName, description, remarks, state.getNS(getModelElement()), state);
    }
  }

  public static void generateDocumentation(
      @NonNull IDefinition definition,
      @NonNull XmlGenerationState state) {
    new DocumentationGenerator(definition).generate(state);
  }

  public static void generateDocumentation(
      @NonNull INamedInstance instance,
      @NonNull XmlGenerationState state) {
    new DocumentationGenerator(instance).generate(state);
  }

  public static void generateDocumentation( // NOPMD acceptable complexity
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull List<MarkupMultiline> remarks,
      @NonNull String xmlNS, @NonNull XmlGenerationState state) {

    try {
      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "annotation", XmlSchemaGenerator.NS_XML_SCHEMA);
      if (formalName != null || description != null) {
        state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "appinfo", XmlSchemaGenerator.NS_XML_SCHEMA);

        if (formalName != null) {
          state.writeStartElement(xmlNS, "formal-name");
          state.writeCharacters(formalName);
          state.writeEndElement();
        }

        if (description != null) {
          state.writeStartElement(xmlNS, "description");
          description.writeXHtml(xmlNS, state.getXMLStreamWriter());
          state.writeEndElement();
        }

        state.writeEndElement(); // xs:appInfo
      }

      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "documentation", XmlSchemaGenerator.NS_XML_SCHEMA);
      state.writeNamespace("", XmlSchemaGenerator.NS_XHTML);

      if (description != null) {
        // write description
        state.writeStartElement(XmlSchemaGenerator.NS_XHTML, "p");

        if (formalName != null) {
          state.writeStartElement(XmlSchemaGenerator.NS_XHTML, "b");
          state.writeCharacters(formalName);
          state.writeEndElement();
          state.writeCharacters(": ");
        }

        description.writeXHtml(XmlSchemaGenerator.NS_XHTML, state.getXMLStreamWriter());
        state.writeEndElement(); // p
      }

      for (MarkupMultiline remark : remarks) {
        remark.writeXHtml(XmlSchemaGenerator.NS_XHTML, state.getXMLStreamWriter());
      }

      state.writeEndElement(); // xs:documentation
      state.writeEndElement(); // xs:annotation
    } catch (XMLStreamException ex) {
      throw new SchemaGenerationException(ex);
    }
  }
}
