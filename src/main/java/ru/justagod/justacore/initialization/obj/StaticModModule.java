package ru.justagod.justacore.initialization.obj;

import com.google.common.base.Preconditions;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ru.justagod.justacore.initialization.annotation.ModuleEventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Внутреняя кухня
 * @author JustAGod
 */
public class StaticModModule implements ModModule {

    private final Class targetClazz;

    private Method onPreInit;
    private Method onInit;
    private Method onPostInit;

    public StaticModModule(Class targetClazz) throws Exception {
        this.targetClazz = targetClazz;

        for (Method method : targetClazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ModuleEventHandler.class)) {
                Preconditions.checkArgument(method.getParameterCount() != 1, "Illegal parameters count");
                Preconditions.checkArgument(Modifier.isStatic(method.getModifiers()), "Method must be static");

                Class parameter = method.getParameterTypes()[0];

                if (FMLPreInitializationEvent.class.isAssignableFrom(parameter)) {
                    if (onPreInit == null) {
                        method.setAccessible(true);
                        onPreInit = method;
                    } else {
                        throw new Exception("On pre init method is already declared");
                    }
                }
                if (FMLInitializationEvent.class.isAssignableFrom(parameter)) {
                    if (onInit == null) {
                        method.setAccessible(true);
                        onInit = method;
                    } else {
                        throw new Exception("On init method is already declared");
                    }
                }
                if (FMLPostInitializationEvent.class.isAssignableFrom(parameter)) {
                    if (onPostInit == null) {
                        method.setAccessible(true);
                        onPostInit = method;
                    } else {
                        throw new Exception("On post init method is already declared");
                    }
                }
            }
        }
        Preconditions.checkArgument(onPreInit != null || onInit != null ||  onPostInit != null, "You have to specify at least one event handler");
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent e) {
        if (onPreInit != null) {
            try {
                onPreInit.invoke(targetClazz, e);
            } catch (IllegalAccessException | InvocationTargetException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
        }
    }

    @Override
    public void onInit(FMLInitializationEvent e) {
        if (onInit != null) {
            try {
                onInit.invoke(targetClazz,e);
            } catch (IllegalAccessException | InvocationTargetException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
        }
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent e) {
        if (onPostInit != null) {
            try {
                onPostInit.invoke(targetClazz,e);
            } catch (IllegalAccessException | InvocationTargetException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
        }
    }
}
