/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMDocumentNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DocumentImpl implements IDMDocumentNodeItem {
  @NonNull
  private final RootAssembly root;
  @NonNull
  private final IResourceLocation resourceLocation;
  @NonNull
  private final ISource source;

  public DocumentImpl(
      @NonNull URI resource,
      @NonNull IResourceLocation resourceLocation,
      @NonNull IAssemblyDefinition root,
      @NonNull IResourceLocation assemblyLocation) {
    this.root = new RootAssembly(root, assemblyLocation);
    this.resourceLocation = resourceLocation;
    this.source = ISource.externalSource(resource);
  }

  @Override
  public URI getDocumentUri() {
    return ObjectUtils.notNull(source.getSource());
  }

  @Override
  public Collection<? extends IFlagNodeItem> getFlags() {
    // no flags
    return CollectionUtil.emptyList();
  }

  @Override
  public IFlagNodeItem getFlagByName(IEnhancedQName name) {
    // no flags
    return null;
  }

  @Override
  public Collection<? extends List<? extends IModelNodeItem<?, ?>>> getModelItems() {
    return CollectionUtil.singleton(CollectionUtil.singletonList(root));
  }

  @Override
  public List<? extends IModelNodeItem<?, ?>> getModelItemsByName(IEnhancedQName name) {
    return root.getQName().equals(name)
        ? CollectionUtil.singletonList(root)
        : CollectionUtil.emptyList();
  }

  @Override
  public IResourceLocation getLocation() {
    return resourceLocation;
  }

  @Override
  public String stringValue() {
    return "";
  }

  @Override
  public StaticContext getStaticContext() {
    return source.getStaticContext();
  }

  @Override
  public Object getValue() {
    return this;
  }

  @Override
  public String toSignature() {
    return ObjectUtils.notNull(new StringBuilder()
        .append(getType().toSignature())
        .append('\u2ABB')
        .append(getMetapath())
        .append('\u2ABC')
        .toString());
  }

  @Override
  public IDMRootAssemblyNodeItem getRootAssemblyNodeItem() {
    return root;
  }

  private class RootAssembly
      extends AbstractDMAssemblyNodeItem
      implements IDMRootAssemblyNodeItem {
    @NonNull
    private final IAssemblyDefinition definition;
    @NonNull
    private final IResourceLocation resourceLocation;

    public RootAssembly(
        @NonNull IAssemblyDefinition definition,
        @NonNull IResourceLocation location) {
      this.definition = definition;
      this.resourceLocation = location;
    }

    @Override
    public IEnhancedQName getQName() {
      return definition.getRootQName();
    }

    @Override
    public IResourceLocation getLocation() {
      return resourceLocation;
    }

    @Override
    public IDocumentNodeItem getDocumentNodeItem() {
      return DocumentImpl.this;
    }

    @Override
    public IAssemblyDefinition getDefinition() {
      return definition;
    }
  }
}
