/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.AbstractResourceResolver;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.ModelDetector.Result;

import org.eclipse.jdt.annotation.NotOwning;
import org.eclipse.jdt.annotation.Owning;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A default implementation of an {@link IBoundLoader}.
 */
public class DefaultBoundLoader
    extends AbstractResourceResolver
    implements IBoundLoader {
  public static final int LOOK_AHEAD_BYTES = 32_768;
  // @NonNull
  // private static final JsonFactory JSON_FACTORY = new JsonFactory();
  // @NonNull
  // private static final XmlFactory XML_FACTORY = new XmlFactory();
  // @NonNull
  // private static final YAMLFactory YAML_FACTORY = new YAMLFactory();

  private FormatDetector formatDetector;

  private ModelDetector modelDetector;

  @NonNull
  private final IBindingContext bindingContext;
  @NonNull
  private final IMutableConfiguration<DeserializationFeature<?>> configuration;

  /**
   * Construct a new loader instance, using the provided {@link IBindingContext}.
   *
   * @param bindingContext
   *          the Module binding context to use to load Java types
   */
  public DefaultBoundLoader(@NonNull IBindingContext bindingContext) {
    this.bindingContext = bindingContext;
    this.configuration = new DefaultConfiguration<>();
  }

  @NonNull
  private IMutableConfiguration<DeserializationFeature<?>> getConfiguration() {
    return configuration;
  }

  @Override
  public boolean isFeatureEnabled(DeserializationFeature<?> feature) {
    return getConfiguration().isFeatureEnabled(feature);
  }

  @Override
  public Map<DeserializationFeature<?>, Object> getFeatureValues() {
    return getConfiguration().getFeatureValues();
  }

  @Override
  public IBoundLoader applyConfiguration(@NonNull IConfiguration<DeserializationFeature<?>> other) {
    getConfiguration().applyConfiguration(other);
    resetDetector();
    return this;
  }

  @SuppressWarnings("PMD.NullAssignment")
  private void resetDetector() {
    // reset the detector
    formatDetector = null;
  }

  @Override
  public IBoundLoader set(DeserializationFeature<?> feature, Object value) {
    getConfiguration().set(feature, value);
    resetDetector();
    return this;
  }

  @Override
  public IBindingContext getBindingContext() {
    return bindingContext;
  }

  @Override
  public Format detectFormat(@NonNull URI uri) throws IOException {
    URI resourceUri = resolve(uri);
    URL resource = resourceUri.toURL();

    try (InputStream is = ObjectUtils.notNull(resource.openStream())) {
      return detectFormat(is).getFormat();
    }
  }

  @Override
  public FormatDetector.Result detectFormat(@NonNull InputStream is) throws IOException {
    return getFormatDetector().detect(is);
  }

  @NonNull
  private FormatDetector getFormatDetector() {
    if (formatDetector == null) {
      formatDetector = new FormatDetector(getConfiguration());
    }
    assert formatDetector != null;
    return formatDetector;
  }

  @NonNull
  private ModelDetector getModelDetector() {
    if (modelDetector == null) {
      modelDetector = new ModelDetector(
          getBindingContext(),
          getConfiguration());
    }
    assert modelDetector != null;
    return modelDetector;
  }

  @Override
  @Owning
  public Result detectModel(@NotOwning InputStream is, Format format) throws IOException {
    return getModelDetector().detect(is, format);
  }

  @Override
  public <CLASS extends IBoundObject> CLASS load(@NonNull URI uri) throws IOException {
    URI resourceUri = resolve(uri);
    URL resource = resourceUri.toURL();

    try (InputStream is = ObjectUtils.notNull(resource.openStream())) {
      return load(is, uri);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  @NonNull
  public <CLASS extends IBoundObject> CLASS load(
      @NotOwning @NonNull InputStream is,
      @NonNull URI documentUri)
      throws IOException {
    FormatDetector.Result formatMatch = getFormatDetector().detect(is);
    Format format = formatMatch.getFormat();

    try (InputStream formatStream = formatMatch.getDataStream()) {
      try (ModelDetector.Result modelMatch = detectModel(formatStream, format)) {

        IDeserializer<?> deserializer = getDeserializer(
            modelMatch.getBoundClass(),
            format,
            getConfiguration());
        try (InputStream modelStream = modelMatch.getDataStream()) {
          return (CLASS) deserializer.deserialize(modelStream, documentUri);
        }
      }
    }
  }

  @Override
  public <CLASS extends IBoundObject> CLASS load(Class<CLASS> clazz, URI uri) throws IOException {
    URI resourceUri = resolve(uri);
    URL resource = resourceUri.toURL();

    try (InputStream is = ObjectUtils.notNull(resource.openStream())) {
      return load(clazz, is, resourceUri);
    }
  }

  @Override
  public <CLASS extends IBoundObject> CLASS load(Class<CLASS> clazz, InputStream is, URI documentUri)
      throws IOException {
    // we cannot close this stream, since it will cause the underlying stream to be
    // closed
    FormatDetector.Result match = getFormatDetector().detect(is);
    Format format = match.getFormat();

    try (InputStream remainingStream = match.getDataStream()) {
      // is autoclosing ok?
      return load(clazz, format, remainingStream, documentUri);
    }
  }

  @Override
  @NonNull
  public <CLASS extends IBoundObject> CLASS load(
      @NonNull Class<CLASS> clazz,
      @NonNull Format format,
      @NonNull InputStream is,
      @NonNull URI documentUri) throws IOException {

    IDeserializer<CLASS> deserializer = getDeserializer(clazz, format, getConfiguration());
    return deserializer.deserialize(is, documentUri);
  }

  @Override
  public IDocumentNodeItem loadAsNodeItem(URI uri) throws IOException {
    URI resourceUri = resolve(uri);
    URL resource = resourceUri.toURL();

    try (InputStream is = ObjectUtils.notNull(resource.openStream())) {
      return loadAsNodeItem(is, resourceUri);
    }
  }

  @NonNull
  private IDocumentNodeItem loadAsNodeItem(@NonNull InputStream is, @NonNull URI documentUri) throws IOException {
    FormatDetector.Result formatMatch = getFormatDetector().detect(is);
    Format format = formatMatch.getFormat();

    try (InputStream formatStream = formatMatch.getDataStream()) {
      return loadAsNodeItem(format, formatStream, documentUri);
    }
  }

  @Override
  public IDocumentNodeItem loadAsNodeItem(Format format, URI uri) throws IOException {
    URI resourceUri = resolve(uri);
    URL resource = resourceUri.toURL();

    try (InputStream is = ObjectUtils.notNull(resource.openStream())) {
      return loadAsNodeItem(format, is, resourceUri);
    }
  }

  @Override
  public IDocumentNodeItem loadAsNodeItem(Format format, InputStream is, URI documentUri)
      throws IOException {
    try (ModelDetector.Result modelMatch = detectModel(is, format)) {

      IDeserializer<?> deserializer = getDeserializer(
          modelMatch.getBoundClass(),
          format,
          getConfiguration());
      try (InputStream modelStream = modelMatch.getDataStream()) {
        return (IDocumentNodeItem) deserializer.deserializeToNodeItem(modelStream, documentUri);
      }
    }
  }

  @NonNull
  private <CLASS extends IBoundObject> IDeserializer<CLASS> getDeserializer(
      @NonNull Class<CLASS> clazz,
      @NonNull Format format,
      @NonNull IConfiguration<DeserializationFeature<?>> config) {
    IDeserializer<CLASS> retval = getBindingContext().newDeserializer(format, clazz);
    retval.applyConfiguration(config);
    return retval;
  }
}
