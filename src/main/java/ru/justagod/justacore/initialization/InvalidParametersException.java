package ru.justagod.justacore.initialization;

/**
 * Created by JustAGod on 10.12.17.
 */
public class InvalidParametersException extends Exception {

    public InvalidParametersException(String className, String methodName) {
        super(String.format("Invalid method parameters %s of class %s", methodName, className));
    }
}
