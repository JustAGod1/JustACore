package ru.justagod.justacore.initialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Писать над не статическим методом без параметров.
 * Вызывается если вы в {@link TileRegistryObject}, {@link RegistryObject} или {@link RegistryObjectsContainer} указали
 * customRegistry true
 * @author JustAGod
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface CustomRegistry {
}
