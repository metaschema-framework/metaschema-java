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
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath document node item that is the top of a document-based data model.
 */
public class DocumentNodeItem
    extends AbstractDMNodeItem
    implements IDMDocumentNodeItem {
  @NonNull
  private final RootAssembly root;
  @NonNull
  private final ISource source;

  /**
   * Construct a new node item.
   *
   * @param resource
   *          the Metaschema module instance resource this document is from
   * @param root
   *          the root Metaschema module assembly definition that represents the
   *          root node of this document
   */
  public DocumentNodeItem(
      @NonNull URI resource,
      @NonNull IAssemblyDefinition root) {
    this.root = new RootAssembly(root);
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
  protected String getValueSignature() {
    return null;
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

    public RootAssembly(
        @NonNull IAssemblyDefinition definition) {
      this.definition = definition;
    }

    @Override
    public IEnhancedQName getQName() {
      return ObjectUtils.requireNonNull(definition.getRootQName(), "the definition is expected to have a root QName.");
    }

    @Override
    public IDocumentNodeItem getDocumentNodeItem() {
      return DocumentNodeItem.this;
    }

    @Override
    public IAssemblyDefinition getDefinition() {
      return definition;
    }
  }
}
