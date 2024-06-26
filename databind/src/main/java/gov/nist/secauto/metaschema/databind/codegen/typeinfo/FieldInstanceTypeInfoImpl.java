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

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.impl.AnnotationGenerator;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public class FieldInstanceTypeInfoImpl
    extends AbstractNamedModelInstanceTypeInfo<IFieldInstanceAbsolute>
    implements IFieldInstanceTypeInfo {

  public FieldInstanceTypeInfoImpl(
      @NonNull IFieldInstanceAbsolute instance,
      @NonNull IAssemblyDefinitionTypeInfo parentDefinition) {
    super(instance, parentDefinition);
  }

  @Override
  public TypeName getJavaItemType() {
    TypeName retval;
    IFieldInstance fieldInstance = getInstance();
    if (fieldInstance.getDefinition().hasChildren()) {
      retval = super.getJavaItemType();
    } else {
      IDataTypeAdapter<?> dataType = fieldInstance.getDefinition().getJavaTypeAdapter();
      // this is a simple value
      retval = ObjectUtils.notNull(ClassName.get(dataType.getJavaClass()));
    }
    return retval;
  }

  @Override
  protected AnnotationSpec.Builder newBindingAnnotation() {
    return ObjectUtils.notNull(AnnotationSpec.builder(BoundField.class));
  }

  @SuppressWarnings("checkstyle:methodlength")
  @Override
  public Set<IModelDefinition> buildBindingAnnotation(
      TypeSpec.Builder typeBuilder,
      FieldSpec.Builder fieldBuilder,
      AnnotationSpec.Builder annotation) {
    // first build the core attributes
    final Set<IModelDefinition> retval = super.buildBindingAnnotation(typeBuilder, fieldBuilder, annotation);

    IFieldInstance instance = getInstance();

    // next build the field-specific attributes
    if (IFieldInstance.DEFAULT_FIELD_IN_XML_WRAPPED != instance.isInXmlWrapped()) {
      annotation.addMember("inXmlWrapped", "$L", instance.isInXmlWrapped());
    }

    IFieldDefinition definition = instance.getDefinition();
    IDataTypeAdapter<?> adapter = instance.getDefinition().getJavaTypeAdapter();

    Object defaultValue = instance.getDefaultValue();
    if (defaultValue != null) {
      annotation.addMember("defaultValue", "$S", adapter.asString(defaultValue));
    }

    // handle the field value related info
    if (!definition.hasChildren()) {
      // this is a simple field, without flags
      if (!MetaschemaDataTypeProvider.DEFAULT_DATA_TYPE.equals(adapter)) {
        annotation.addMember("typeAdapter", "$T.class", adapter.getClass());
      }
      AnnotationGenerator.buildValueConstraints(annotation, definition);
    }
    return retval;
  }
}
