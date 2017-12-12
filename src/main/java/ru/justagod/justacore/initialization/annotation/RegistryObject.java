package ru.justagod.justacore.initialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Эту анотацию писать над типом Block или Item. Автоматически будет создан экземпляр
 * и зарегистрирован под именем взятым из названия типа т.е. если название ItemBlackSkirt,
 * то зарегистрирован он будет под именем black_skirt. Если хотите какое то свое имя укажите
 * его в {@link #registryId()}
 * @author JustAGod
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryObject {

    @SuppressWarnings("unused")
    String[] dependencies() default {};

    @SuppressWarnings("unused")
    String registryId() default  "";


    /**
     * @return искать ли метод с аннтоцией {@link CustomRegistry} в которой вы сами зарегистрируете
     * предмет/блок
     */
    @SuppressWarnings("unused")
    boolean customRegistry() default false;

    /**
     * Только для блоков!
     * @return айтем блок.
     */
    @SuppressWarnings("unused")
    String itemBlock() default  "";
}
