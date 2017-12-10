package ru.justagod.justacore.initialization;

import com.google.common.collect.SetMultimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import joptsimple.internal.Strings;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.justagod.justacore.initialization.annotation.*;
import ru.justagod.justacore.initialization.data.Data;
import ru.justagod.justacore.initialization.data.ItemRenderRegistryData;
import ru.justagod.justacore.initialization.data.RegistryContainerData;
import ru.justagod.justacore.initialization.data.RegistryData;
import ru.justagod.justacore.initialization.obj.ModModule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JustAGod on 10.12.17.
 */
@SuppressWarnings("unused")
public class InitHandler {

    private static final Logger logger = LogManager.getLogger("InitHandler");

    private final Object mod;

    private final Pattern pattern = Pattern.compile("[A-Z]");
    private final Set<RegistryData> registryClasses = new LinkedHashSet<>();
    private final Set<RegistryData> tileClasses = new LinkedHashSet<>();
    private final Set<Data> moduleClasses = new LinkedHashSet<>();
    private final Set<ItemRenderRegistryData> itemRenderClasses = new LinkedHashSet<>();
    private final Set<RegistryContainerData> containerClasses = new LinkedHashSet<>();

    private final Set<String> softDependencies = new LinkedHashSet<>();
    private final Set<String> hardDependencies = new LinkedHashSet<>();

    private static final List<ModModule> modules = new LinkedList<>();

    public InitHandler(Object mod) {
        this.mod = mod;
    }

    public void start(FMLConstructionEvent event) {
        SetMultimap<String, ASMData> data = event.getASMHarvestedData().getAnnotationsFor(FMLCommonHandler.instance().findContainerFor(mod));

        queryModules(data.get(IntegrationModule.class.getName()));
        queryRegistryObjects(data.get(RegistryObject.class.getName()));
        queryItemRenders(data.get(ItemRenderRegistryObject.class.getName()));
        queryTiles(data.get(TileRegistryObject.class.getName()));

    }

    public void preInit(FMLPreInitializationEvent e) {
        for (RegistryData registryClass : registryClasses) {
            if (registryClass.dependencies.length > 0) {
                boolean flag = false;
                for (String dependency : registryClass.dependencies) {
                    if (!Loader.isModLoaded(dependency)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;
            }
            Object o;
            try {
                o = buildInstance(registryClass.clazz);
            } catch (Exception e1) {
                logger.warn("Can't load class " + registryClass.clazz);
                logger.error(e1);
                continue;
            }
            if (registryClass.customRegistry) {
                try {
                    callCustomRegistry(o);
                } catch (Exception e1) {
                    logger.warn("Can't call custom registry method in class " + registryClass.clazz);
                    logger.error(e1);
                    continue;
                }
            } else {
                String id = registryClass.registryId;
                if (o instanceof Item) {
                    GameRegistry.registerItem((Item) o, id);
                } else if (o instanceof Block) {
                    if (!Strings.isNullOrEmpty(registryClass.itemBlock)) {
                        String nameItemBlock = registryClass.itemBlock;
                        try {
                            Class classItemBlock = Class.forName(nameItemBlock);
                            if (!ItemBlock.class.isAssignableFrom(classItemBlock)) {
                                logger.warn("Class " + nameItemBlock + " must extends ItemBlock");
                                continue;
                            }
                            GameRegistry.registerBlock((Block) o, classItemBlock, id);
                        } catch (ClassNotFoundException e1) {
                            logger.error(e1);
                            continue;
                        }
                    } else {
                        GameRegistry.registerBlock((Block) o, id);
                    }
                } else {
                    logger.warn("Not supported class " + registryClass.clazz);
                }
            }
        }
        for (RegistryData tile : tileClasses) {
            if (tile.dependencies.length > 0) {
                boolean flag = false;
                for (String dependency : tile.dependencies) {
                    if (!Loader.isModLoaded(dependency)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;
            }
            Object o;
            try {
                o = buildInstance(tile.clazz);
            } catch (Exception e1) {
                logger.warn("Can't load class " + tile.clazz);
                logger.error(e1);
                continue;
            }
            if (tile.customRegistry) {
                try {
                    callCustomRegistry(o);
                } catch (Exception e1) {
                    logger.warn("Can't call custom registry method in class " + tile.clazz);
                    logger.error(e1);
                    continue;
                }
            } else {
                String id = tile.registryId;
                if (o instanceof TileEntity) {
                    try {
                        GameRegistry.registerTileEntity((Class<? extends TileEntity>) Class.forName(tile.clazz), id);
                    } catch (ClassNotFoundException e1) {
                        logger.error(e1);
                    }
                } else {
                    logger.warn("Expected: TileEntity actual: " + tile.clazz);
                }
            }
        }
        for (RegistryContainerData container : containerClasses) {
            Object o;
            if (container.dependencies.length > 0) {
                boolean flag = false;
                for (String dependency : container.dependencies) {
                    if (!Loader.isModLoaded(dependency)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;
            }
            Class clazz;
            try {

                clazz = Class.forName(container.clazz);
            } catch (ClassNotFoundException e1) {
                logger.warn("Can't find registry container class");
                logger.error(e1);
                continue;
            }
            registerFields(clazz);

        }
        if (e.getSide() == Side.CLIENT) {
            for (ItemRenderRegistryData itemRender : itemRenderClasses) {
                Item toBind = GameData.getItemRegistry().getObject(itemRender.itemName);
                if (toBind == null) {
                    toBind = GameData.getItemRegistry().getObject(FMLCommonHandler.instance().findContainerFor(mod).getModId() + ':' + itemRender.itemName);
                }
                if (toBind == null) {
                    logger.warn("Item " + itemRender.itemName + " not registered");
                    continue;
                }
                Object o;
                try {
                    o = buildInstance(itemRender.clazz);
                } catch (Exception e1) {
                    logger.warn("Can't load class " + itemRender.clazz);
                    logger.error(e1);
                    continue;
                }
                if (!(o instanceof IItemRenderer)) {
                    logger.warn("Expected: IItemRenderer actual: " + itemRender.clazz);
                    continue;
                }
                if (itemRender.customRegistry) {
                    try {
                        callCustomRegistry(o);
                    } catch (Exception e1) {
                        logger.warn("Can't call custom registry method in class " + itemRender.clazz);
                        logger.error(e1);
                        continue;
                    }
                } else {
                    MinecraftForgeClient.registerItemRenderer(toBind, (IItemRenderer) o);
                }
            }
        }
        for (Data module : moduleClasses) {
            if (module.dependencies.length > 0) {
                boolean flag = false;
                for (String dependency : module.dependencies) {
                    if (!Loader.isModLoaded(dependency)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;
            }
            Object o;
            try {
                o = buildInstance(module.clazz);
            } catch (Exception e1) {
                logger.warn("Can't load module " + module.clazz);
                logger.error(e1);
                continue;
            }
            if (!(o instanceof ModModule)) {
                logger.warn("Module " + module.clazz + " have to extends ModModule");
                continue;
            }
            modules.add((ModModule) o);
            ((ModModule) o).onPreInit(e);
        }
    }



    public void init(FMLInitializationEvent e) {
        for (ModModule module : modules) {
            module.onInit(e);
        }
    }

    public void postInit(FMLPostInitializationEvent e) {
        for (ModModule module : modules) {
            module.onPostInit(e);
        }
    }

    private void registerFields(Class clazz) {
        for (Field field : clazz.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            Class fieldClazz = field.getDeclaringClass();
            try {
                if (Item.class.isAssignableFrom(fieldClazz) || Block.class.isAssignableFrom(fieldClazz)) {
                    if (!field.isAnnotationPresent(RegistryExcept.class)) {
                        String id;
                        if (field.isAnnotationPresent(RegistrySpecial.class)) {
                            id = field.getAnnotation(RegistrySpecial.class).name();
                        } else {
                            id = field.getName();
                        }
                        Object o = field.get(clazz);
                        if (o != null) {
                            if (o instanceof Item) {
                                GameRegistry.registerItem((Item) o, id);
                            } else if (o instanceof Block) {
                                if (field.isAnnotationPresent(RegistryBlockSpecial.class)) {
                                    String nameItemBlock = field.getAnnotation(RegistryBlockSpecial.class).classItemBlock();
                                    try {
                                        Class classItemBlock = Class.forName(nameItemBlock);
                                        if (!ItemBlock.class.isAssignableFrom(classItemBlock)) {
                                            logger.warn("Class " + nameItemBlock + " must extends ItemBlock");
                                            continue;
                                        }
                                        GameRegistry.registerBlock((Block) o, classItemBlock, id);
                                    } catch (ClassNotFoundException e) {
                                        logger.error(e);
                                        continue;
                                    }
                                } else {
                                    GameRegistry.registerBlock((Block) o, id);
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

    private void callCustomRegistry(Object o) throws Exception {
        Class clazz = o.getClass();

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(CustomRegistry.class)) {
                if (Modifier.isStatic(method.getModifiers())) throw new Exception("Method has no be static");
                if (method.getParameterCount() > 0) throw new Exception("Method has not have parameters");

                method.invoke(o);
                return;
            }
        }
        throw new Exception("Custom registry method not found");
    }

    private static <T> T buildInstance(String className) throws Exception {
        Class<? extends T> clazz = (Class<? extends T>) Class.forName(className);

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(InstnaceFactory.class)) {
                if (!Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers())) {
                    throw new InvalidModifiersException(className, method.getName());
                }
                if (!method.getReturnType().isAssignableFrom(clazz)) {
                    throw new IllegalReturnTypeException(className, clazz.getName());
                }
                if (method.getParameterCount() > 0) throw new InvalidParametersException(className, method.getName());

                return (T) method.invoke(clazz);
            }
        }
        return clazz.newInstance();
    }

    private void queryTiles(Set<ASMData> data) {
        for (ASMData datum : data) {
            String[] dependencies = (String[]) datum.getAnnotationInfo().get("dependencies");
            String id = datum.getAnnotationInfo().get("registryName") == null ? "" : (String) datum.getAnnotationInfo().get("registryName");
            boolean customRegistry = datum.getAnnotationInfo().get("customRegistry") == null ? false : (Boolean) datum.getAnnotationInfo().get("customRegistry");

            softDependencies.addAll(Arrays.asList(dependencies));

            if (Strings.isNullOrEmpty(id) && !customRegistry) {
                id = datum.getClassName();
                try {
                    id = transformName(id);
                } catch (InvalidRegistryNameException e) {
                    throw new RuntimeException(e);
                }
            }

            RegistryData registryData = new RegistryData(dependencies, datum.getClassName(), id, customRegistry, "");
            tileClasses.add(registryData);
        }
    }

    private void queryModules(Set<ASMData> data) {
        for (ASMData datum : data) {
            String[] dependencies = datum.getAnnotationInfo().get("dependencies") == null ? new String[]{} : (String[]) datum.getAnnotationInfo().get("dependencies");
            boolean mandatory = datum.getAnnotationInfo().get("isMandatory") == null ? false : (Boolean) datum.getAnnotationInfo().get("isMandatory");

            if (mandatory) {
                hardDependencies.addAll(Arrays.asList(dependencies));
            } else {
                softDependencies.addAll(Arrays.asList(dependencies));
            }

            Data moduleData = new Data(dependencies, datum.getClassName());
            moduleClasses.add(moduleData);
        }
    }

    private void queryRegistryObjects(Set<ASMData> data) {

        for (ASMData datum : data) {
            String[] dependencies = datum.getAnnotationInfo().get("dependencies") == null ? new String[]{} : (String[]) datum.getAnnotationInfo().get("dependencies");
            String id = datum.getAnnotationInfo().get("registryId") == null ? "" : (String) datum.getAnnotationInfo().get("registryId");
            String itemBlock = datum.getAnnotationInfo().get("itemBlock") == null ? "" : (String) datum.getAnnotationInfo().get("itemBlock");
            Boolean customRegistry = datum.getAnnotationInfo().get("customRegistry") == null ? false : (Boolean) datum.getAnnotationInfo().get("customRegistry");

            softDependencies.addAll(Arrays.asList(dependencies));

            if (Strings.isNullOrEmpty(id) && !customRegistry) {
                id = datum.getClassName().substring(datum.getClassName().lastIndexOf(".") + 1);
                try {
                    id = transformName(id);
                } catch (InvalidRegistryNameException e) {
                    throw new RuntimeException(e);
                }
            }

            RegistryData registryData = new RegistryData(dependencies, datum.getClassName(), id, customRegistry, itemBlock);
            registryClasses.add(registryData);
        }
    }

    public String transformName(String name) throws InvalidRegistryNameException {
        final String nameCopy = name;
        name = name.replace("Item", "");
        name = name.replace("Block", "");
        if (name.startsWith("TileEntity")) {
            name = name.replace("TileEntity", "");
        } else {
            name = name.replace("Tile", "");
        }

        if (Strings.isNullOrEmpty(name)) throw new InvalidRegistryNameException(nameCopy);

        StringBuilder builder = new StringBuilder(name);
        Matcher matcher = pattern.matcher(builder);
        if (Character.isUpperCase(name.charAt(0))) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }

        name = name.replaceAll("[A-Z]","_$0");

        return name.toLowerCase();
    }

    private void queryContainers(Set<ASMData> data) {
        for (ASMData datum : data) {
            String[] dependencies = datum.getAnnotationInfo().get("dependencies") == null ? new String[]{} : (String[]) datum.getAnnotationInfo().get("dependencies");
            String id = datum.getAnnotationInfo().get("registryId") == null ? "" : (String) datum.getAnnotationInfo().get("registryId");
            Boolean customRegistry = datum.getAnnotationInfo().get("customRegistry") == null ? false : (Boolean) datum.getAnnotationInfo().get("customRegistry");

            softDependencies.addAll(Arrays.asList(dependencies));

            containerClasses.add(new RegistryContainerData(dependencies, datum.getClassName(), customRegistry));
        }
    }

    private void queryItemRenders(Set<ASMData> data) {
        for (ASMData datum : data) {
            String item = datum.getAnnotationInfo().get("itemName") == null ? "" : (String) datum.getAnnotationInfo().get("itemName");
            boolean customRegistry = datum.getAnnotationInfo().get("customRegistry") == null ? false : (Boolean) datum.getAnnotationInfo().get("customRegistry");

            itemRenderClasses.add(new ItemRenderRegistryData(customRegistry, item, datum.getClassName()));
        }
    }

}
