/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.model.IDefinition;
import dev.metaschema.core.model.IModelElement;
import dev.metaschema.core.model.INamedInstance;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.schemagen.SchemaGenerationException;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Generates XML Schema documentation elements for Metaschema model elements.
 * <p>
 * This class produces {@code xs:annotation} elements containing both structured
 * application information and human-readable documentation content.
 */
public final class DocumentationGenerator {

  @Nullable
  private final String formalName;
  @Nullable
  private final MarkupLine description;
  @NonNull
  private final List<MarkupMultiline> remarks;
  @NonNull
  private final IModelElement modelElement;

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

  /**
   * Retrieves the formal name of the model element.
   *
   * @return the formal name, or {@code null} if not defined
   */
  @Nullable
  public String getFormalName() {
    return formalName;
  }

  /**
   * Retrieves the description of the model element.
   *
   * @return the description as markup, or {@code null} if not defined
   */
  @Nullable
  public MarkupLine getDescription() {
    return description;
  }

  /**
   * Retrieves the remarks associated with the model element.
   *
   * @return a list of remarks, which may be empty but never {@code null}
   */
  @NonNull
  public List<MarkupMultiline> getRemarks() {
    return remarks;
  }

  /**
   * Retrieves the underlying model element.
   *
   * @return the model element
   */
  @NonNull
  public IModelElement getModelElement() {
    return modelElement;
  }

  private void generate(@NonNull IXmlGenerationState state) {
    String formalName = getFormalName();
    MarkupLine description = getDescription();
    List<MarkupMultiline> remarks = getRemarks();

    if (formalName != null || description != null || !remarks.isEmpty()) {
      generateDocumentation(formalName, description, remarks, state.getNS(getModelElement()), state);
    }
  }

  /**
   * Generates XML Schema documentation for a definition.
   *
   * @param definition
   *          the definition to generate documentation for
   * @param state
   *          the XML generation state for writing output
   */
  public static void generateDocumentation(
      @NonNull IDefinition definition,
      @NonNull IXmlGenerationState state) {
    new DocumentationGenerator(definition).generate(state);
  }

  /**
   * Generates XML Schema documentation for a named instance.
   *
   * @param instance
   *          the named instance to generate documentation for
   * @param state
   *          the XML generation state for writing output
   */
  public static void generateDocumentation(
      @NonNull INamedInstance instance,
      @NonNull IXmlGenerationState state) {
    new DocumentationGenerator(instance).generate(state);
  }

  /**
   * Generates XML Schema documentation with explicit content.
   * <p>
   * Creates an {@code xs:annotation} element containing both structured
   * application information ({@code xs:appinfo}) and human-readable documentation
   * ({@code xs:documentation}).
   *
   * @param formalName
   *          the formal name, or {@code null} if not available
   * @param description
   *          the description markup, or {@code null} if not available
   * @param remarks
   *          the list of remarks to include
   * @param xmlNS
   *          the target XML namespace for custom elements
   * @param state
   *          the XML generation state for writing output
   */
  public static void generateDocumentation( // NOPMD acceptable complexity
      @Nullable String formalName,
      @Nullable MarkupLine description,
      @NonNull List<MarkupMultiline> remarks,
      @NonNull String xmlNS, @NonNull IXmlGenerationState state) {

    try {
      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "annotation", XmlDatatypeManager.NS_XML_SCHEMA);
      if (formalName != null || description != null) {
        state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "appinfo", XmlDatatypeManager.NS_XML_SCHEMA);

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

      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "documentation", XmlDatatypeManager.NS_XML_SCHEMA);
      state.writeNamespace("", XmlDatatypeManager.NS_XHTML);

      if (description != null) {
        // write description
        state.writeStartElement(XmlDatatypeManager.NS_XHTML, "p");

        if (formalName != null) {
          state.writeStartElement(XmlDatatypeManager.NS_XHTML, "b");
          state.writeCharacters(formalName);
          state.writeEndElement();
          state.writeCharacters(": ");
        }

        description.writeXHtml(XmlDatatypeManager.NS_XHTML, state.getXMLStreamWriter());
        state.writeEndElement(); // p
      }

      for (MarkupMultiline remark : remarks) {
        remark.writeXHtml(XmlDatatypeManager.NS_XHTML, state.getXMLStreamWriter());
      }

      state.writeEndElement(); // xs:documentation
      state.writeEndElement(); // xs:annotation
    } catch (XMLStreamException ex) {
      throw new SchemaGenerationException(ex);
    }
  }
}
