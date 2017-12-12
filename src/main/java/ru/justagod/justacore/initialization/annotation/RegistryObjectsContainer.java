package ru.justagod.justacore.initialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Текс эту штуку надо писать над типами с кучей статических полей типаBlock или Item.
 * Оно их автоматом зарегистрирует. Их имена будут соответствовать
 * имени поля или тому что указано в {@link RegistrySpecial}. Если вы не хотите чтоб
 * объект был зарегистрирован, используйте {@link RegistryExcept}. Если вы хотите
 * зарегистрировать блок с ItemBlock используйте аннотацию {@link RegistryBlockSpecial}
 * @author JustAGod
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryObjectsContainer {

    @SuppressWarnings("unused")
    String[] dependencies() default {};

    /**
     * @return искать ли метод с аннтоцией {@link CustomRegistry} в которой вы сами зарегистрируете
     * предметы/блоки
     */
    @SuppressWarnings("unused")
    boolean customRegistry() default false;
}
