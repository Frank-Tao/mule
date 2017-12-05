/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.metadata.extension;

import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.metadata.TypeResolver;
import org.mule.runtime.extension.api.annotation.metadata.fixed.InputXmlType;
import org.mule.runtime.extension.api.annotation.metadata.fixed.OutputXmlType;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.test.metadata.extension.resolver.CsvInputStaticTypeResolver;
import org.mule.test.metadata.extension.resolver.JavaInputStaticTypeResolver;
import org.mule.test.metadata.extension.resolver.JavaOutputStaticTypeResolver;
import org.mule.test.metadata.extension.resolver.JsonInputStaticTypeResolver;

import java.io.InputStream;

public class CustomStaticMetadataOperations {

  @OutputXmlType(schema = "order.xsd", qName = "shiporder")
  public InputStream xmlOutput() {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream("order.xml");
  }

  @OutputXmlType(schema = "order.xsd", qName = "shiporder")
  public String xmlInput(@InputXmlType(schema = "order.xsd", qName = "shiporder") InputStream xml) {
    return IOUtils.toString(xml);
  }

  @OutputResolver(output = CsvInputStaticTypeResolver.class)
  public Object customTypeOutput() {
    return "Name,LastName\njuan,desimoni\nesteban,wasinger";
  }

  @MediaType("application/json")
  public String customTypeInput(@TypeResolver(JsonInputStaticTypeResolver.class) InputStream type) {
    return IOUtils.toString(type);
  }

  @OutputResolver(output = JavaOutputStaticTypeResolver.class)
  public Object customInputAndOutput(@TypeResolver(JsonInputStaticTypeResolver.class) InputStream type) {
    return null;
  }
}
