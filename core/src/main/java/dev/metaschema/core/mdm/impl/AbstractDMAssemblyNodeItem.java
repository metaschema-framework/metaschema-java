/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.mdm.impl;

import dev.metaschema.core.mdm.IDMAssemblyNodeItem;
import dev.metaschema.core.mdm.IDMFieldNodeItem;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstance;
import dev.metaschema.core.model.IFieldInstance;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This abstract Metapath assmebly node item implementation supports creating a
 * Metaschema module-based data model.
 */
public abstract class AbstractDMAssemblyNodeItem
    extends AbstractDMModelNodeItem<IAssemblyDefinition, IAssemblyInstance>
    implements IDMAssemblyNodeItem {
  @NonNull
  private final Map<IEnhancedQName, List<IDMModelNodeItem<?, ?>>> modelItems
      = new ConcurrentHashMap<>();

  /**
   * Construct a new node item.
   */
  protected AbstractDMAssemblyNodeItem() {
    // only allow extending classes to create instances
  }

  @Override
  public String stringValue() {
    return "";
  }

  @Override
  protected String getValueSignature() {
    return "";
  }

  @Override
  public Collection<List<IDMModelNodeItem<?, ?>>> getModelItems() {
    return ObjectUtils.notNull(modelItems.values());
  }

  @Override
  public List<? extends IDMModelNodeItem<?, ?>> getModelItemsByName(IEnhancedQName name) {
    List<? extends IDMModelNodeItem<?, ?>> retval = modelItems.get(name);
    return retval == null ? CollectionUtil.emptyList() : retval;
  }

  @Override
  public IDMFieldNodeItem newField(IFieldInstance instance, IAnyAtomicItem value) {
    List<IDMModelNodeItem<?, ?>> result = modelItems.computeIfAbsent(
        instance.getQName(),
        name -> Collections.synchronizedList(new LinkedList<>()));
    IDMFieldNodeItem field = new ChildFieldNodeItem(instance, this, value);
    result.add(field);
    return field;
  }

  @Override
  public IDMAssemblyNodeItem newAssembly(IAssemblyInstance instance) {
    List<IDMModelNodeItem<?, ?>> result = modelItems.computeIfAbsent(
        instance.getQName(),
        name -> Collections.synchronizedList(new LinkedList<>()));
    IDMAssemblyNodeItem assembly = new ChildAssemblyNodeItem(instance, this);
    result.add(assembly);
    return assembly;
  }
}
