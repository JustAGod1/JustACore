package ru.justagod.justacore.initialization.annotation;

import net.minecraft.item.ItemBlock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Смотри {@link RegistryObjectsContainer}
 * @author JustAGod
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface RegistryBlockSpecial {

    Class<? extends ItemBlock> classItemBlock();
}
