/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type;

import org.mule.runtime.api.meta.model.ModelProperty;

/**
 * Internal {@link ModelProperty} which saves the {@link }
 *
 * @since 4.1
 */
public class ExtensionParameterDescriptorModelProperty implements ModelProperty {

  private ExtensionParameter extensionParameter;

  public ExtensionParameterDescriptorModelProperty(ExtensionParameter extensionParameter) {
    this.extensionParameter = extensionParameter;
  }

  public ExtensionParameter getExtensionParameter() {
    return extensionParameter;
  }

  @Override
  public String getName() {
    return "extension-param";
  }

  @Override
  public boolean isPublic() {
    return false;
  }
}
