package ru.justagod.justacore.initialization.annotation;

import cpw.mods.fml.common.eventhandler.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by JustAGod on 10.12.17.
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    @SuppressWarnings("unused")
    String[] dependencies() default {};

    @SuppressWarnings("unused")
    boolean isMandatory() default false;

    @SuppressWarnings("unused")
    EventPriority priority() default EventPriority.NORMAL;
}
