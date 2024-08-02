/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractModelInstanceWriteHandler<ITEM>
    implements IModelInstanceWriteHandler<ITEM> {
  @NonNull
  private final IBoundInstanceModel<ITEM> instance;

  public AbstractModelInstanceWriteHandler(@NonNull IBoundInstanceModel<ITEM> instance) {
    this.instance = instance;
  }

  /**
   * Get the associated instance.
   *
   * @return the instance
   */
  public IBoundInstanceModel<ITEM> getInstance() {
    return instance;
  }

  /**
   * Get the collection information.
   *
   * @return the info
   */
  @NonNull
  public IModelInstanceCollectionInfo<ITEM> getCollectionInfo() {
    return instance.getCollectionInfo();
  }

  @Override
  public void writeList(List<ITEM> items) throws IOException {
    writeCollection(items);
  }

  @Override
  public void writeMap(Map<String, ITEM> items) throws IOException {
    writeCollection(ObjectUtils.notNull(items.values()));
  }

  private void writeCollection(@NonNull Collection<ITEM> items) throws IOException {
    for (ITEM item : items) {
      writeItem(ObjectUtils.requireNonNull(item));
    }
  }
}
