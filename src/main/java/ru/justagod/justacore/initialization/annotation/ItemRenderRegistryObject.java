package ru.justagod.justacore.initialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Писать над типом IItemRender
 * Автоматически привязывает экземпляр класса к предмету
 * @author JustAGod
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemRenderRegistryObject {

    @SuppressWarnings("unused")
    String item();

    @SuppressWarnings("unused")
    boolean customRegistry() default false;


}
