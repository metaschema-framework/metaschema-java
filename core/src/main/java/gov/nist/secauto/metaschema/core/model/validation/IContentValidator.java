/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.validation;

import gov.nist.secauto.metaschema.core.model.util.JsonUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.transform.Source;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A common interface for validation of Metaschema module-based content.
 */
public interface IContentValidator {
  /**
   * Validate the resource at provided {@code path}.
   *
   * @param path
   *          the resource to validate
   * @return the result of the validation
   * @throws IOException
   *           if an error occurred while performing validation
   */
  @NonNull
  default IValidationResult validate(@NonNull Path path) throws IOException {
    try (InputStream is = ObjectUtils.notNull(Files.newInputStream(path))) {
      return validate(is, ObjectUtils.notNull(path.toUri()));
    }
  }

  /**
   * Validate the resource at the provided {@code url}.
   *
   * @param url
   *          the resource to validate
   * @return the result of the validation
   * @throws IOException
   *           if an error occurred while performing validation
   * @throws URISyntaxException
   *           if there is a problem with the provided {@code url}
   */
  @NonNull
  default IValidationResult validate(@NonNull URL url) throws IOException, URISyntaxException {
    return validate(ObjectUtils.notNull(url.toURI()));
  }

  /**
   * Validate the resource identified by the provided {@code uri}.
   *
   * @param uri
   *          the resource to validate
   * @return the result of the validation
   * @throws IOException
   *           if an error occurred while performing validation
   */
  @NonNull
  IValidationResult validate(@NonNull URI uri) throws IOException;

  /**
   * Validate the resource associated with the provided input stream {@code is}.
   *
   * @param is
   *          an input stream to access the resource
   * @param documentUri
   *          the URI of the resource to validate
   * @return the result of the validation
   * @throws IOException
   *           if an error occurred while performing validation
   */
  @NonNull
  IValidationResult validate(@NonNull InputStream is, @NonNull URI documentUri) throws IOException;

  /**
   * Validate the target using the provided XML schemas.
   *
   * @param target
   *          the target to validate
   * @param schemaSources
   *          the XML schema sources to validate with
   * @return the validation result
   * @throws IOException
   *           if an error occurred while performing validation
   * @throws SAXException
   *           if an error occurred while parsing the XML target or schema
   */
  @NonNull
  static IValidationResult validateWithXmlSchema(@NonNull URI target, @NonNull List<Source> schemaSources)
      throws IOException, SAXException {
    return new XmlSchemaContentValidator(schemaSources).validate(target);
  }

  /**
   * Validate the target using the provided JSON schema.
   *
   * @param target
   *          the target to validate
   * @param schema
   *          the JSON schema to validate with
   * @return the validation result
   * @throws IOException
   *           if an error occurred while performing validation
   * @see JsonUtil#toJsonObject(InputStream)
   * @see JsonUtil#toJsonObject(java.io.Reader)
   */
  @NonNull
  static IValidationResult validateWithJsonSchema(@NonNull URI target, @NonNull JSONObject schema)
      throws IOException {
    return new JsonSchemaContentValidator(schema).validate(target);
  }
}
