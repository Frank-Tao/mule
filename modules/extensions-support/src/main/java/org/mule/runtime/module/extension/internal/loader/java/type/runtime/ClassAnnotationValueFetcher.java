/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.runtime;

import static java.util.stream.Collectors.toList;

import org.mule.runtime.module.extension.internal.loader.java.type.AnnotationValueFetcher;
import org.mule.runtime.module.extension.internal.loader.java.type.Type;

import javax.lang.model.element.VariableElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class ClassAnnotationValueFetcher<T extends Annotation> implements AnnotationValueFetcher<T> {

  private Class<T> annotationClass;
  private AnnotatedElement annotatedElement;

  public ClassAnnotationValueFetcher(Class<T> annotationClass, AnnotatedElement annotatedElement) {
    this.annotationClass = annotationClass;
    this.annotatedElement = annotatedElement;
  }

  @Override
  public String getStringValue(Function<T, String> function) {
    return function.apply(annotatedElement.getAnnotation(annotationClass));
  }

  @Override
  public List<Type> getClassArrayValue(Function<T, Class[]> function) {
    return Stream.of(function.apply(annotatedElement.getAnnotation(annotationClass)))
        .map(TypeWrapper::new)
        .collect(toList());
  }

  @Override
  public TypeWrapper getClassValue(Function<T, Class> function) {
    return new TypeWrapper(function.apply(annotatedElement.getAnnotation(annotationClass)));
  }

  @Override
  public Integer getIntValue(Function<T, Integer> function) {
    return function.apply(annotatedElement.getAnnotation(annotationClass));
  }

  @Override
  public <E extends Enum> VariableElement getEnumValue(Function<T, E> function) {
    return null;
  }

  @Override
  public <R> R getValue(Function<T, R> function) {
    return function.apply(annotatedElement.getAnnotation(annotationClass));
  }
}
