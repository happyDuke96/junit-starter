package com.junit.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;

/**Эту возможность активно использует Spring чтобы получить все данные о классе используя Reflection API*/
public class PostProcessingExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        Class<?> testClazz = testInstance.getClass();
        for (Field declaredField : testClazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            System.out.println(declaredField.getType());
            System.out.println(declaredField.getName());
        }
        for (Class<?> declaredClass : testClazz.getDeclaredClasses()) {
            for (AnnotatedType annotatedInterface : declaredClass.getAnnotatedInterfaces()) {
                System.out.println(annotatedInterface);
            }
            System.out.println(declaredClass.getName());
        }
    }
}
