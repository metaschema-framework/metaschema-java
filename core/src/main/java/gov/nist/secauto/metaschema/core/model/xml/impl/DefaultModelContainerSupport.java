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

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IModelInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultModelContainerSupport
    implements IStandardModelContainerSupport {

  @NonNull
  private final List<IModelInstance> modelInstances = new LinkedList<>();
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private final Map<String, INamedModelInstance> namedModelInstances = new LinkedHashMap<>();
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private final Map<String, IFieldInstance> fieldInstances = new LinkedHashMap<>();
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private final Map<String, IAssemblyInstance> assemblyInstances = new LinkedHashMap<>();

  /**
   * Get a listing of all model instances.
   *
   * @return the listing
   */
  @Override
  @NonNull
  public List<IModelInstance> getModelInstances() {
    return modelInstances;
  }

  /**
   * Get a mapping of all named model instances, mapped from their effective name
   * to the instance.
   *
   * @return the mapping
   */
  @Override
  @NonNull
  public Map<String, INamedModelInstance> getNamedModelInstanceMap() {
    return namedModelInstances;
  }

  /**
   * Get a mapping of all field instances, mapped from their effective name to the
   * instance.
   *
   * @return the mapping
   */
  @Override
  @NonNull
  public Map<String, IFieldInstance> getFieldInstanceMap() {
    return fieldInstances;
  }

  /**
   * Get a mapping of all assembly instances, mapped from their effective name to
   * the instance.
   *
   * @return the mapping
   */
  @Override
  @NonNull
  public Map<String, IAssemblyInstance> getAssemblyInstanceMap() {
    return assemblyInstances;
  }

  /**
   * Get a listing of all choice instances.
   *
   * @return the listing
   */
  @Override
  @SuppressWarnings("null")
  @NonNull
  public List<IChoiceInstance> getChoiceInstances() {
    // this shouldn't get called all that often, so this is better than allocating
    // memory
    return getModelInstances().stream()
        .filter(obj -> obj instanceof IChoiceInstance)
        .map(obj -> (IChoiceInstance) obj)
        .collect(Collectors.toList());
  }

  /**
   * Get a listing of all choice group instances.
   *
   * @return the listing
   */
  @Override
  @SuppressWarnings("null")
  @NonNull
  public List<IChoiceGroupInstance> getChoiceGroupInstances() {
    // this shouldn't get called all that often, so this is better than allocating
    // memory
    return getModelInstances().stream()
        .filter(obj -> obj instanceof IChoiceGroupInstance)
        .map(obj -> (IChoiceGroupInstance) obj)
        .collect(Collectors.toList());
  }
}