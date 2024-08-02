/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.AbstractLoader;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.AssemblyConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.AssemblyTargetedConstraints;
import gov.nist.secauto.metaschema.core.model.constraint.DefaultConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.DefaultScopedContraints;
import gov.nist.secauto.metaschema.core.model.constraint.FieldTargetedConstraints;
import gov.nist.secauto.metaschema.core.model.constraint.FlagTargetedConstraints;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.IModelConstrained;
import gov.nist.secauto.metaschema.core.model.constraint.IScopedContraints;
import gov.nist.secauto.metaschema.core.model.constraint.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.ITargetedConstraints;
import gov.nist.secauto.metaschema.core.model.constraint.IValueConstrained;
import gov.nist.secauto.metaschema.core.model.constraint.ValueConstraintSet;
import gov.nist.secauto.metaschema.core.model.xml.impl.ConstraintXmlSupport;
import gov.nist.secauto.metaschema.core.model.xml.impl.XmlObjectParser;
import gov.nist.secauto.metaschema.core.model.xml.impl.XmlObjectParser.Handler;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.METASCHEMACONSTRAINTSDocument;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.METASCHEMACONSTRAINTSDocument.METASCHEMACONSTRAINTS.Scope;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.values.XmlValueNotSupportedException;

import java.io.IOException;
import java.net.URI;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides methods to load a constraint set expressed in XML.
 * <p>
 * Loaded constraint instances are cached to avoid the need to load them for
 * every use. Any constraint set imported is also loaded and cached
 * automatically.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class XmlConstraintLoader
    extends AbstractLoader<List<IConstraintSet>>
    implements IConstraintLoader {

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private static final Map<QName,
      Handler<Pair<ISource, List<ITargetedConstraints>>>> SCOPE_OBJECT_MAPPING = ObjectUtils.notNull(
          Map.ofEntries(
              Map.entry(new QName(IModule.XML_NAMESPACE, "assembly"),
                  XmlConstraintLoader::handleScopedAssembly),
              Map.entry(new QName(IModule.XML_NAMESPACE, "field"),
                  XmlConstraintLoader::handleScopedField),
              Map.entry(new QName(IModule.XML_NAMESPACE, "flag"),
                  XmlConstraintLoader::handleScopedFlag)));

  @NonNull
  private static final XmlObjectParser<Pair<ISource, List<ITargetedConstraints>>> SCOPE_PARSER
      = new XmlObjectParser<>(SCOPE_OBJECT_MAPPING) {

        @Override
        protected Handler<Pair<ISource, List<ITargetedConstraints>>> identifyHandler(XmlCursor cursor, XmlObject obj) {
          Handler<Pair<ISource, List<ITargetedConstraints>>> retval;
          if (obj instanceof Scope.Assembly) {
            retval = XmlConstraintLoader::handleScopedAssembly;
          } else if (obj instanceof Scope.Field) {
            retval = XmlConstraintLoader::handleScopedField;
          } else if (obj instanceof Scope.Flag) {
            retval = XmlConstraintLoader::handleScopedFlag;
          } else {
            throw new IllegalStateException(String.format("Unhandled element type '%s'.", obj.getClass().getName()));
          }
          return retval;
        }

      };

  @Override
  protected List<IConstraintSet> parseResource(@NonNull URI resource, @NonNull Deque<URI> visitedResources)
      throws IOException {

    // parse this metaschema
    METASCHEMACONSTRAINTSDocument xmlObject = parseConstraintSet(resource);

    // now check if this constraint set imports other constraint sets
    int size = xmlObject.getMETASCHEMACONSTRAINTS().sizeOfImportArray();
    Set<IConstraintSet> importedConstraints;
    if (size == 0) {
      importedConstraints = CollectionUtil.emptySet();
    } else {
      try {
        importedConstraints = new LinkedHashSet<>();
        for (METASCHEMACONSTRAINTSDocument.METASCHEMACONSTRAINTS.Import imported : xmlObject.getMETASCHEMACONSTRAINTS()
            .getImportList()) {
          URI importedResource = URI.create(imported.getHref());
          importedResource = ObjectUtils.notNull(resource.resolve(importedResource));
          importedConstraints.addAll(loadInternal(importedResource, visitedResources));
        }
      } catch (MetaschemaException ex) {
        throw new IOException(ex);
      }
    }

    // now create this constraint set
    return CollectionUtil.singletonList(new DefaultConstraintSet(
        resource,
        parseScopedConstraints(xmlObject, resource),
        importedConstraints));
  }

  /**
   * Parse the provided XML resource as a Metaschema constraints.
   *
   * @param resource
   *          the resource to parse
   * @return the XMLBeans representation of the Metaschema contraints
   * @throws IOException
   *           if a parsing error occurred
   */
  @NonNull
  private static METASCHEMACONSTRAINTSDocument parseConstraintSet(@NonNull URI resource) throws IOException {
    try {
      XmlOptions options = new XmlOptions();
      options.setBaseURI(resource);
      options.setLoadLineNumbers();
      return ObjectUtils.notNull(METASCHEMACONSTRAINTSDocument.Factory.parse(resource.toURL(), options));
    } catch (XmlException ex) {
      throw new IOException(ex);
    }
  }

  /**
   * Parse individual constraint definitions from the provided XMLBeans object.
   *
   * @param xmlObject
   *          the XMLBeans object
   * @param resource
   *          the resource containing the constraint content
   * @return the scoped constraint definitions
   */
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // intentional
  @NonNull
  protected List<IScopedContraints> parseScopedConstraints(
      @NonNull METASCHEMACONSTRAINTSDocument xmlObject,
      @NonNull URI resource) {
    List<IScopedContraints> scopedConstraints = new LinkedList<>();

    StaticContext.Builder builder = StaticContext.builder()
        .baseUri(resource);

    METASCHEMACONSTRAINTSDocument.METASCHEMACONSTRAINTS constraints = xmlObject.getMETASCHEMACONSTRAINTS();

    constraints.getNamespaceBindingList().stream()
        .forEach(binding -> builder.namespace(
            ObjectUtils.notNull(binding.getPrefix()), ObjectUtils.notNull(binding.getUri())));

    builder.useWildcardWhenNamespaceNotDefaulted(true);

    ISource source = ISource.externalSource(builder.build());

    for (Scope scope : constraints.getScopeList()) {
      assert scope != null;

      List<ITargetedConstraints> targetedConstraints = new LinkedList<>(); // NOPMD - intentional
      try {
        SCOPE_PARSER.parse(scope, Pair.of(source, targetedConstraints));
      } catch (MetapathException | XmlValueNotSupportedException ex) {
        if (ex.getCause() instanceof MetapathException) {
          throw new MetapathException(
              String.format("Unable to compile a Metapath in '%s'. %s",
                  source.getSource(),
                  ex.getLocalizedMessage()),
              ex);
        }
        throw ex;
      }

      URI namespace = ObjectUtils.notNull(URI.create(scope.getMetaschemaNamespace()));
      String shortName = ObjectUtils.requireNonNull(scope.getMetaschemaShortName());

      scopedConstraints.add(new DefaultScopedContraints(
          namespace,
          shortName,
          CollectionUtil.unmodifiableList(targetedConstraints)));
    }
    return CollectionUtil.unmodifiableList(scopedConstraints);
  }

  private static void handleScopedAssembly( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<ISource, List<ITargetedConstraints>> state) {
    Scope.Assembly assembly = (Scope.Assembly) obj;

    IModelConstrained constraints = new AssemblyConstraintSet();
    ConstraintXmlSupport.parse(constraints, assembly, ObjectUtils.notNull(state.getLeft()));

    state.getRight().add(new AssemblyTargetedConstraints(
        ObjectUtils.requireNonNull(assembly.getTarget()),
        constraints));
  }

  private static void handleScopedField( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<ISource, List<ITargetedConstraints>> state) {
    Scope.Field field = (Scope.Field) obj;

    IValueConstrained constraints = new ValueConstraintSet();
    ConstraintXmlSupport.parse(constraints, field, ObjectUtils.notNull(state.getLeft()));

    state.getRight().add(new FieldTargetedConstraints(
        ObjectUtils.requireNonNull(field.getTarget()),
        constraints));
  }

  private static void handleScopedFlag( // NOPMD false positive
      @NonNull XmlObject obj,
      Pair<ISource, List<ITargetedConstraints>> state) {
    Scope.Flag flag = (Scope.Flag) obj;

    IValueConstrained constraints = new ValueConstraintSet();
    ConstraintXmlSupport.parse(constraints, flag, ObjectUtils.notNull(state.getLeft()));

    state.getRight().add(new FlagTargetedConstraints(
        ObjectUtils.requireNonNull(flag.getTarget()),
        constraints));
  }
}
