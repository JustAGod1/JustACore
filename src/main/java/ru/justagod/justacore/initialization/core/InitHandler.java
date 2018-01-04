package ru.justagod.justacore.initialization.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.SetMultimap;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.discovery.asm.ModAnnotation;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.justagod.justacore.initialization.InvalidRegistryNameException;
import ru.justagod.justacore.initialization.annotation.*;
import ru.justagod.justacore.initialization.core.config.Config;
import ru.justagod.justacore.initialization.core.config.ConfigType;
import ru.justagod.justacore.initialization.core.config.TextConfig;
import ru.justagod.justacore.initialization.data.*;
import ru.justagod.justacore.initialization.language.JavaAdapter;
import ru.justagod.justacore.initialization.language.LanguageAdapter;
import ru.justagod.justacore.initialization.language.ScalaAdapter;
import ru.justagod.justacore.initialization.obj.ModModule;

import java.io.File;
import java.util.*;

import static ru.justagod.justacore.initialization.obj.Side.*;

/**
 * @author JustAGod
 */
public final class InitHandler {

    private static final Map<String, InitHandler> handlers = new LinkedHashMap<>();
    private final Logger logger;
    private final List<ModuleWrapper> modules = new LinkedList<>();
    private final ModContainer mod;

    private final Set<RegistryData> registryClasses = new LinkedHashSet<>();
    private final Set<RegistryData> tileClasses = new LinkedHashSet<>();
    private final Set<ModuleData> moduleClasses = new LinkedHashSet<>();
    private final Set<ItemRenderRegistryData> itemRenderClasses = new LinkedHashSet<>();
    private final Set<RegistryContainerData> containerClasses = new LinkedHashSet<>();

    private final Set<String> softDependencies = new LinkedHashSet<>();
    private final Set<String> hardDependencies = new LinkedHashSet<>();

    private Config config;
    private LanguageAdapter adapter;
    /**
     * Просто чтобы отлавливать ошибки
     */
    private int stage = 0;

    private InitHandler(ModContainer mod) {
        this.mod = mod;
        logger = LogManager.getLogger(String.format("InitHandler[%s]", mod.getModId()));

    }

    static void find(List<ModContainer> containers, ASMDataTable table) {
        for (ModContainer container : containers) {
            if (container instanceof FMLModContainer) {
                InitHandler handler = new InitHandler(container);
                handler.start(table);

                handlers.put(container.getModId(), handler);
            }
        }
    }

    public static InitHandler findHandlerForMod(String modid) {
        for (Map.Entry<String, InitHandler> entry : handlers.entrySet()) {
            if (entry.getKey().equals(modid)) return entry.getValue();
        }
        return null;
    }

    @SuppressWarnings("all")
    public static String transformName(String name) throws InvalidRegistryNameException {
        final String nameCopy = name;
        name = name.replace("Item", "");
        name = name.replace("Block", "");
        if (name.startsWith("TileEntity")) {
            name = name.replace("TileEntity", "");
        } else {
            name = name.replace("Tile", "");
        }

        if (Strings.isNullOrEmpty(name)) throw new InvalidRegistryNameException(nameCopy);

        if (Character.isUpperCase(name.charAt(0))) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }

        name = name.replaceAll("[A-Z]", "_$0");

        return name.toLowerCase();
    }

    @SuppressWarnings("unused")
    public Config getConfig() {
        return config;
    }


    private void start(ASMDataTable dataTable) {
        Preconditions.checkArgument(stage == 0, "You have to call this method first");
        stage = 1;
        SetMultimap<String, ASMData> data = dataTable.getAnnotationsFor(mod);


        if (data != null) {
            String language = "";
            for (ASMData asmData : data.get(Mod.class.getName())) {
                String tmp = (String) asmData.getAnnotationInfo().get("modLanguage");
                language = tmp != null ? tmp : "java";
            }
            switch (language) {
                case "scala":
                    adapter = new ScalaAdapter();
                    break;
                case "java":
                    adapter = new JavaAdapter();
                    break;
                default:
                    logger.error("Can't identify mod language (" + language + ')');
                    return;
            }
            queryModules(data.get(Module.class.getName()));
            queryRegistryObjects(data.get(RegistryObject.class.getName()));
            queryItemRenders(data.get(ItemRenderRegistryObject.class.getName()));
            queryTiles(data.get(TileRegistryObject.class.getName()));
            queryContainers(data.get(RegistryObjectsContainer.class.getName()));

            for (String dependency : hardDependencies) {
                mod.getRequirements().add(new DefaultArtifactVersion(dependency, true));
            }
            for (String dependency : softDependencies) {
                mod.getDependencies().add(new DefaultArtifactVersion(dependency, true));
            }
        }
    }

    @SuppressWarnings("unchecked")
    void preInit(FMLPreInitializationEvent e) {
        if (stage != 1) return;
        logger.info("Pre initializing mod");
        stage = 2;

        {
            Class clazz = mod.getMod().getClass();
            Class<? extends Config> configClazz = TextConfig.class;
            String postfix = "txt";
            String name = null;
            if (clazz.isAnnotationPresent(ConfigExtra.class)) {
                name = ((ConfigExtra) clazz.getAnnotation(ConfigExtra.class)).name();
                ConfigType type = ((ConfigExtra) clazz.getAnnotation(ConfigExtra.class)).type();
                postfix = type.getPostfix();
                configClazz = type.getClazz();
            }
            if (Strings.isNullOrEmpty(name)) {
                name = mod.getModId() + "-config." + postfix;
            }
            try {
                config = configClazz.getConstructor(String.class, Class.class, boolean.class).newInstance("config/" + name, clazz, clazz.isAnnotationPresent(EmptyConfig.class));
            } catch (Exception e1) {
                logger.error("Can't initialize config", e1);
                config = new TextConfig((File) null, true);
            }

            adapter.setConfig(clazz, config);
        }
        List<String> notLoaded = new LinkedList<>();
        for (String dependency : hardDependencies) {
            if (!Loader.isModLoaded(dependency)) {
                notLoaded.add(dependency);
            }
        }
        if (notLoaded.size() > 0) {
            logger.error("Not loaded mods: " + notLoaded.toString());
            logger.error("Those are mandatory mods, so goodbye!");
            FMLCommonHandler.instance().exitJava(1, false);
        }
        notLoaded.clear();
        for (String dependency : softDependencies) {
            if (!Loader.isModLoaded(dependency)) {
                notLoaded.add(dependency);
            }
        }
        if (notLoaded.size() > 0) {
            logger.info("Not loaded mods: " + notLoaded.toString());
            logger.info("Those aren't mandatory mods");
        }
        for (RegistryData registryClass : registryClasses) {
            if (registryClass.dependencies.size() > 0) {
                boolean flag = false;
                for (String dependency : registryClass.dependencies) {
                    if (!Loader.isModLoaded(dependency)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;
            }
            if (!config.getBooleanValue(registryClass.configDependency)) {
                continue;
            }
            Class clazz;
            try {
                clazz = Class.forName(registryClass.clazz);
            } catch (ClassNotFoundException e1) {
                logger.error("Can't find class " + registryClass.clazz, e1);
                continue;
            }
            if (!registryClass.customRegistry || !adapter.tryToStaticRegister(clazz)) {
                Object o;
                try {
                    o = adapter.buildInstance(Class.forName(registryClass.clazz, true, mod.getMod().getClass().getClassLoader()));
                } catch (Exception e1) {
                    logger.error("Can't load class " + registryClass.clazz, e1);
                    continue;
                }
                if (registryClass.customRegistry) {
                    try {
                        adapter.callCustomRegistry(o);
                    } catch (Exception e1) {
                        logger.warn("Can't call custom registry method in class " + registryClass.clazz);
                        logger.error(e1);
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
                            }
                        } else {
                            GameRegistry.registerBlock((Block) o, id);
                        }
                    } else {
                        logger.warn("Not supported class " + registryClass.clazz);
                    }
                }
            }
        }
        for (RegistryData tile : tileClasses) {
            if (!config.getBooleanValue(tile.configDependency)) {
                continue;
            }
            if (tile.dependencies.size() > 0) {
                boolean flag = false;
                for (String dependency : tile.dependencies) {
                    if (!Loader.isModLoaded(dependency)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;
            }
            Class clazz;
            try {
                clazz = Class.forName(tile.clazz);
            } catch (ClassNotFoundException e1) {
                logger.error("Can't find class " + tile.clazz, e1);
                continue;
            }
            if (!tile.customRegistry || !adapter.tryToStaticRegister(clazz)) {

                Object o;
                try {
                    o = adapter.buildInstance(Class.forName(tile.clazz, true, mod.getMod().getClass().getClassLoader()));
                } catch (Exception e1) {
                    logger.warn("Can't load class " + tile.clazz);
                    logger.error(e1);
                    continue;
                }
                if (tile.customRegistry) {
                    try {
                        adapter.callCustomRegistry(o);
                    } catch (Exception e1) {
                        logger.warn("Can't call custom registry method in class " + tile.clazz);
                        logger.error(e1);
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
        }
        for (RegistryContainerData container : containerClasses) {
            if (!config.getBooleanValue(container.configDependency)) {
                continue;
            }

            if (container.dependencies.size() > 0) {
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
            adapter.registerFields(clazz);


        }
        if (e.getSide() == Side.CLIENT) {
            for (ItemRenderRegistryData itemRender : itemRenderClasses) {
                Item toBind = GameData.getItemRegistry().getObject(itemRender.itemName);
                if (toBind == null) {
                    toBind = GameData.getItemRegistry().getObject(mod.getModId() + ':' + itemRender.itemName);
                }
                if (toBind == null) {
                    logger.warn("Item " + itemRender.itemName + " not registered");
                    continue;
                }
                Object o;
                try {
                    o = adapter.buildInstance(Class.forName(itemRender.clazz, true, mod.getMod().getClass().getClassLoader()));
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
                        adapter.callCustomRegistry(o);
                    } catch (Exception e1) {
                        logger.warn("Can't call custom registry method in class " + itemRender.clazz);
                        logger.error(e1);
                    }
                } else {
                    MinecraftForgeClient.registerItemRenderer(toBind, (IItemRenderer) o);
                }
            }
        }
        sortModules();
        for (ModuleData module : moduleClasses) {
            if (!config.getBooleanValue(module.configDependency)) {
                continue;
            }
            if (module.side != COMMON) {
                if (module.side == CLIENT && e.getSide() != Side.CLIENT) continue;
                if (module.side == SERVER && e.getSide() != Side.SERVER) continue;
            }
            if (module.dependencies.size() > 0) {
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
                o = adapter.buildModule(Class.forName(module.clazz, true, mod.getMod().getClass().getClassLoader()));
            } catch (Exception e1) {
                logger.error("Can't load module " + module.clazz, e1);
                if (module.mandatory) {
                    logger.error("Module " + module.clazz + " is mandatory, so goodbye!");
                    FMLCommonHandler.instance().exitJava(1, false);
                }
                continue;
            }
            ModuleWrapper wrapper = new ModuleWrapper((ModModule) o, module.mandatory, module.clazz.substring(module.clazz.lastIndexOf('.') + 1));
            modules.add(wrapper);
            logger.info("Module " + module.clazz.substring(module.clazz.lastIndexOf('.') + 1) + " has been loaded");

            try {
                wrapper.module.onPreInit(e);
            } catch (Exception e1) {
                RuntimeException exception = new RuntimeException("Exception while init module " + wrapper.name, e1);
                if (wrapper.mandatory) throw exception;
                else logger.error("", exception);
            }
        }
    }


    private void sortModules() {
        List<ModuleData> highest = new LinkedList<>();
        List<ModuleData> high = new LinkedList<>();
        List<ModuleData> normal = new LinkedList<>();
        List<ModuleData> low = new LinkedList<>();
        List<ModuleData> lowest = new LinkedList<>();

        for (ModuleData moduleData : moduleClasses) {
            if (moduleData.priority == EventPriority.HIGHEST) highest.add(moduleData);
            if (moduleData.priority == EventPriority.HIGH) high.add(moduleData);
            if (moduleData.priority == EventPriority.NORMAL) normal.add(moduleData);
            if (moduleData.priority == EventPriority.LOW) low.add(moduleData);
            if (moduleData.priority == EventPriority.LOWEST) lowest.add(moduleData);
        }

        moduleClasses.clear();

        moduleClasses.addAll(highest);
        moduleClasses.addAll(high);
        moduleClasses.addAll(normal);
        moduleClasses.addAll(low);
        moduleClasses.addAll(lowest);

    }

    void init(FMLInitializationEvent e) {
        if (stage != 2) return;
        logger.info("Initializing mod");
        stage = 3;
        for (ModuleWrapper module : modules) {
            try {
                module.module.onInit(e);
            } catch (Exception e1) {
                RuntimeException exception = new RuntimeException("Exception while init module " + module.name, e1);
                if (module.mandatory) throw exception;
                else logger.error("", exception);

            }
        }
    }

    void postInit(FMLPostInitializationEvent e) {
        if (stage != 3) return;
        stage = 4;
        logger.info("Post initializing mod");
        for (ModuleWrapper module : modules) {
            try {
                module.module.onPostInit(e);
            } catch (Exception e1) {
                RuntimeException exception = new RuntimeException("Exception while init module " + module.name, e1);
                if (module.mandatory) throw exception;
                else logger.error("", exception);
            }
        }
    }


    @SuppressWarnings("unchecked")
    private void queryTiles(Set<ASMData> data) {
        for (ASMData datum : data) {
            ArrayList<String> dependencies = datum.getAnnotationInfo().get("dependencies") == null ? new ArrayList<>() : (ArrayList<String>) datum.getAnnotationInfo().get("dependencies");
            String id = datum.getAnnotationInfo().get("registryName") == null ? "" : (String) datum.getAnnotationInfo().get("registryName");
            String config = datum.getAnnotationInfo().get("configDependency") == null ? "" : (String) datum.getAnnotationInfo().get("configDependency");
            boolean customRegistry = datum.getAnnotationInfo().get("customRegistry") == null ? false : (Boolean) datum.getAnnotationInfo().get("customRegistry");

            softDependencies.addAll(dependencies);

            if (Strings.isNullOrEmpty(id) && !customRegistry) {
                id = datum.getClassName();
                try {
                    id = transformName(id);
                } catch (InvalidRegistryNameException e) {
                    throw new RuntimeException(e);
                }
            }

            RegistryData registryData = new RegistryData(dependencies, datum.getClassName(), id, customRegistry, "", config);
            tileClasses.add(registryData);
        }
    }

    @SuppressWarnings("unchecked")
    private void queryModules(Set<ASMData> data) {
        for (ASMData datum : data) {
            ArrayList<String> dependencies = datum.getAnnotationInfo().get("dependencies") == null ? new ArrayList<>() : (ArrayList<String>) datum.getAnnotationInfo().get("dependencies");
            boolean mandatory = datum.getAnnotationInfo().get("isMandatory") == null ? false : (Boolean) datum.getAnnotationInfo().get("isMandatory");
            String config = datum.getAnnotationInfo().get("configDependency") == null ? "" : (String) datum.getAnnotationInfo().get("configDependency");
            String rawPriority = datum.getAnnotationInfo().get("priority") == null ? "NORMAL" : ReflectionHelper.getPrivateValue(ModAnnotation.EnumHolder.class, (ModAnnotation.EnumHolder) datum.getAnnotationInfo().get("priority"), "value");
            String rawSide = datum.getAnnotationInfo().get("sideOnly") == null ? "COMMON" : ReflectionHelper.getPrivateValue(ModAnnotation.EnumHolder.class, (ModAnnotation.EnumHolder) datum.getAnnotationInfo().get("sideOnly"), "value");

            EventPriority priority = Enum.valueOf(EventPriority.class, rawPriority);
            ru.justagod.justacore.initialization.obj.Side side = Enum.valueOf(ru.justagod.justacore.initialization.obj.Side.class, rawSide);
            if (mandatory) {
                hardDependencies.addAll(dependencies);
            } else {
                softDependencies.addAll(dependencies);
            }

            ModuleData moduleData = new ModuleData(dependencies, datum.getClassName(), priority, mandatory, config, side);
            moduleClasses.add(moduleData);
        }
    }

    @SuppressWarnings("unchecked")
    private void queryRegistryObjects(Set<ASMData> data) {

        for (ASMData datum : data) {
            ArrayList<String> dependencies = datum.getAnnotationInfo().get("dependencies") == null ? new ArrayList<>() : (ArrayList<String>) datum.getAnnotationInfo().get("dependencies");
            String id = datum.getAnnotationInfo().get("registryId") == null ? "" : (String) datum.getAnnotationInfo().get("registryId");
            String itemBlock = datum.getAnnotationInfo().get("itemBlock") == null ? "" : (String) datum.getAnnotationInfo().get("itemBlock");
            String config = datum.getAnnotationInfo().get("configDependency") == null ? "" : (String) datum.getAnnotationInfo().get("configDependency");
            Boolean customRegistry = datum.getAnnotationInfo().get("customRegistry") == null ? false : (Boolean) datum.getAnnotationInfo().get("customRegistry");

            softDependencies.addAll(dependencies);

            if (Strings.isNullOrEmpty(id) && !customRegistry) {
                id = datum.getClassName().substring(datum.getClassName().lastIndexOf(".") + 1);
                try {
                    id = transformName(id);
                } catch (InvalidRegistryNameException e) {
                    throw new RuntimeException(e);
                }
            }

            RegistryData registryData = new RegistryData(dependencies, datum.getClassName(), id, customRegistry, itemBlock, config);
            registryClasses.add(registryData);
        }
    }

    @SuppressWarnings("unchecked")
    private void queryContainers(Set<ASMData> data) {
        for (ASMData datum : data) {
            ArrayList<String> dependencies = datum.getAnnotationInfo().get("dependencies") == null ? new ArrayList<>() : (ArrayList<String>) datum.getAnnotationInfo().get("dependencies");
            Boolean customRegistry = datum.getAnnotationInfo().get("customRegistry") == null ? false : (Boolean) datum.getAnnotationInfo().get("customRegistry");
            String config = datum.getAnnotationInfo().get("configDependency") == null ? "" : (String) datum.getAnnotationInfo().get("configDependency");

            softDependencies.addAll(dependencies);

            containerClasses.add(new RegistryContainerData(dependencies, datum.getClassName(), customRegistry, config));
        }
    }

    private void queryItemRenders(Set<ASMData> data) {
        for (ASMData datum : data) {
            String item = datum.getAnnotationInfo().get("item") == null ? "" : (String) datum.getAnnotationInfo().get("itemName");
            boolean customRegistry = datum.getAnnotationInfo().get("customRegistry") == null ? false : (Boolean) datum.getAnnotationInfo().get("customRegistry");

            itemRenderClasses.add(new ItemRenderRegistryData(customRegistry, item, datum.getClassName()));
        }
    }

    @Override
    public String toString() {
        return "InitHandler{" +
                "mod=" + mod +
                ", stage=" + stage +
                '}';
    }


}
