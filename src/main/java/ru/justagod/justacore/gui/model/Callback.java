package ru.justagod.justacore.gui.model;

/**
 * Created by JustAGod on 04.12.17.
 */
public interface Callback<P, R> {

    R call(P param);
}
