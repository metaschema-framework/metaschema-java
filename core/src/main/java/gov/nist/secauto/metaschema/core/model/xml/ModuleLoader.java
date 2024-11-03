/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import gov.nist.secauto.metaschema.core.model.AbstractModuleLoader;
import gov.nist.secauto.metaschema.core.model.IModuleLoader;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.ExternalConstraintsModulePostProcessor;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.xml.impl.XmlModule;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.METASCHEMADocument;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides methods to load a Metaschema expressed in XML.
 * <p>
 * Loaded Metaschema instances are cached to avoid the need to load them for
 * every use. Any Metaschema imported is also loaded and cached automatically.
 */
public class ModuleLoader
    extends AbstractModuleLoader<METASCHEMADocument, IXmlMetaschemaModule> {
  private boolean resolveEntities; // = false;
  @NonNull
  private final List<IModuleLoader.IModulePostProcessor> modulePostProcessors;

  /**
   * Construct a new Metaschema loader.
   */
  public ModuleLoader() {
    this(CollectionUtil.<IModuleLoader.IModulePostProcessor>emptyList());
  }

  /**
   * Construct a new Metaschema loader, which applies the provided constraints to
   * loaded modules.
   *
   * @param constraints
   *          a set of Metaschema module constraints
   */
  public ModuleLoader(@NonNull Collection<IConstraintSet> constraints) {
    this(CollectionUtil.singletonList(new ExternalConstraintsModulePostProcessor(constraints)));
  }

  /**
   * Construct a new Metaschema loader, which use the provided module post
   * processors when loading a module.
   *
   * @param modulePostProcessors
   *          post processors to perform additional module customization when
   *          loading
   */
  public ModuleLoader(@NonNull List<IModuleLoader.IModulePostProcessor> modulePostProcessors) {
    this.modulePostProcessors = modulePostProcessors;
  }

  /**
   * Enable a mode that allows XML entity resolution. This may be needed to parse
   * some resource files that contain entities. Enabling entity resolution is a
   * less secure, which requires trust in the resource content being parsed.
   */
  public void allowEntityResolution() {
    resolveEntities = true;
  }

  @Override
  protected IXmlMetaschemaModule newModule(
      URI resource,
      METASCHEMADocument binding,
      List<? extends IXmlMetaschemaModule> importedModules)
      throws MetaschemaException {
    IXmlMetaschemaModule module = new XmlModule(resource, binding, importedModules);

    for (IModuleLoader.IModulePostProcessor postProcessor : modulePostProcessors) {
      postProcessor.processModule(module);
    }
    return module;
  }

  @Override
  protected List<URI> getImports(METASCHEMADocument binding) {
    return ObjectUtils.notNull(binding.getMETASCHEMA().getImportList().stream()
        .map(imported -> URI.create(imported.getHref()))
        .collect(Collectors.toList()));
  }

  /**
   * Parse the provided XML resource as a Metaschema module.
   *
   * @param resource
   *          the resource to parse
   * @return the XMLBeans representation of the Metaschema module
   * @throws IOException
   *           if a parsing error occurred
   */
  @Override
  protected METASCHEMADocument parseModule(@NonNull URI resource) throws IOException {
    METASCHEMADocument metaschemaXml;
    try {
      XmlOptions options = new XmlOptions();
      if (resolveEntities) {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
          // factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
          factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
          factory.setFeature("http://xml.org/sax/features/external-general-entities", true);
          factory.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
          SAXParser parser = factory.newSAXParser();
          parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "file"); // ,jar:file
          XMLReader reader = parser.getXMLReader();
          reader.setEntityResolver((publicId, systemId) -> null);
          options.setLoadUseXMLReader(reader);
        } catch (SAXException | ParserConfigurationException ex) {
          throw new IOException(ex);
        }
        // options.setLoadEntityBytesLimit(204800);
        // options.setLoadUseDefaultResolver();
        options.setEntityResolver((publicId, systemId) -> {
          String effectiveSystemId = systemId;
          // TODO: It's very odd that the system id looks like this. Need to investigate.
          if (effectiveSystemId.startsWith("file://file://")) {
            effectiveSystemId = effectiveSystemId.substring(14);
          }
          URI resolvedSystemId = resource.resolve(effectiveSystemId);
          return new InputSource(resolvedSystemId.toString());
        });
        options.setLoadDTDGrammar(true);
      }
      options.setBaseURI(resource);
      options.setLoadLineNumbers();
      metaschemaXml = ObjectUtils.notNull(METASCHEMADocument.Factory.parse(resource.toURL(), options));
    } catch (XmlException ex) {
      throw new IOException(ex);
    }
    return metaschemaXml;
  }

}
