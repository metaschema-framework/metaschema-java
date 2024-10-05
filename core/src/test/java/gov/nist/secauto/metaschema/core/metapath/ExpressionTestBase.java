/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.net.URI;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ExpressionTestBase {
  @NonNull
  @RegisterExtension
  public final Mockery context = new JUnit5Mockery();

  /**
   * Get the mocking context.
   *
   * @return the mocking context
   */
  @NonNull
  protected Mockery getContext() {
    return context;
  }

  /**
   * Construct a new dynamic context for testing.
   *
   * @return the dynamic context
   */
  @NonNull
  protected static DynamicContext newDynamicContext() {
    URI baseUri = ObjectUtils.notNull(new File("").getAbsoluteFile().toURI());

    return new DynamicContext(StaticContext.builder()
        .baseUri(baseUri)
        .build());
  }

  /**
   * Get a mocked document node item.
   *
   * @return the mocked node item
   */
  @NonNull
  protected IDocumentNodeItem newDocumentNodeMock() {
    IDocumentNodeItem retval = getContext().mock(IDocumentNodeItem.class);
    assert retval != null;

    getContext().checking(new Expectations() {
      { // NOPMD - intentional
        allowing(retval).getNodeItem();
        will(returnValue(retval));
        allowing(retval).ancestorOrSelf();
        will(returnValue(Stream.of(retval)));
      }
    });

    return retval;
  }

  /**
   * Get a mocked node item.
   *
   * @param mockName
   *          the name of the mocked object
   *
   * @return the mocked node item
   */
  @NonNull
  protected INodeItem newNonDocumentNodeMock(@NonNull String mockName) {
    INodeItem retval = getContext().mock(INodeItem.class, mockName);
    assert retval != null;

    getContext().checking(new Expectations() {
      { // NOPMD - intentional
        allowing(retval).getNodeItem();
        will(returnValue(retval));
        allowing(retval).ancestorOrSelf();
        will(returnValue(Stream.of(retval)));
      }
    });

    return retval;
  }
}
