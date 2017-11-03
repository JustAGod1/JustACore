package ru.justagod.justacore.gui.overlay;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by JustAGod on 17.10.17.
 */
@Target(value = {ElementType.PARAMETER, ElementType.METHOD})
public @interface Clickable {
}
