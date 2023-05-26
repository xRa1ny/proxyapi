package me.xra1ny.proxyapi.exceptions;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public class ClassNotAnnotatedException extends RuntimeException {
    public ClassNotAnnotatedException(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation) {
        super("class " + clazz.getName() + " needs to be annotated with " + annotation.getName());
    }
}
