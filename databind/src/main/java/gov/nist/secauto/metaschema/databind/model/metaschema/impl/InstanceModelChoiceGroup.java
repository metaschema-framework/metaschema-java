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

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.AbstractChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.IGroupAs;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.JsonKey;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public class InstanceModelChoiceGroup
    extends AbstractChoiceGroupInstance<
        IAssemblyDefinition,
        INamedModelInstanceGrouped,
        IFieldInstanceGrouped,
        IAssemblyInstanceGrouped>
    implements IFeatureInstanceModelGroupAs {
  @NonNull
  private final AssemblyModel.ChoiceGroup binding;
  @NonNull
  private final IGroupAs groupAs;
  @NonNull
  private final Lazy<IContainerModelSupport<
      INamedModelInstanceGrouped,
      INamedModelInstanceGrouped,
      IFieldInstanceGrouped,
      IAssemblyInstanceGrouped>> modelContainer;
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;

  public InstanceModelChoiceGroup(
      @NonNull AssemblyModel.ChoiceGroup binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IAssemblyDefinition parent,
      @NonNull INodeItemFactory nodeItemFactory) {
    super(parent);
    this.binding = binding;
    this.groupAs = ModelSupport.groupAs(binding.getGroupAs(), parent.getContainingModule());
    this.modelContainer = ObjectUtils.notNull(Lazy.lazy(() -> ChoiceGroupModelContainerSupport.of(
        binding,
        bindingInstance,
        this,
        nodeItemFactory)));
    this.boundNodeItem = ObjectUtils.notNull(
        Lazy.lazy(() -> (IAssemblyNodeItem) ObjectUtils.notNull(getContainingDefinition().getNodeItem())
            .getModelItemsByName(bindingInstance.getXmlQName())
            .get(position)));
  }

  @NonNull
  protected AssemblyModel.ChoiceGroup getBinding() {
    return binding;
  }

  @Override
  public IContainerModelSupport<
      INamedModelInstanceGrouped,
      INamedModelInstanceGrouped,
      IFieldInstanceGrouped,
      IAssemblyInstanceGrouped> getModelContainer() {
    return ObjectUtils.notNull(modelContainer.get());
  }

  @Override
  public IGroupAs getGroupAs() {
    return groupAs;
  }

  @Override
  public IAssemblyNodeItem getNodeItem() {
    return boundNodeItem.get();
  }

  // ---------------------------------------
  // - Start binding driven code - CPD-OFF -
  // ---------------------------------------

  @Override
  public int getMinOccurs() {
    BigInteger min = getBinding().getMinOccurs();
    return min == null ? DEFAULT_GROUP_AS_MIN_OCCURS : min.intValueExact();
  }

  @Override
  public int getMaxOccurs() {
    String max = getBinding().getMaxOccurs();
    return max == null ? DEFAULT_GROUP_AS_MAX_OCCURS : ModelSupport.maxOccurs(max);
  }

  @Override
  public IAssemblyDefinition getOwningDefinition() {
    return getParentContainer();
  }

  @Override
  public String getJsonDiscriminatorProperty() {
    String discriminator = getBinding().getDiscriminator();
    return discriminator == null ? DEFAULT_JSON_DISCRIMINATOR_PROPERTY_NAME : discriminator;
  }

  @Override
  public String getJsonKeyFlagInstanceName() {
    JsonKey jsonKey = getBinding().getJsonKey();
    return jsonKey == null ? null : jsonKey.getFlagRef();
  }
}
