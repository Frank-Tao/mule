/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.enricher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.metadata.api.model.MetadataFormat.CSV;
import static org.mule.metadata.api.model.MetadataFormat.JSON;
import static org.mule.metadata.api.model.MetadataFormat.XML;
import static org.mule.runtime.module.extension.api.util.MuleExtensionUtils.loadExtension;

import org.mule.metadata.api.model.MetadataFormat;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.meta.Typed;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.api.meta.model.OutputModel;
import org.mule.runtime.api.meta.model.operation.OperationModel;
import org.mule.runtime.api.meta.model.parameter.ParameterModel;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.test.metadata.extension.MetadataExtension;

import org.junit.Test;
import scala.annotation.meta.param;

public class CustomStaticTypeDeclarationEnricherTestCase extends AbstractMuleTestCase {

  ExtensionModel extension = loadExtension(MetadataExtension.class);

  @Test
  public void withInputXmlStaticType() throws Exception {
    OperationModel o = getOperation("xmlInput");
    ParameterModel param = o.getAllParameterModels().get(0);
    MetadataType type = param.getType();
    assertThat(param.hasDynamicType(), is(false));
    assertThat(type.getMetadataFormat(), is(XML));
    assertThat(type.toString(), is("shiporder"));
  }

  @Test
  public void withOutputXmlStaticType() throws Exception {
    OperationModel o = getOperation("xmlOutput");
    OutputModel output = o.getOutput();
    assertThat(output.hasDynamicType(), is(false));
    MetadataType type = output.getType();
    assertThat(type.getMetadataFormat(), is(XML));
    assertThat(type.toString(), is("shiporder"));
  }

  @Test
  public void customTypeOutput() throws Exception {
    OperationModel o = getOperation("customTypeOutput");
    OutputModel output = o.getOutput();
    MetadataType type = output.getType();
    assertThat(output.hasDynamicType(), is(false));
    assertThat(type.getMetadataFormat(), is(CSV));
    assertThat(type.toString(), is("csv-object"));
  }

  @Test
  public void customTypeInput() throws Exception {
    OperationModel o = getOperation("customTypeInput");
    ParameterModel param = o.getAllParameterModels().get(0);
    assertCustomStaticType(param, new CustomTypeAssertionUnit(JSON, "json-object"));
  }

  @Test
  public void customTypeInputAndOutput() throws Exception {
    OperationModel o = getOperation("customInputAndOutput");
    assertCustomStaticType(o.getAllParameterModels().get(0), new CustomTypeAssertionUnit(JSON, "json-object"));
  }

  private OperationModel getOperation(String ope) {
    return extension.getOperationModel(ope).orElseThrow(() -> new RuntimeException(ope + " not found"));
  }

  private void assertCustomStaticType(Typed typed, CustomTypeAssertionUnit unit) {
    MetadataType type = typed.getType();
    assertThat(typed.hasDynamicType(), is(false));
    assertThat(type.getMetadataFormat(), is(unit.getFormat()));
    assertThat(type.toString(), is(unit.getTypeId()));
  }

  private class CustomTypeAssertionUnit {

    private final MetadataFormat format;
    private final String typeId;

    public CustomTypeAssertionUnit(MetadataFormat format, String typeId) {
      this.format = format;
      this.typeId = typeId;
    }

    public MetadataFormat getFormat() {
      return format;
    }

    public String getTypeId() {
      return typeId;
    }
  }
}
