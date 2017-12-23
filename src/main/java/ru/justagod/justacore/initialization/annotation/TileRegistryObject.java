package ru.justagod.justacore.initialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Писать над TileEntity.
 * Автоматически зарегистрирует по такому же методу как и {@link RegistryObject}
 * @author JustAGod
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

    String configDependency() default "";
}
