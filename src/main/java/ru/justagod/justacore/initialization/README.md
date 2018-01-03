# Initialization



Initialization is part of JustACore based on java annotation that simplify mod's initialization. 
It provides:
 - Modules
 - Advanced and automatic configs
 - Automatic registration of Items, Tiles, Item Renderers and Blocks

# Setup
Setup is very easy! You just have to type following text in your main mod class:
```java
@Mod(modid = "modid", name = "Simple Mod", version = "0.1", dependencies = "required-after:jac")
public class Main {}
```
Looks very simple, I think. You can extend it with some more annotations.
```java
@ConfigExtra(name = "Simple.txt")
@Mod(modid = "jac", name = "JustACore", version = "0.1")
public class Main {
    @ConfigHolder
    public static final Config config = null;
}
```
Now JustACore will create config file with name: Simple.txt(default is config-\<modid\>.txt) and automatically assign config instance to field config. Yes it's ancient magic.
# Config setup

Just a core provides you useful config's system. So you often want to add default values to your config or create config file with predefined text. So you can just add file with name config.txt to your resource root. Config manager will automatically create file if not exists and copy text from this file. Or you can simply write this text in your __Initialization__ stage: 
```java
@ConfigExtra(name = "Simple.txt")
@Mod(modid = "jac", name = "JustACore", version = "0.1")
public class Main {

    @ConfigHolder
    public static final Config config = null;

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        List<Config.ConfigEntry> entries = new LinkedList<>();
        entries.add(new Config.ConfigEntry("Sample", "Sample value", "Just for example"));
        config.addDefaultValues(entries);
    }
}
```
First parameter is field name. Second is field value. Third parameter' __s__ are comments. I think it's quiet simple.

# What can you do now

Now you doesn't need to call all your modules from your common proxy. Doesn't need to look up for loaded mods and than call your integration modules. My library will do it for you.

# Modules

There is basic implementation of module in example. It's methods will invoke automaticaly.
```java
@Module
public class SimpleModule implements ModModule {
    @Override
    public void onPreInit(FMLPreInitializationEvent e) {
        
    }

    @Override
    public void onInit(FMLInitializationEvent e) {
        System.out.println("Hello, world!");
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent e) {

    }
}
```

You can add dependencies to your module. It will looks like this:
```java
@Module(dependencies = {"Waila", "pich"})
```
Now your module will be invoked only if mods Waila and pich is loaded.


If your class has not public default constructor you have to add static method to your class that will return instance of it. This method must have annotation @InstanceFactory and it can be private. 
```java
@Module
public class SimpleModule implements ModModule {

    private SimpleModule() {
    }

    @InstanceFactory
    private static SimpleModule build() {
        return new SimpleModule();
    }
    
    @Override
    public void onPreInit(FMLPreInitializationEvent e) {

    }

    @Override
    public void onInit(FMLInitializationEvent e) {
        System.out.println("Hello, world!");
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent e) {

    }
}

```
If your module is interface, abstract class or etc, you musten't to create instance of it.
You can just add static methods. For example: 
```java
@Module
public abstract class SimpleModule {
    @ModuleEventHandler
    private static void onInit(FMLInitializationEvent e) {
        System.out.println("Hello, world!");
    }
}

```
And it will work fine!

Annotation Module(and a lot of another) also has attribute "configDependency".  

# Auto registry

Auto registry isn't so usefull thing as modules, but I like it anyway.
So what is it? It's system based on annotations!

Block with auto registry:
```java
@RegistryObject
public class BlockGui extends Block {

    public BlockGui() {
        super(Material.iron);
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return Blocks.bookshelf.getIcon(p_149691_1_, p_149691_2_);
    }

    @InstanceFactory
    public static BlockGui create() {
        return new BlockGui();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float clickX, float clickY, float clickZ) {
        if (world.isRemote) new SimpleGui().open();
        return true;
    }
}
```
So what will InitHandler do? 
 - It will find your class at Constuction event
 - Try to take registration id from annotation
 - If id == "" it create it from name of your class.(BlockBlackSkirt = black_skirt ItemGrayWand = gray_wand and etc)
 - Try to find method with annotation @InstanceFactory. And take instance of object from it.
 - If there isnt this method in class it try to create instance via default constructor
 - And just registrate it in Minecraft!

Also there is @RegistryObjectsContainer annotation. If you write it above your class, InitHandler try to find public static fields that extends Item or Block and register it.
(See RegistryExcept, RegistrySpecial and RegistryBlockSpecial)

Thats all! Thank you for your attention.
