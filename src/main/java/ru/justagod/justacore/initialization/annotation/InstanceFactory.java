package ru.justagod.justacore.initialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Писать над статическими, можно не публичными, методами без параметров.
 * Метод должен возвращать экземпляр класса. Используется всегда когда нужно создать экземпляр.
 * @author JustAGod
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface InstanceFactory {
}
