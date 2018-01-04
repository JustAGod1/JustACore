package ru.justagod.justacore.example.initializationexample;

/**
 * Created by JustAGod on 12.12.17.
 */
@SuppressWarnings("unused")
public class SimpleModule {

    int someValue = 80;
    static int someStaticValue = 90;

    public SimpleModule() {
        System.out.println(method(50));
    }

    private int method(int someParameter) {
        int someLocalValue = someValue + someParameter + someStaticValue;
        return someLocalValue;
    }
}
