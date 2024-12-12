
package gov.nist.secauto.metaschema.core.metapath.item.node;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.model.mocking.AbstractMockitoFactory;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// TODO: Integrate with classes in gov.nist.secauto.metaschema.core.testing
@SuppressWarnings("checkstyle:MissingJavadocMethodCheck")
@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
public class MockNodeItemFactory
    extends AbstractMockitoFactory {
  @NonNull
  public IDocumentNodeItem document(
      URI documentURI,
      IEnhancedQName rootName,
      List<IFlagNodeItem> flags,
      List<IModelNodeItem<?, ?>> modelItems) {
    String qname = ObjectUtils.requireNonNull(rootName.toString());
    IDocumentNodeItem document = mock(IDocumentNodeItem.class, qname);
    IRootAssemblyNodeItem root = mock(IRootAssemblyNodeItem.class, qname);
    IAssemblyDefinition definition = mock(IAssemblyDefinition.class, qname);

    // doAnswer(invocation -> Stream.of(root)).when(document).modelItems();
    doReturn(root).when(document).getRootAssemblyNodeItem();
    doReturn(Collections.singletonList(root)).when(document).getModelItems();
    doReturn(documentURI).when(document).getDocumentUri();
    doReturn(documentURI).when(document).getBaseUri();
    doReturn(NodeItemKind.DOCUMENT).when(document).getNodeItemKind();

    doReturn(rootName).when(root).getQName();
    doReturn(document).when(root).getDocumentNodeItem();
    doReturn(rootName.toString()).when(root).toString();

    doReturn(definition).when(root).getDefinition();
    doReturn(rootName).when(definition).getDefinitionQName();

    handleChildren(document, CollectionUtil.emptyList(), CollectionUtil.singletonList(root));
    handleChildren(root, flags, modelItems);

    return document;
  }

  @SuppressWarnings("null")
  protected <T extends INodeItem> void handleChildren(
      @NonNull T item,
      List<IFlagNodeItem> flags,
      List<IModelNodeItem<?, ?>> modelItems) {

    doReturn(flags).when(item).getFlags();

    ObjectUtils.requireNonNull(flags).forEach(flag -> {
      assert flag != null;

      // handle each flag child
      IEnhancedQName qname = flag.getQName();
      doReturn(flag).when(item).getFlagByName(qname);
      // link parent
      doReturn(item).when(flag).getParentNodeItem();
      doReturn(item).when(flag).getParentContentNodeItem();
    });

    Map<IEnhancedQName, List<IModelNodeItem<?, ?>>> modelItemsMap = toModelItemsMap(modelItems);

    doReturn(modelItemsMap.values()).when(item).getModelItems();

    ObjectUtils.requireNonNull(modelItemsMap).entrySet().forEach(entry -> {
      assert entry != null;

      doReturn(entry.getValue()).when(item).getModelItemsByName(eq(entry.getKey()));

      AtomicInteger position = new AtomicInteger(1);
      entry.getValue().forEach(modelItem -> {
        // handle each model item child
        // link parent
        doReturn(item).when(modelItem).getParentNodeItem();
        doReturn(item instanceof IDocumentNodeItem ? null : item).when(modelItem).getParentContentNodeItem();

        // establish position
        doReturn(position.getAndIncrement()).when(modelItem).getPosition();
      });
    });
  }

  @SuppressWarnings("static-method")
  @NonNull
  private Map<IEnhancedQName, List<IModelNodeItem<?, ?>>> toModelItemsMap(List<IModelNodeItem<?, ?>> modelItems) {

    Map<IEnhancedQName, List<IModelNodeItem<?, ?>>> retval = new LinkedHashMap<>(); // NOPMD - intentional
    for (IModelNodeItem<?, ?> item : ObjectUtils.requireNonNull(modelItems)) {
      IEnhancedQName name = item.getQName();
      List<IModelNodeItem<?, ?>> namedItems = retval.get(name);
      if (namedItems == null) {
        namedItems = new LinkedList<>(); // NOPMD - intentional
        retval.put(name, namedItems);
      }
      namedItems.add(item);
    }
    return CollectionUtil.unmodifiableMap(retval);
  }

  @NonNull
  public IFlagNodeItem flag(@NonNull IEnhancedQName name, @NonNull IAnyAtomicItem value) {
    IFlagNodeItem flag = mock(IFlagNodeItem.class, ObjectUtils.notNull(name.toString()));
    IFlagDefinition definition = mock(IFlagDefinition.class, ObjectUtils.notNull(name.toString()));

    doReturn(name).when(flag).getQName();
    doReturn(true).when(flag).hasValue();
    doReturn(value).when(flag).toAtomicItem();
    doReturn(name.toString()).when(flag).toString();

    doReturn(definition).when(flag).getDefinition();
    doReturn(name).when(definition).getDefinitionQName();
    doReturn(value.getJavaTypeAdapter()).when(definition).getJavaTypeAdapter();

    handleChildren(flag, CollectionUtil.emptyList(), CollectionUtil.emptyList());

    return flag;
  }

  @NonNull
  public IFieldNodeItem field(@NonNull IEnhancedQName name, @NonNull IAnyAtomicItem value) {
    return field(name, value, CollectionUtil.emptyList());
  }

  @NonNull
  public IFieldNodeItem field(
      @NonNull IEnhancedQName name,
      @NonNull IAnyAtomicItem value,
      List<IFlagNodeItem> flags) {
    IFieldNodeItem field = mock(IFieldNodeItem.class, ObjectUtils.notNull(name.toString()));
    IFieldDefinition definition = mock(IFieldDefinition.class, ObjectUtils.notNull(name.toString()));

    doReturn(name).when(field).getQName();
    doReturn(true).when(field).hasValue();
    doReturn(value).when(field).toAtomicItem();
    doReturn(name.toString()).when(field).toString();

    doReturn(definition).when(field).getDefinition();
    doReturn(name).when(definition).getDefinitionQName();
    doReturn(value.getJavaTypeAdapter()).when(definition).getJavaTypeAdapter();

    handleChildren(field, ObjectUtils.requireNonNull(flags), CollectionUtil.emptyList());
    return field;
  }

  @NonNull
  public IAssemblyNodeItem assembly(
      @NonNull IEnhancedQName name,
      List<IFlagNodeItem> flags,
      List<IModelNodeItem<?, ?>> modelItems) {
    IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class, ObjectUtils.notNull(name.toString()));
    IAssemblyDefinition definition = mock(IAssemblyDefinition.class, ObjectUtils.notNull(name.toString()));

    doReturn(name).when(assembly).getQName();
    doReturn(false).when(assembly).hasValue();
    doReturn(name.toString()).when(assembly).toString();

    doReturn(definition).when(assembly).getDefinition();
    doReturn(name).when(definition).getDefinitionQName();

    handleChildren(assembly, ObjectUtils.requireNonNull(flags), ObjectUtils.requireNonNull(modelItems));

    return assembly;
  }

}
