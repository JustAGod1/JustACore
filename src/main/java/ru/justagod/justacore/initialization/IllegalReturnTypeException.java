package ru.justagod.justacore.initialization;

/**
 * Created by JustAGod on 10.12.17.
 */
public class IllegalReturnTypeException extends Exception {

   public IllegalReturnTypeException(String className, String returnClassName) {
       super(String.format("Class %s have to return %s", className, returnClassName));
   }
}
