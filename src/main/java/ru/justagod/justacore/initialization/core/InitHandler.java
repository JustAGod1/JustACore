package ru.justagod.justacore.initialization.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.SetMultimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
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
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.justagod.justacore.initialization.IllegalReturnTypeException;
import ru.justagod.justacore.initialization.InvalidModifiersException;
import ru.justagod.justacore.initialization.InvalidParametersException;
import ru.justagod.justacore.initialization.InvalidRegistryNameException;
import ru.justagod.justacore.initialization.annotation.*;
import ru.justagod.justacore.initialization.data.ItemRenderRegistryData;
import ru.justagod.justacore.initialization.data.ModuleData;
import ru.justagod.justacore.initialization.data.RegistryContainerData;
import ru.justagod.justacore.initialization.data.RegistryData;
import ru.justagod.justacore.initialization.obj.ModModule;
import ru.justagod.justacore.initialization.obj.StaticModModule;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.justagod.justacore.initialization.obj.Side.CLIENT;
import static ru.justagod.justacore.initialization.obj.Side.COMMON;
import static ru.justagod.justacore.initialization.obj.Side.SERVER;

/**
 * Чтобы инициализатор работал корректно вам нужно следовать шаблону
 *
 * @author JustAGod
 */
public final class InitHandler {

    private final Logger logger;
    private static final Map<Object, InitHandler> handlers = new LinkedHashMap<>();
    private final List<ModModule> modules = new LinkedList<>();
    private final ModContainer mod;

    private final Set<RegistryData> registryClasses = new LinkedHashSet<>();
    private final Set<RegistryData> tileClasses = new LinkedHashSet<>();
    private final Set<ModuleData> moduleClasses = new LinkedHashSet<>();
    private final Set<ItemRenderRegistryData> itemRenderClasses = new LinkedHashSet<>();
    private final Set<RegistryContainerData> containerClasses = new LinkedHashSet<>();

    private final Set<String> softDependencies = new LinkedHashSet<>();
    private final Set<String> hardDependencies = new LinkedHashSet<>();

    private final Config config;
    /**
     * Просто чтобы отлавливать ошибки
     */
    private int stage = 0;

    static void find(List<ModContainer> containers, ASMDataTable table) {
        for (ModContainer container : containers) {
            if (container instanceof FMLModContainer) {
                InitHandler handler = new InitHandler(container);
                handler.start(table);

                handlers.put(container.getMod(), handler);
            }
        }
    }

    public static InitHandler findHandlerForMod(Object o) {
        for (Map.Entry<Object, InitHandler> entry : handlers.entrySet()) {
            if (entry.getKey() == o) return entry.getValue();
        }
        return null;
    }

    private InitHandler(ModContainer mod) {
        this.mod = mod;
        logger = LogManager.getLogger(String.format("InitHandler[%s]", mod.getModId()));
        config = new Config("config/" + mod.getModId() + "config.json");
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

    public Config getConfig() {
        return config;
    }

    private Object buildInstance(String className) throws Exception {
        Class<?> clazz = Class.forName(className);

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(InstanceFactory.class)) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new InvalidModifiersException(className, method.getName());
                }
                if (!method.getReturnType().isAssignableFrom(clazz)) {
                    throw new IllegalReturnTypeException(className, clazz.getName());
                }
                if (method.getParameterCount() > 0) throw new InvalidParametersException(className, method.getName());

                method.setAccessible(true);
                return method.invoke(clazz);
            }
        }
        Constructor constructor = clazz.getConstructor();
        constructor.newInstance();
        return clazz.newInstance();
    }

    private void start(ASMDataTable dataTable) {
        Preconditions.checkArgument(stage == 0, "You have to call this method first");
        stage = 1;
        SetMultimap<String, ASMData> data = dataTable.getAnnotationsFor(mod);

        if (data != null) {
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

    void preInit(FMLPreInitializationEvent e) {
        if (stage != 1) return;
        logger.info("Pre initializing mod");
        stage = 2;
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
            registerFields(clazz);


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
                o = buildModule(module.clazz);
            } catch (Exception e1) {
                logger.error("Can't load module " + module.clazz, e1);
                if (module.mandatory) {
                    logger.error("Module " + module.clazz + " is mandatory, so goodbye!");
                    FMLCommonHandler.instance().exitJava(1, false);
                }
                continue;
            }
            modules.add((ModModule) o);
            logger.info("Module " + module.clazz.substring(module.clazz.lastIndexOf('.') + 1) + " has been loaded");
            ((ModModule) o).onPreInit(e);
        }
    }

    private ModModule buildModule(String className) throws Exception {
        Class clazz = Class.forName(className);

        if (!ModModule.class.isAssignableFrom(clazz)) {
            return new StaticModModule(clazz);
        } else {
            return (ModModule) buildInstance(className);
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
        for (ModModule module : modules) {
            module.onInit(e);
        }
    }

    void postInit(FMLPostInitializationEvent e) {
        if (stage != 3) return;
        stage = 4;
        logger.info("Post initializing mod");
        for (ModModule module : modules) {
            module.onPostInit(e);
        }
    }

    private void registerFields(Class clazz) {
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
        throw new Exception("Custom registry method wasn't found");
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

    private void queryContainers(Set<ASMData> data) {
        for (ASMData datum : data) {
            ArrayList<String> dependencies = datum.getAnnotationInfo().get("dependencies") == null ? new ArrayList<>() : (ArrayList<String>) datum.getAnnotationInfo().get("dependencies");
            String id = datum.getAnnotationInfo().get("registryId") == null ? "" : (String) datum.getAnnotationInfo().get("registryId");
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

    public class Config {

        private final Pattern BOOLEAN_FIELD = Pattern.compile("[a-zA-Z]+[ ]*=[ ]*(true|false)[ ]*(//(\\w|\\W)*)?");
        private final Pattern DOUBLE_FIELD = Pattern.compile("[a-zA-Z]+[ ]*=[ ]*[0-9]+[.,][0-9]+[ ]*(//(\\w|\\W)*)?");
        private final Pattern INT_FIELD = Pattern.compile("[a-zA-Z]+[ ]*=[ ]*[0-9]+[ ]*(//(\\w|\\W)*)?");
        private final Pattern EMPTY_LINE = Pattern.compile("[ ]*(//(\\w|\\W)*)?");

        private final Pattern VALUE_NAME = Pattern.compile("[a-zA-Z]+");
        private final Pattern BOOLEAN_VALUE = Pattern.compile("(true|false)");
        private final Pattern DOUBLE_VALUE = Pattern.compile("[0-9]+[.,][0-9]+");
        private final Pattern INT_VALUE = Pattern.compile("[0-9]+");

        private final Pattern COMMENT = Pattern.compile("[ ]*(//(\\w|\\W)*)?");

        private final File file;

        private Map<String, Object> values = new LinkedHashMap<>();

        public Config(File file) {
            this.file = file;
            load();
        }

        public Config(String name) {
            this(new File(name));
        }

        private void load() {
            try {
                File folder = new File(file.getParent());
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();

                    Method method = ClassLoader.class.getDeclaredMethod("getClassLoader", Class.class);
                    method.setAccessible(true);
                    ClassLoader loader = (ClassLoader) method.invoke(ClassLoader.class, InitHandler.class);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("config.txt")));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                    while (reader.ready()) {
                        String line = reader.readLine();
                        parseLine(line);
                        writer.write(line + '\n');
                    }
                    writer.close();
                    reader.close();
                } else {
                    BufferedReader reader = new BufferedReader(new FileReader(file));

                    while (reader.ready()) {
                        parseLine(reader.readLine());
                    }

                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Config file wasn't loaded successful");
            }
        }

        private void parseLine(String line) {
            if (BOOLEAN_FIELD.matcher(line).matches()) {
                line = COMMENT.matcher(line).replaceAll("");

                Matcher matcher = VALUE_NAME.matcher(line);
                matcher.find();
                String name = line.substring(matcher.start(), matcher.end());

                matcher = BOOLEAN_VALUE.matcher(line);
                matcher.find();
                Boolean value = Boolean.valueOf(line.substring(matcher.start(), matcher.end()));

                values.put(name, value);
            } else if (INT_FIELD.matcher(line).matches()) {
                line = COMMENT.matcher(line).replaceAll("");

                Matcher matcher = VALUE_NAME.matcher(line);
                matcher.find();

                String name = line.substring(matcher.start(), matcher.end());

                matcher = INT_VALUE.matcher(line);
                matcher.find();
                Integer value = Integer.valueOf(line.substring(matcher.start(), matcher.end()));

                values.put(name, value);
            } else if (DOUBLE_FIELD.matcher(line).matches()) {
                line = COMMENT.matcher(line).replaceAll("");

                Matcher matcher = VALUE_NAME.matcher(line);
                matcher.find();
                String name = line.substring(matcher.start(), matcher.end());

                matcher = DOUBLE_VALUE.matcher(line);
                matcher.find();
                Double value = Double.valueOf(line.substring(matcher.start(), matcher.end()));

                values.put(name, value);
            } else if (!EMPTY_LINE.matcher(line).matches()) {
                throw new RuntimeException("Invalid line format: " + line + '"');
            }
        }

        public boolean getBooleanValue(String key) {
            return getBooleanValue(key, false);
        }

        public boolean getBooleanValue(String key, boolean _default) {
            if (values.containsKey(key)) {
                Object object = values.get(key);

                if (object instanceof Boolean) {
                    return (Boolean) object;
                } else {
                    return _default;
                }
            } else {
                return _default;
            }
        }

        public int getIntValue(String key) {
            return getIntValue(key, 0);
        }

        public int getIntValue(String key, int _default) {
            if (values.containsKey(key)) {
                Object object = values.get(key);

                if (object instanceof Integer) {
                    return (Integer) object;
                } else if (object instanceof Double) {
                    return (int) object;
                } else {
                    return _default;
                }
            } else {
                return _default;
            }
        }

        public double getDoubleValue(String key) {
            return getDoubleValue(key, 0);
        }

        public double getDoubleValue(String key, double _default) {
            if (values.containsKey(key)) {
                Object object = values.get(key);

                if (object instanceof Double) {
                    return (Double) object;
                } else if (object instanceof Integer) {
                    return (double) ((Integer) object);
                } else {
                    return _default;
                }
            } else {
                return _default;
            }
        }
    }

}