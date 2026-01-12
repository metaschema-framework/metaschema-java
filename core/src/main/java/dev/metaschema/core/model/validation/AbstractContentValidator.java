/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.validation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import dev.metaschema.core.model.AbstractResourceResolver;
import dev.metaschema.core.util.ObjectUtils;

/**
 * Base class for a content validator.
 */
public abstract class AbstractContentValidator
    extends AbstractResourceResolver
    implements IContentValidator {

  @Override
  public IValidationResult validate(URI uri) throws IOException {
    URI resourceUri = resolve(uri);
    URL resource = resourceUri.toURL();

    try (InputStream is = new BufferedInputStream(ObjectUtils.notNull(resource.openStream()))) {
      return validate(is, resourceUri);
    }
  }
}
