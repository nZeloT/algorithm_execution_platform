package org.nzelot.execution.platform.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

    public static Constructor<?> getConstructorWithAnnotation(Class<?> target, Class<? extends Annotation> annotation){
        for (Constructor<?> declaredConstructor : target.getDeclaredConstructors()) {
            if(declaredConstructor.isAnnotationPresent(annotation)){
                return declaredConstructor;
            }
        }
        return null;
    }

    public static Method getMethodWithAnnotation(Class<?> target, Class<? extends Annotation> annotation) {
        for (Method declaredMethod : target.getDeclaredMethods()) {
            if(declaredMethod.isAnnotationPresent(annotation)){
                return declaredMethod;
            }
        }
        return null;
    }

    public static List<Method> getMethodsWithAnnotation(Class<?> target, Class<? extends Annotation> annotation) {
        var methods = new ArrayList<Method>();
        for (Method declaredMethod : target.getDeclaredMethods()) {
            if(declaredMethod.isAnnotationPresent(annotation)){
                methods.add(declaredMethod);
            }
        }
        return methods;
    }

}
