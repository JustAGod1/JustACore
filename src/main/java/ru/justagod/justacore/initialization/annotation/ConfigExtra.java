package ru.justagod.justacore.initialization.annotation;

import ru.justagod.justacore.initialization.core.config.ConfigType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by JustAGod on 03.01.2018.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ConfigExtra {

    String name() default "";

    ConfigType type() default ConfigType.TEXT;
}
