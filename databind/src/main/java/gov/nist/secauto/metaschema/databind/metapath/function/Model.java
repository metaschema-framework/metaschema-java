/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModelElement;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class Model {
  @NonNull
  static final IFunction SIGNATURE = IFunction.builder()
      .name("model")
      .namespace(MetapathConstants.NS_METAPATH_FUNCTIONS_EXTENDED)
      .argument(IArgument.builder()
          .name("node")
          .type(INodeItem.type())
          .one()
          .build())
      .returnType(INodeItem.type())
      .focusIndependent()
      .contextIndependent()
      .deterministic()
      .returnZeroOrOne()
      .functionHandler(Model::execute)
      .build();

  private Model() {
    // disable construction
  }

  @SuppressWarnings({ "unused",
      "PMD.OnlyOneReturn" // readability
  })
  @NonNull
  public static ISequence<?> execute(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      IItem focus) {
    assert arguments.size() == 1;
    ISequence<? extends INodeItem> nodeSequence = FunctionUtils.asType(
        ObjectUtils.notNull(arguments.get(0)));

    if (nodeSequence.isEmpty()) {
      return ISequence.empty();
    }

    // always not null, since the first item is required
    INodeItem node = nodeSequence.getFirstItem(true);

    if (!(node instanceof IDefinitionNodeItem)) {
      return ISequence.empty();
    }

    node = getModel((IDefinitionNodeItem<?, ?>) node);
    return ISequence.of(node);
  }

  public static INodeItem getModel(@NonNull IDefinitionNodeItem<?, ?> definitionNodeItem) {
    INamedInstance instance = definitionNodeItem.getInstance();

    INodeItem retval = null;
    if (instance != null) {
      if (instance instanceof IBindingModelElement) {
        retval = ((IBindingModelElement) instance).getSourceNodeItem();
      }
    } else {
      IDefinition definition = definitionNodeItem.getDefinition();
      if (definition instanceof IBindingModelElement) {
        retval = ((IBindingModelElement) definition).getSourceNodeItem();
      }
    }

    return retval;
  }
}
