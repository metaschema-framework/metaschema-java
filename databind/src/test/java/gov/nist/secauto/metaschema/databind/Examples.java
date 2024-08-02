/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

//import gov.nist.secauto.metaschema.databind.io.Format;
//import gov.nist.secauto.metaschema.databind.io.IBoundLoader;
//import gov.nist.secauto.metaschema.databind.io.ISerializer;
//
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;

class Examples { // NOPMD

  // @SuppressWarnings("PMD")
  // @Test
  // void testLoadJson() throws FileNotFoundException, IOException {
  // // get the binding context instance, which manages Module-to-object
  // binding information
  // IBindingContext bindingContext = IBindingContext.instance();
  //
  // // create a loader which is used to parse the content
  // IBoundLoader loader = bindingContext.newBoundLoader();
  //
  // // specify the bound class to load data into and the file to load it from
  // // the loader figures out the format to load from (i.e. JSON)
  // BoundClass object
  // = loader.load(BoundClass.class, new
  // File("src/test/resources/test-content/bound-class-simple.json"));
  // System.out.println(object.getId());
  // }
  //
  // @Test
  // void test() throws FileNotFoundException, IOException {
  // // get the binding context instance, which manages Module-to-object
  // binding information
  // IBindingContext bindingContext = IBindingContext.instance();
  //
  // // create a loader which is used to parse the content
  // IBoundLoader loader = bindingContext.newBoundLoader();
  //
  // // specify the bound class to load data into and the file to load it from
  // // the loader figures out the format to load from (i.e. JSON)
  // BoundClass object
  // = loader.load(BoundClass.class, new
  // File("src/test/resources/test-content/bound-class-simple.json"));
  //
  // // create a serializer to write the object
  // ISerializer<Object> serializer = bindingContext.newSerializer(Format.XML,
  // Object.class);
  // serializer.serialize(object, new File("target/bound-class-simple.xml"));
  // }

}
