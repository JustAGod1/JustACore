package ru.justagod.justacore.initialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by JustAGod on 10.12.17.
 */
@Target(value = {ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TileRegistryObject {

    @SuppressWarnings("unused")
    String[] dependencies() default {};

    @SuppressWarnings("unused")
    String registryName() default  "";

    @SuppressWarnings("unused")
    boolean customRegistry() default false;
}
