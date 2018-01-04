package ru.justagod.justacore.initialization.language;

import com.google.common.base.Strings;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.justagod.justacore.initialization.IllegalReturnTypeException;
import ru.justagod.justacore.initialization.InvalidModifiersException;
import ru.justagod.justacore.initialization.InvalidParametersException;
import ru.justagod.justacore.initialization.annotation.*;
import ru.justagod.justacore.initialization.core.config.Config;
import ru.justagod.justacore.initialization.obj.ModModule;
import ru.justagod.justacore.initialization.obj.StaticModModule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by JustAGod on 04.01.2018.
 */
public class JavaAdapter implements LanguageAdapter {

    private static Logger logger = LogManager.getLogger();

    @Override
    public ModModule buildModule(Class clazz) throws Exception {
        if (!ModModule.class.isAssignableFrom(clazz)) {
            return new StaticModModule(clazz);
        } else {
            return (ModModule) buildInstance(clazz);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object buildInstance(Class clazz) throws Exception {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(InstanceFactory.class)) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new InvalidModifiersException(clazz.getName(), method.getName());
                }
                if (!method.getReturnType().isAssignableFrom(clazz)) {
                    throw new IllegalReturnTypeException(clazz.getName(), clazz.getName());
                }
                if (method.getParameterCount() > 0) throw new InvalidParametersException(clazz.getName(), method.getName());

                method.setAccessible(true);
                return method.invoke(clazz);
            }
        }
        Constructor constructor = clazz.getConstructor();
        constructor.newInstance();
        return clazz.newInstance();
    }

    @Override
    public void registerFields(Class clazz) {
        for (Field field : clazz.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            Class fieldClazz = field.getType();
            try {
                if (Item.class.isAssignableFrom(fieldClazz) || Block.class.isAssignableFrom(fieldClazz)) {
                    if (!field.isAnnotationPresent(RegistryExcept.class)) {
                        String id = null;
                        String oreDict = "";
                        if (field.isAnnotationPresent(RegistrySpecial.class)) {
                            RegistrySpecial special = field.getAnnotation(RegistrySpecial.class);
                            id = special.name();
                            oreDict = special.oreDict();
                        }
                        if (Strings.isNullOrEmpty(id)) {
                            id = field.getName();
                        }

                        Object o = field.get(clazz);
                        if (o != null) {
                            if (o instanceof Item) {
                                GameRegistry.registerItem((Item) o, id);
                                if (!Strings.isNullOrEmpty(oreDict)) {
                                    OreDictionary.registerOre(oreDict, (Item) o);
                                }
                            } else if (o instanceof Block) {
                                if (field.isAnnotationPresent(RegistryBlockSpecial.class)) {
                                    Class<? extends ItemBlock> classItemBlock = field.getAnnotation(RegistryBlockSpecial.class).classItemBlock();
                                    GameRegistry.registerBlock((Block) o, classItemBlock, id);
                                } else {
                                    GameRegistry.registerBlock((Block) o, id);
                                }
                                if (!Strings.isNullOrEmpty(oreDict)) {
                                    OreDictionary.registerOre(oreDict, (Block) o);
                                }
                            }
                        } else {
                            logger.warn("Field " + field.getName() + " is null");
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("Can't register field " + field.getName());
                logger.error(e);
            }
        }
    }

    @Override
    public void callCustomRegistry(Object o) throws Exception{
        Class clazz = o.getClass();

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(CustomRegistry.class)) {
                if (Modifier.isStatic(method.getModifiers())) throw new Exception("Method has no be static");
                if (method.getParameterCount() > 0) throw new Exception("Method has not have parameters");

                method.invoke(o);
                return;
            }
        }
        throw new Exception("Custom registry method wasn't found");
    }

    @Override
    public boolean tryToStaticRegister(Class clazz) {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(CustomRegistry.class)) {
                    method.setAccessible(true);
                    method.invoke(clazz);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Exception while invoking Custom Registry static method", e);
        }
        return false;
    }

    @Override
    public void setConfig(Class clazz, Config config) {
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigHolder.class)) {
                    if (Modifier.isStatic(field.getModifiers()) && field.getType().isAssignableFrom(Config.class)) {
                        field.setAccessible(true);
                        field.set(clazz, config);
                    }
                }
            }
        } catch (Exception e1) {
            logger.error("Can't assign config", e1);
        }
    }
}
