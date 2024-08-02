/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.constraint.DefaultConstraintValidator;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintValidationHandler;
import gov.nist.secauto.metaschema.core.model.constraint.LoggingConstraintValidationHandler;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The base class of all format-specific deserializers.
 *
 * @param <CLASS>
 *          the bound class to deserialize to
 */
public abstract class AbstractDeserializer<CLASS extends IBoundObject>
    extends AbstractSerializationBase<DeserializationFeature<?>>
    implements IDeserializer<CLASS> {

  private IConstraintValidationHandler constraintValidationHandler;

  /**
   * Construct a new deserializer.
   *
   * @param definition
   *          the bound class information for the Java type this deserializer is
   *          operating on
   */
  protected AbstractDeserializer(@NonNull IBoundDefinitionModelAssembly definition) {
    super(definition);
  }

  /**
   * Get the constraint validation handler configured for this deserializer, which
   * will be used to validate loaded data.
   *
   * @return the deserializer
   */
  @Override
  @NonNull
  public IConstraintValidationHandler getConstraintValidationHandler() {
    synchronized (this) {
      if (constraintValidationHandler == null) {
        constraintValidationHandler = new LoggingConstraintValidationHandler();
      }
      return ObjectUtils.notNull(constraintValidationHandler);
    }
  }

  @Override
  public void setConstraintValidationHandler(@NonNull IConstraintValidationHandler constraintValidationHandler) {
    synchronized (this) {
      this.constraintValidationHandler = constraintValidationHandler;
    }
  }

  @Override
  public INodeItem deserializeToNodeItem(Reader reader, URI documentUri) throws IOException {

    INodeItem nodeItem;
    try {
      nodeItem = deserializeToNodeItemInternal(reader, documentUri);
    } catch (Exception ex) { // NOPMD - this is intentional
      throw new IOException(ex);
    }

    if (isValidating()) {
      validate(nodeItem);
    }
    return nodeItem;
  }

  /**
   * This abstract method delegates parsing to the concrete implementation.
   *
   * @param reader
   *          the reader instance to read data from
   * @param documentUri
   *          the URI of the document that is being read
   * @return a new node item containing the read contents
   * @throws IOException
   *           if an error occurred while reading data from the stream
   */
  @NonNull
  protected abstract INodeItem deserializeToNodeItemInternal(@NonNull Reader reader, @NonNull URI documentUri)
      throws IOException;

  @Override
  public final CLASS deserializeToValue(Reader reader, URI documentUri) throws IOException {
    CLASS retval;

    if (isValidating()) {
      INodeItem nodeItem = deserializeToNodeItemInternal(reader, documentUri);
      validate(nodeItem);
      retval = ObjectUtils.asType(ObjectUtils.requireNonNull(nodeItem.getValue()));
    } else {
      retval = deserializeToValueInternal(reader, documentUri);
    }
    return retval;
  }

  private void validate(@NonNull INodeItem nodeItem) {
    IDefinitionNodeItem<?, ?> definitionNodeItem;
    if (nodeItem instanceof IDocumentNodeItem) {
      definitionNodeItem = ((IDocumentNodeItem) nodeItem).getRootAssemblyNodeItem();
    } else if (nodeItem instanceof IDefinitionNodeItem) {
      definitionNodeItem = (IDefinitionNodeItem<?, ?>) nodeItem;
    } else {
      throw new UnsupportedOperationException(String.format(
          "The node item type '%s' is not supported for validation.",
          nodeItem.getClass().getName()));
    }

    DynamicContext dynamicContext = new DynamicContext(nodeItem.getStaticContext());
    dynamicContext.setDocumentLoader(getBindingContext().newBoundLoader());
    DefaultConstraintValidator validator = new DefaultConstraintValidator(getConstraintValidationHandler());
    validator.validate(definitionNodeItem, dynamicContext);
    validator.finalizeValidation(dynamicContext);
  }

  @NonNull
  protected abstract CLASS deserializeToValueInternal(@NonNull Reader reader, @NonNull URI documentUri)
      throws IOException;

  @Override
  public IDeserializer<CLASS> enableFeature(DeserializationFeature<?> feature) {
    return set(feature, true);
  }

  @Override
  public IDeserializer<CLASS> disableFeature(DeserializationFeature<?> feature) {
    return set(feature, false);
  }

  @Override
  public IDeserializer<CLASS> applyConfiguration(
      @NonNull IConfiguration<DeserializationFeature<?>> other) {
    IMutableConfiguration<DeserializationFeature<?>> config = getConfiguration();
    config.applyConfiguration(other);
    configurationChanged(config);
    return this;
  }

  @Override
  public IDeserializer<CLASS> set(DeserializationFeature<?> feature, Object value) {
    IMutableConfiguration<DeserializationFeature<?>> config = getConfiguration();
    config.set(feature, value);
    configurationChanged(config);
    return this;
  }
}
