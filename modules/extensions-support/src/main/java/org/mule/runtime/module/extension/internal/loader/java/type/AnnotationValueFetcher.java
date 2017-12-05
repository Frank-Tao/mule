/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type;

import javax.lang.model.element.VariableElement;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Function;

/**
 * @param <A> Annotation type
 * @since 4.1
 */
public interface AnnotationValueFetcher<A extends Annotation> {

  String getStringValue(Function<A, String> function);

  List<Type> getClassArrayValue(Function<A, Class[]> function);

  Type getClassValue(Function<A, Class> function);

  Integer getIntValue(Function<A, Integer> function);

  <E extends Enum> VariableElement getEnumValue(Function<A, E> function);

  <R> R getValue(Function<A, R> function);
}
