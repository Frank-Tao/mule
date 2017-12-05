/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java;

import org.mule.runtime.api.meta.model.ModelProperty;
import org.mule.runtime.module.extension.internal.loader.java.type.MethodElement;

public class ExtensionOperationModelDescriptorProperty implements ModelProperty {

  private MethodElement operationMethod;

  public ExtensionOperationModelDescriptorProperty(MethodElement operationMethod) {
    this.operationMethod = operationMethod;
  }

  public MethodElement getOperationMethod() {
    return operationMethod;
  }

  @Override
  public String getName() {
    return "operation-desadasasdsa";
  }

  @Override
  public boolean isPublic() {
    return false;
  }
}
