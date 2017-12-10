package ru.justagod.justacore.initialization;

/**
 * Created by JustAGod on 10.12.17.
 */
public class InvalidModifiersException extends Exception {

    public InvalidModifiersException(String className, String methodName) {
        super(String.format("Invalid method modifiers(must be public static) %s of class %s", methodName, className));
    }
}
