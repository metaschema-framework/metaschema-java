/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IContainerFlagSupport;
import gov.nist.secauto.metaschema.core.model.IFeatureDefinitionInstanceInlined;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureScalarItemValueHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;

import java.io.IOException;

public interface IBoundInstanceModelFieldScalar
    extends IBoundInstanceModelField,
    IBoundDefinitionModelField, IFeatureScalarItemValueHandler,
    IFeatureDefinitionInstanceInlined<IBoundDefinitionModelField, IBoundInstanceModelFieldScalar> {

  // integrate above
  @Override
  default IBoundDefinitionModelField getDefinition() {
    return IFeatureDefinitionInstanceInlined.super.getDefinition();
  }

  @Override
  default IBoundInstanceModelFieldScalar getInlineInstance() {
    return IFeatureDefinitionInstanceInlined.super.getInlineInstance();
  }

  @Override
  IBoundDefinitionModelAssembly getContainingDefinition();

  @Override
  default IContainerFlagSupport<IBoundInstanceFlag> getFlagContainer() {
    return IContainerFlagSupport.empty();
  }

  @Override
  default IBoundInstanceFlag getJsonKey() {
    // no flags
    return null;
  }

  @Override
  default IBoundInstanceFlag getItemJsonKey(Object item) {
    // no flags, no JSON key
    return null;
  }

  @Override
  default Object getFieldValue(Object item) {
    // the item is the field value
    return item;
  }

  @Override
  default String getJsonValueKeyName() {
    // no bound value, no value key name
    return null;
  }

  @Override
  default IBoundInstanceFlag getJsonValueKeyFlagInstance() {
    // no bound value, no value key name
    return null;
  }

  @Override
  default Object readItem(Object parent, IItemReadHandler handler) throws IOException {
    return handler.readItemField(ObjectUtils.requireNonNull(parent, "parent"), this);
  }

  @Override
  default void writeItem(Object item, IItemWriteHandler handler) throws IOException {
    handler.writeItemField(item, this);
  }
}
