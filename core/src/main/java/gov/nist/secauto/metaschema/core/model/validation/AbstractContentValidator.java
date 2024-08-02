/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.validation;

import gov.nist.secauto.metaschema.core.resource.AbstractResourceResolver;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

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
