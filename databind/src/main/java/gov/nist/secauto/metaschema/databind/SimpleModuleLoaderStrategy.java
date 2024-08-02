/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.AbstractBoundModule;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaField;
import gov.nist.secauto.metaschema.databind.model.impl.DefinitionAssembly;
import gov.nist.secauto.metaschema.databind.model.impl.DefinitionField;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class SimpleModuleLoaderStrategy implements IBindingContext.IModuleLoaderStrategy {
  @NonNull
  private final IBindingContext bindingContext;
  @NonNull
  private final Map<Class<?>, IBoundModule> modulesByClass = new ConcurrentHashMap<>();
  @NonNull
  private final Map<Class<?>, IBoundDefinitionModelComplex> definitionsByClass = new ConcurrentHashMap<>();

  protected SimpleModuleLoaderStrategy(@NonNull IBindingContext bindingContext) {
    this.bindingContext = bindingContext;
  }

  @NonNull
  private IBindingContext getBindingContext() {
    return bindingContext;
  }

  @Override
  public IBoundModule loadModule(@NonNull Class<? extends IBoundModule> clazz) {
    IBoundModule retval;
    synchronized (this) {
      retval = modulesByClass.get(clazz);
      if (retval == null) {
        retval = AbstractBoundModule.createInstance(clazz, getBindingContext());
        modulesByClass.put(clazz, retval);
      }
    }
    return ObjectUtils.notNull(retval);
  }

  @Override
  public IBoundDefinitionModelComplex getBoundDefinitionForClass(@NonNull Class<? extends IBoundObject> clazz) {
    IBoundDefinitionModelComplex retval;
    synchronized (this) {
      retval = definitionsByClass.get(clazz);
      if (retval == null) {
        retval = newBoundDefinition(clazz);
        if (retval != null) {
          definitionsByClass.put(clazz, retval);
        }
      }
    }
    return retval;
  }

  @Nullable
  private IBoundDefinitionModelComplex newBoundDefinition(@NonNull Class<? extends IBoundObject> clazz) {
    IBoundDefinitionModelComplex retval = null;
    if (clazz.isAnnotationPresent(MetaschemaAssembly.class)) {
      retval = DefinitionAssembly.newInstance(clazz, getBindingContext());
    } else if (clazz.isAnnotationPresent(MetaschemaField.class)) {
      retval = DefinitionField.newInstance(clazz, getBindingContext());
    }
    return retval;
  }
}
