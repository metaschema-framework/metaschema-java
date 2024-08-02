/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.AbstractGlobalDefinition.NameInitializer;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public abstract class AbstractNamedInstance<
    PARENT extends IContainer>
    extends AbstractInstance<PARENT>
    implements INamedInstance {
  @NonNull
  private final Lazy<QName> qname;
  @NonNull
  private final Lazy<QName> definitionQName;

  /**
   * Construct a new instance.
   *
   * @param parent
   *          the parent containing the instance
   * @param initializer
   *          used to generate the instance qualified name
   */
  protected AbstractNamedInstance(@NonNull PARENT parent, @NonNull NameInitializer initializer) {
    super(parent);
    this.qname = ObjectUtils.notNull(Lazy.lazy(() -> initializer.apply(getEffectiveName())));
    this.definitionQName = ObjectUtils.notNull(Lazy.lazy(() -> initializer.apply(getName())));
  }

  @SuppressWarnings("null")
  @Override
  public final QName getXmlQName() {
    return qname.get();
  }

  @SuppressWarnings("null")
  @Override
  public final QName getReferencedDefinitionQName() {
    return definitionQName.get();
  }
}
