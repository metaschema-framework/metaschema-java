
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// TODO: Integrate with classes in gov.nist.secauto.metaschema.core.testing
@SuppressWarnings("checkstyle:MissingJavadocMethodCheck")
@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
public class MockNodeItemFactory {

  @NonNull
  private final Mockery context;

  @SuppressWarnings("exports")
  public MockNodeItemFactory(@NonNull Mockery ctx) {
    this.context = ctx;
  }

  protected Mockery getContext() {
    return context;
  }

  @SuppressWarnings("null")
  @NonNull
  protected <T extends INodeItem> T newMock(@NonNull Class<T> clazz, @NonNull String name) {
    String mockName = new StringBuilder()
        .append(clazz.getSimpleName())
        .append('-')
        .append(name)
        .append('-')
        .append(UUID.randomUUID().toString())
        .toString();
    return getContext().mock(clazz, mockName);
  }

  public IDocumentNodeItem document(@NonNull URI documentURI, @NonNull QName rootName,
      @NonNull List<IFlagNodeItem> flags,
      @NonNull List<IModelNodeItem<?, ?>> modelItems) {
    String qname = ObjectUtils.notNull(rootName.toString());
    IDocumentNodeItem document = newMock(IDocumentNodeItem.class, qname);
    IRootAssemblyNodeItem root = newMock(IRootAssemblyNodeItem.class, qname);

    getContext().checking(new Expectations() {
      { // NOPMD - intentional
        allowing(document).modelItems();
        will(returnValue(Stream.of(root)));
        allowing(document).getDocumentUri();
        will(returnValue(documentURI));
        allowing(document).getNodeItem();
        will(returnValue(document));
        allowing(document).getParentNodeItem();
        will(returnValue(null));
        allowing(document).ancestorOrSelf();
        will(returnValue(Stream.of(document)));

        allowing(root).getQName();
        will(returnValue(rootName));
        allowing(root).getNodeItem();
        will(returnValue(root));
        allowing(root).getDocumentNodeItem();
        will(returnValue(document));
      }
    });

    handleChildren(document, CollectionUtil.emptyList(), CollectionUtil.singletonList(root));
    handleChildren(root, flags, modelItems);

    return document;
  }

  @SuppressWarnings("null")
  protected <T extends INodeItem> void handleChildren(
      @NonNull T item,
      @NonNull List<IFlagNodeItem> flags,
      @NonNull List<IModelNodeItem<?, ?>> modelItems) {
    getContext().checking(new Expectations() {
      { // NOPMD - intentional
        allowing(item).getFlags();
        will(returnValue(flags));
        flags.forEach(flag -> {
          // handle each flag child
          allowing(item).getFlagByName(with(equal(flag.getQName())));
          will(returnValue(flag));
          // link parent
          allowing(flag).getParentNodeItem();
          will(returnValue(item));
        });

        Map<QName, List<IModelNodeItem<?, ?>>> modelItemsMap = toModelItemsMap(modelItems);
        allowing(item).getModelItems();
        will(returnValue(modelItemsMap.values()));
        modelItemsMap.entrySet().forEach(entry -> {
          allowing(item).getModelItemsByName(with(equal(entry.getKey())));
          will(returnValue(entry.getValue()));

          AtomicInteger position = new AtomicInteger(1);
          entry.getValue().forEach(modelItem -> {
            // handle each model item child
            // link parent
            allowing(modelItem).getParentNodeItem();
            will(returnValue(item));

            // establish position
            allowing(modelItem).getPosition();
            will(returnValue(position.getAndIncrement()));
          });
        });

        allowing(item).modelItems();
        will(new Action() {

          @Override
          public void describeTo(Description description) {
            description.appendText("returns stream");
          }

          @Override
          public Object invoke(Invocation invocation) {
            return modelItemsMap.values().stream()
                .flatMap(children -> children.stream());
          }
        });
      }
    });
  }

  @SuppressWarnings("static-method")
  @NonNull
  private Map<QName, List<IModelNodeItem<?, ?>>>
      toModelItemsMap(@NonNull List<IModelNodeItem<?, ?>> modelItems) {

    Map<QName, List<IModelNodeItem<?, ?>>> retval = new LinkedHashMap<>(); // NOPMD - intentional
    for (IModelNodeItem<?, ?> item : modelItems) {
      QName name = item.getQName();
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
  public IFlagNodeItem flag(@NonNull QName name, @NonNull IAnyAtomicItem value) {
    IFlagNodeItem retval = newMock(IFlagNodeItem.class, ObjectUtils.notNull(name.toString()));

    getContext().checking(new Expectations() {
      { // NOPMD - intentional
        allowing(retval).getQName();
        will(returnValue(name));

        allowing(retval).hasValue();
        will(returnValue(true));

        allowing(retval).toAtomicItem();
        will(returnValue(value));

        allowing(retval).getNodeItem();
        will(returnValue(retval));
      }
    });

    handleChildren(retval, CollectionUtil.emptyList(), CollectionUtil.emptyList());

    return retval;
  }

  @NonNull
  public IFieldNodeItem field(@NonNull QName name, @NonNull IAnyAtomicItem value) {
    return field(name, value, CollectionUtil.emptyList());
  }

  @NonNull
  public IFieldNodeItem field(
      @NonNull QName name,
      @NonNull IAnyAtomicItem value,
      @NonNull List<IFlagNodeItem> flags) {
    IFieldNodeItem retval = newMock(IFieldNodeItem.class, ObjectUtils.notNull(name.toString()));

    getContext().checking(new Expectations() {
      { // NOPMD - intentional
        allowing(retval).getQName();
        will(returnValue(name));

        allowing(retval).hasValue();
        will(returnValue(true));

        allowing(retval).toAtomicItem();
        will(returnValue(value));

        allowing(retval).getNodeItem();
        will(returnValue(retval));
      }
    });

    handleChildren(retval, flags, CollectionUtil.emptyList());
    return retval;
  }

  @NonNull
  public IAssemblyNodeItem assembly(
      @NonNull QName name,
      @NonNull List<IFlagNodeItem> flags,
      @NonNull List<IModelNodeItem<?, ?>> modelItems) {
    IAssemblyNodeItem retval = newMock(IAssemblyNodeItem.class, ObjectUtils.notNull(name.toString()));

    getContext().checking(new Expectations() {
      { // NOPMD - intentional
        allowing(retval).getQName();
        will(returnValue(name));

        allowing(retval).getNodeItem();
        will(returnValue(retval));
      }
    });

    handleChildren(retval, flags, modelItems);

    return retval;
  }

}
