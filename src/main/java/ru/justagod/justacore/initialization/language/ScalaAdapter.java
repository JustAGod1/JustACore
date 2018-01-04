package ru.justagod.justacore.initialization.language;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.justagod.justacore.initialization.annotation.*;
import ru.justagod.justacore.initialization.core.config.Config;
import ru.justagod.justacore.initialization.obj.ModModule;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by JustAGod on 04.01.2018.
 */
public class ScalaAdapter implements LanguageAdapter {
    private Logger logger = LogManager.getLogger();

    @Override
    public ModModule buildModule(Class clazz) throws Exception {
        if (isScalaObject(clazz)) {
            return new ScalaModule(getScalaObject(clazz));
        } else {
            if (ModModule.class.isAssignableFrom(clazz)) {
                return (ModModule) clazz.newInstance();
            }
        }
        throw new Exception("Too empty module");
    }

    @Override
    public Object buildInstance(Class clazz) throws Exception {
        if (isScalaObject(clazz)) {
            return getScalaObject(clazz);
        } else {
            return clazz.newInstance();
        }
    }

    private Object getScalaObject(Class clazz) throws Exception {
        if (!isScalaObject(clazz)) throw new Exception("Not scala object");

        return Class.forName(clazz.getName() + "$", false, clazz.getClassLoader()).getField("MODULE$").get(null);
    }

    private boolean isScalaObject(Class clazz) {
        try {
            Class.forName(clazz.getName() + "$", false, clazz.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (Exception e) {
            logger.error("Something went wrong", e);
        }
        return false;
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
                if (isScalaObject(clazz)) {
                    registerObjectFields(getScalaObject(clazz));
                }
            } catch (Exception e) {
                logger.warn("Can't register field " + field.getName());
                logger.error(e);
            }
        }
    }

    private void registerObjectFields(Object obj) {
        for (Field field : obj.getClass().getFields()) {
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

                        Object o = field.get(obj);
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
    public void callCustomRegistry(Object o) throws Exception {
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
        if (!isScalaObject(clazz)) return false;
        try {
            Object instance = getScalaObject(clazz);
            for (Method method : instance.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(CustomRegistry.class)) {
                    Preconditions.checkArgument(method.getParameterCount() == 0);
                    method.setAccessible(true);
                    method.invoke(instance);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("", e);
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
                    } else {
                        throw new Exception("Invalid field type " + field);
                    }
                }
            }
            if (isScalaObject(clazz)) {
                Object obj = getScalaObject(clazz);
                for (Field field : obj.getClass().getDeclaredFields()) {
                    if (field.getType().isAssignableFrom(Config.class)) {
                        field.setAccessible(true);
                        field.set(clazz, config);
                    } else {
                        throw new Exception("Invalid field type " + field);
                    }
                }
            }
        } catch (Exception e1) {
            logger.error("Can't assign config", e1);
        }
    }

    private static class ScalaModule implements ModModule {

        private final Object object;

        private Method preInit;
        private Method init;
        private Method postInit;

        @SuppressWarnings("unchecked")
        public ScalaModule(Object object) {
            this.object = object;

            for (Method method : object.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(ModuleEventHandler.class)) {
                    Preconditions.checkArgument(method.getParameterCount() == 1, "Methods with annotation ModuleEventHandler must have only 1 parameter");
                    Preconditions.checkArgument(FMLEvent.class.isAssignableFrom(method.getParameterTypes()[0]), "Method parameter must extend FMLEvent");
                    Class<? extends FMLEvent> parameter = (Class<? extends FMLEvent>) method.getParameterTypes()[0];
                    if (FMLPreInitializationEvent.class.isAssignableFrom(parameter)) {
                        Preconditions.checkArgument(preInit == null, "Pre init method already defined");
                        method.setAccessible(true);
                        preInit = method;
                    } else if (FMLInitializationEvent.class.isAssignableFrom(parameter)) {
                        Preconditions.checkArgument(init == null, "Init method already defined");
                        method.setAccessible(true);
                        init = method;
                    } else if (FMLPostInitializationEvent.class.isAssignableFrom(parameter)) {
                        Preconditions.checkArgument(postInit == null, "Post init method already defined");
                        method.setAccessible(true);
                        postInit = method;
                    }
                }
            }
        }

        @Override
        public void onPreInit(FMLPreInitializationEvent e) {
            if (preInit != null) {
                try {
                    preInit.invoke(object, e);
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public void onInit(FMLInitializationEvent e) {
            if (init != null) {
                try {
                    init.invoke(object, e);
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public void onPostInit(FMLPostInitializationEvent e) {
            if (postInit != null) {
                try {
                    postInit.invoke(object, e);
                } catch (IllegalAccessException | InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
