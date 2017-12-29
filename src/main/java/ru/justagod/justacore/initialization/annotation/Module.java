package ru.justagod.justacore.initialization.annotation;

import cpw.mods.fml.common.eventhandler.EventPriority;
import ru.justagod.justacore.initialization.core.InitHandler;
import ru.justagod.justacore.initialization.obj.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Писать над чем угодно
 * Если тип наследуется от {@link ru.justagod.justacore.initialization.obj.ModModule},
 * то {@link InitHandler} попытаеться создать экземпляр.
 * Если у него ничего не получиться он забудет о вашем модуле. Если Класс не наследуеться
 * от ModModule, инициализатор попытаеться найти статические методы с параметрами FMLPreInitializationEvent,
 * FMLInitializationEvent и FMLPostInitializationEvent. Если он их найдет он будет их вызывать.
 * @author JustAGod
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    /**
     * Возвращает зависимости в виде массива из modid. Если хоть один из нужных модов
     * не представлен объект загружен не будет.
     * @return зависимости.
     */
    @SuppressWarnings("unused")
    String[] dependencies() default {};

    /**
     * Если модуль обязателен и не будет загружен, майн будет экстрено прерван.
     * @return да/нет
     */
    @SuppressWarnings("unused")
    boolean isMandatory() default false;

    /**
     * Модули с большим приритетом загружаються и вызываються раньше других.
     * @return приритет
     */
    @SuppressWarnings("unused")
    EventPriority priority() default EventPriority.NORMAL;

    @SuppressWarnings("unused")
    String configDependency() default "";

    @SuppressWarnings("unused")
    Side sideOnly() default Side.COMMON;
}
