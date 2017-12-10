package ru.justagod.justacore.initialization;

/**
 * Created by JustAGod on 10.12.17.
 */
public class InvalidRegistryNameException extends Exception{

    public InvalidRegistryNameException(String name) {
        super("Error while parsing name: " + name);
    }


}
