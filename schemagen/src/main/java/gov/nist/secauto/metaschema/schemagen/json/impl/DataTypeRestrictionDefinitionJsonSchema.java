/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.DecimalAdapter;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.AbstractGenerationState.AllowedValueCollection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a restriction of Metaschema
 * data type, which can be generated inline or as a JSON schema definition.
 * <p>
 * The data type values are restricted based on a set of {@link IAllowedValue}
 * entries that are part of an {@link IAllowedValuesConstraint} associated with
 * the provided definition.
 */
public class DataTypeRestrictionDefinitionJsonSchema
    implements IDataTypeJsonSchema {
  @NonNull
  private final String name;
  @NonNull
  private final IDataTypeAdapter<?> dataTypeAdapter;
  @NonNull
  private final AllowedValueCollection allowedValuesCollection;

  /**
   * Construct a new data type JSON schema based on a Metaschema definition whose
   * constraints may further restrict the values allowed by the associated data
   * type.
   *
   * @param definition
   *          the Metaschema definition that declares the data type
   * @param allowedValuesCollection
   *          the constraints restricting values
   * @param state
   *          the schema generation state used for context
   */
  public DataTypeRestrictionDefinitionJsonSchema(
      @NonNull IValuedDefinition definition,
      @NonNull AllowedValueCollection allowedValuesCollection,
      @NonNull IJsonGenerationState state) {
    this.name = state.generateJsonSchemaDefinitionName(definition, null, "Value");
    this.dataTypeAdapter = definition.getJavaTypeAdapter();
    CollectionUtil.requireNonEmpty(allowedValuesCollection.getValues());
    this.allowedValuesCollection = allowedValuesCollection;
  }

  @Override
  public IDataTypeAdapter<?> getDataTypeAdapter() {
    return dataTypeAdapter;
  }

  @Override
  public boolean isInline(IJsonGenerationState state) {
    // always inline
    return true;
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinitionAssembly> visited,
      IJsonGenerationState state) {
    // ensure the base datatype is registered
    state.getSchema(getDataTypeAdapter());
    return ObjectUtils.notNull(Stream.of(this));
  }

  @Override
  public String getDefinitionName() {
    return name;
  }

  @Override
  public void generateDefinitionJsonSchema(ObjectNode node, IJsonGenerationState state) {
    throw new UnsupportedOperationException("not used");
  }

  @Override
  public void generateInlineJsonSchema(ObjectNode node, IJsonGenerationState state) {

    // generate a restriction on the built-in type for the enumerated values
    ArrayNode enumArray = generateEnumArray();

    // get schema for the built-in type
    IDataTypeJsonSchema dataTypeSchema = state.getSchema(getDataTypeAdapter());

    // if other values are allowed, we need to make a union of the restriction type
    // and the base
    // built-in type
    ArrayNode ofArray;
    if (allowedValuesCollection.isClosed()) {
      // this restriction is allOf, since both must match
      ofArray = node.putArray("allOf");
    } else {
      // this restriction is anyOf, since any can match
      ofArray = node.putArray("anyOf");
    }

    // add the data type reference
    dataTypeSchema.generateJsonSchemaOrDefinitionRef(ObjectUtils.notNull(ofArray.addObject()), state);

    // add the enumeration
    ofArray.addObject()
        .set("enum", enumArray);
  }

  @SuppressWarnings("PMD.CyclomaticComplexity")
  @NonNull
  private ArrayNode generateEnumArray() {
    // generate a restriction on the built-in type for the enumerated values
    ArrayNode enumArray = JsonNodeFactory.instance.arrayNode();

    for (IAllowedValue allowedValue : allowedValuesCollection.getValues()) {
      switch (getDataTypeAdapter().getJsonRawType()) {
      case STRING:
        enumArray.add(allowedValue.getValue());
        break;
      case BOOLEAN:
        enumArray.add(Boolean.parseBoolean(allowedValue.getValue()));
        break;
      case INTEGER:
        enumArray.add(new BigInteger(allowedValue.getValue())); // NOPMD unavoidable
        break;
      case NUMBER:
        enumArray.add(new BigDecimal(allowedValue.getValue(), DecimalAdapter.mathContext())); // NOPMD unavoidable
        break;
      case ANY:
      case ARRAY:
      case NULL:
      case OBJECT:
        throw new UnsupportedOperationException(getDataTypeAdapter().getJsonRawType().toString());
      }
    }
    return ObjectUtils.notNull(enumArray);
  }
}
