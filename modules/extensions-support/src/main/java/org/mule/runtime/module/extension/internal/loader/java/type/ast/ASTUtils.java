/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.type.TypeKind.BOOLEAN;
import static javax.lang.model.type.TypeKind.BYTE;
import static javax.lang.model.type.TypeKind.CHAR;
import static javax.lang.model.type.TypeKind.DOUBLE;
import static javax.lang.model.type.TypeKind.FLOAT;
import static javax.lang.model.type.TypeKind.INT;
import static javax.lang.model.type.TypeKind.LONG;
import static javax.lang.model.type.TypeKind.SHORT;
import static javax.lang.model.type.TypeKind.VOID;

import org.mule.runtime.api.util.Reference;
import org.mule.runtime.module.extension.internal.loader.java.type.AnnotationValueFetcher;
import org.mule.runtime.module.extension.internal.loader.java.type.GenericInfo;
import org.mule.runtime.module.extension.internal.loader.java.type.Type;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import com.google.gson.reflect.TypeToken;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

class ASTUtils {

  private ProcessingEnvironment processingEnvironment;
  //TODO - This will be removed once there is a AST MetadataType TypeLoader
  private Map<String, Class> primitiveTypesClasses = new HashMap<>();
  private Map<String, TypeMirror> primitiveTypeMirrors = new HashMap<>();

  /**
   *
   * @param processingEnvironment
   */
  ASTUtils(ProcessingEnvironment processingEnvironment) {
    this.processingEnvironment = processingEnvironment;
    primitiveTypeMirrors.put("int", processingEnvironment.getTypeUtils().getPrimitiveType(INT));
    primitiveTypeMirrors.put("long", processingEnvironment.getTypeUtils().getPrimitiveType(LONG));
    primitiveTypeMirrors.put("double", processingEnvironment.getTypeUtils().getPrimitiveType(DOUBLE));
    primitiveTypeMirrors.put("float", processingEnvironment.getTypeUtils().getPrimitiveType(FLOAT));
    primitiveTypeMirrors.put("boolean", processingEnvironment.getTypeUtils().getPrimitiveType(BOOLEAN));
    primitiveTypeMirrors.put("char", processingEnvironment.getTypeUtils().getPrimitiveType(CHAR));
    primitiveTypeMirrors.put("byte", processingEnvironment.getTypeUtils().getPrimitiveType(BYTE));
    primitiveTypeMirrors.put("void", processingEnvironment.getTypeUtils().getNoType(VOID));
    primitiveTypeMirrors.put("short", processingEnvironment.getTypeUtils().getPrimitiveType(SHORT));

    primitiveTypesClasses.put("int", Integer.TYPE);
    primitiveTypesClasses.put("long", Long.TYPE);
    primitiveTypesClasses.put("double", Double.TYPE);
    primitiveTypesClasses.put("float", Float.TYPE);
    primitiveTypesClasses.put("boolean", Boolean.TYPE);
    primitiveTypesClasses.put("char", Character.TYPE);
    primitiveTypesClasses.put("byte", Byte.TYPE);
    primitiveTypesClasses.put("void", Void.TYPE);
    primitiveTypesClasses.put("short", Short.TYPE);
  }

  <T extends Annotation> ASTValueFetcher<T> fromAnnotation(Class<T> annotationClass, Element element) {
    return new ASTValueFetcher<>(annotationClass, element, processingEnvironment);
  }

  public static class ASTValueFetcher<A extends Annotation> implements AnnotationValueFetcher<A> {

    private final Class<A> configurationClass;
    private final Element typeElement;
    private ProcessingEnvironment processingEnvironment;

    public ASTValueFetcher(Class<A> configurationClass, Element typeElement, ProcessingEnvironment processingEnvironment) {
      this.configurationClass = configurationClass;
      this.typeElement = typeElement;
      this.processingEnvironment = processingEnvironment;
    }

    public String getStringValue(Function<A, String> function) {
      return (String) getConstant(function).getValue();
    }

    @Override
    public List<Type> getClassArrayValue(Function<A, Class[]> function) {
      AnnotationValue value = (AnnotationValue) getObjectValue(function);
      if (value != null) {
        List<AnnotationValue> array = (List<AnnotationValue>) value.getValue();
        return array.stream().map(attr -> ((DeclaredType) attr.getValue()))
            .map(declaredType -> new ASTType((TypeElement) declaredType.asElement(), processingEnvironment))
            .collect(toList());
      } else {
        return emptyList();
      }
    }

    @Override
    public ASTType getClassValue(Function<A, Class> function) {
      return new ASTType((TypeElement) ((DeclaredType) ((AnnotationValue) getObjectValue(function)).getValue()).asElement(),
                         processingEnvironment);
    }

    @Override
    public Integer getIntValue(Function<A, Integer> function) {
      return (Integer) getConstant(function).getValue();
    }

    @Override
    public <E extends Enum> VariableElement getEnumValue(Function<A, E> function) {
      return (VariableElement) ((AnnotationValue) getObjectValue(function)).getValue();
    }

    @Override
    public <R> R getValue(Function<A, R> function) {
      return (R) getObjectValue(function);
    }

    AnnotationValue getConstant(Function function) {
      return (AnnotationValue) getObjectValue(function);
    }

    private Object getObjectValue(Function function) {
      return getExecutable(configurationClass, typeElement, function, processingEnvironment).get();
    }
  }

  public static <T> Reference<Object> getExecutable(Class<T> configurationClass, Element element, Function function,
                                                    ProcessingEnvironment processingEnvironment) {
    CountDownLatch latch = new CountDownLatch(1);
    Enhancer enhancer = new Enhancer();
    Reference<Object> reference = new Reference<>();
    enhancer.setSuperclass(configurationClass);
    enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
      if (method.getName().equals("toString")) {
        return "String";
      }
      System.out.println(Thread.currentThread().toString());
      reference.set(null);

      getAnnotationFrom(configurationClass, element, processingEnvironment)
          .ifPresent(annotation -> getAnnotationElementValue(annotation, method.getName())
              .ifPresent(reference::set));

      latch.countDown();
      return null;
    });
    function.apply((T) enhancer.create());
    return reference;
  }

  private static Optional<? extends AnnotationValue> getAnnotationElementValue(AnnotationMirror annotation, String name) {
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
      if (entry.getKey().getSimpleName().toString().equals(name)) {
        return of(entry.getValue());
      }
    }

    for (Element element : annotation.getAnnotationType().asElement().getEnclosedElements()) {
      if (element.getKind().equals(ElementKind.METHOD)) {
        if (element.getSimpleName().toString().equals(name)) {
          return ofNullable(((ExecutableElement) element).getDefaultValue());
        }
      }
    }

    return empty();
  }

  private static Optional<AnnotationMirror> getAnnotationFrom(Class configurationClass, Element typeElement,
                                                              ProcessingEnvironment processingEnvironment) {
    TypeElement annotationTypeElement = processingEnvironment.getElementUtils().getTypeElement(configurationClass.getTypeName());
    for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
      DeclaredType annotationType = annotationMirror.getAnnotationType();
      TypeMirror obj = annotationTypeElement.asType();
      if (annotationType.equals(obj)) {
        return of(annotationMirror);
      }
    }
    return empty();
  }

  java.lang.reflect.Type getReflectType(ASTType type) {
    List<GenericInfo> generics = type.getGenerics();
    try {
      if (type.getTypeElement() != null) {
        String typeName = processingEnvironment.getElementUtils().getBinaryName(type.getTypeElement()).toString();
        if (!generics.isEmpty()) {
          java.lang.reflect.Type[] types = generics.stream().map(ASTUtils::toType).toArray(java.lang.reflect.Type[]::new);
          return TypeToken.getParameterized(Class.forName(typeName), types).getType();
        } else {
          return Class.forName(typeName);
        }
      } else {
        return getPrimitiveClass(type)
            .orElseThrow(() -> new RuntimeException("Unable to find type for " + type.getTypeName()));
      }
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  static java.lang.reflect.Type toType(GenericInfo info) {
    java.lang.reflect.Type[] typeArguments =
        info.getGenerics().stream().map(ASTUtils::toType).toArray(java.lang.reflect.Type[]::new);
    try {
      return TypeToken.getParameterized(Class.forName(info.getConcreteType().getTypeName()), typeArguments).getType();
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  Optional<Class> getPrimitiveClass(Type type) {
    return ofNullable(primitiveTypesClasses.get(type.getTypeName()));
  }

  Optional<TypeMirror> getPrimitiveTypeMirror(Class clazz) {
    return ofNullable(primitiveTypeMirrors.get(clazz.getTypeName()));
  }
}
