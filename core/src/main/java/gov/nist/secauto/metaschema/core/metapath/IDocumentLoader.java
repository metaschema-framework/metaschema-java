/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.IResourceResolver;
import gov.nist.secauto.metaschema.core.model.IUriResolver;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports loading documents referenced in Metapath expressions.
 */
public interface IDocumentLoader extends IResourceResolver {
  /**
   * Allows setting an {@link IUriResolver}, which will be used to map URIs prior
   * to loading the resource.
   *
   * @param resolver
   *          the resolver to set
   */
  void setUriResolver(@NonNull IUriResolver resolver);

  /**
   * Load a Metaschema-based document from a file resource.
   *
   * @param file
   *          the file to load
   * @return a document item representing the contents of the document.
   * @throws IOException
   *           if an error occurred while parsing the file
   */
  @NonNull
  default IDocumentNodeItem loadAsNodeItem(@NonNull File file) throws IOException {
    return loadAsNodeItem(ObjectUtils.notNull(file.toPath()));
  }

  /**
   * Load a Metaschema-based document from a file resource identified by a path.
   *
   * @param path
   *          the file to load
   * @return a document item representing the contents of the document.
   * @throws IOException
   *           if an error occurred while parsing the file
   */
  @NonNull
  default IDocumentNodeItem loadAsNodeItem(@NonNull Path path) throws IOException {
    return loadAsNodeItem(ObjectUtils.notNull(path.toUri()));
  }

  /**
   * Load a Metaschema-based document from a URL resource.
   *
   * @param url
   *          the resource to load
   * @return a document item representing the contents of the document.
   * @throws IOException
   *           if an error occurred while parsing the resource
   * @throws URISyntaxException
   *           if the URL is not a valid URI
   */
  @NonNull
  default IDocumentNodeItem loadAsNodeItem(@NonNull URL url) throws IOException, URISyntaxException {
    return loadAsNodeItem(ObjectUtils.notNull(url.toURI()));
  }

  /**
   * Load a Metaschema-based document from a URI resource.
   * <p>
   * This is the expected, primary entry point for implementations.
   *
   * @param uri
   *          the resource to load
   * @return a document item representing the contents of the document.
   * @throws IOException
   *           if an error occurred while parsing the resource
   */
  @NonNull
  IDocumentNodeItem loadAsNodeItem(@NonNull URI uri) throws IOException;
}
