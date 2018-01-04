package ru.justagod.justacore.initialization.core.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.justagod.justacore.initialization.core.InitHandler;

import java.io.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by JustAGod on 03.01.2018.
 */
public class JsonConfig extends Config {

    private static Logger logger = LogManager.getLogger();

    public JsonConfig(File file, boolean empty) {
        super(file, empty);
    }

    public JsonConfig(File file, Class<?> clazz, boolean empty) {
        super(file, clazz, empty);
    }

    public JsonConfig(String name, boolean empty) {
        super(name, empty);
    }

    public JsonConfig(String name, Class<?> clazz, boolean empty) {
        super(name, clazz, empty);
    }

    @Override
    protected void load() {
        try {
            if (file.getParent() != null) {
                File folder = new File(file.getParent());
                if (!folder.exists()) {
                    folder.mkdirs();
                }
            }
            if (!file.exists()) {
                file.createNewFile();

                InputStream in;
                if (clazz != null) {
                    Method method = ClassLoader.class.getDeclaredMethod("getClassLoader", Class.class);
                    method.setAccessible(true);
                    ClassLoader loader = (ClassLoader) method.invoke(ClassLoader.class, InitHandler.class);
                    in = loader.getResourceAsStream("config.txt");
                } else {
                    in = ClassLoader.getSystemResourceAsStream("config.txt");
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                while (reader.ready()) {
                    String line = reader.readLine();
                    writer.write(line + '\n');
                }
                writer.close();
                reader.close();
                load();
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                JsonElement element = new JsonParser().parse(reader);
                if (!element.isJsonObject()) throw new Exception();
                JsonObject object = element.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
                        if (primitive.isBoolean()) {
                            values.put(entry.getKey(), primitive.getAsBoolean());
                        } else if (primitive.isString()) {
                            values.put(entry.getKey(), primitive.getAsString());
                        } else if (primitive.isNumber()) {
                            values.put(entry.getKey(), primitive.getAsNumber().doubleValue());
                        } else {
                            logger.warn("Not supported format: " + primitive);
                        }
                    }
                    logger.warn("Not supported format: " + entry.getValue());
                }
                reader.close();
            }
        } catch (Exception e) {
            logger.error("Config file wasn't loaded successful", e);
        }
    }

    @Override
    @SuppressWarnings({"unused", "unchecked"})
    public void addDefaultValues(List<ConfigEntry> values) {
        Map<String, Object> extra = (Map<String, Object>) this.values.clone();
        try (JsonWriter writer = new JsonWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.setIndent("    ");
            writer.beginObject();
            for (ConfigEntry value : values) {
                if (value.comment != null) {
                    logger.warn("Comments not supported in Json Config");
                }
                writer.name(value.name);
                if (extra.containsKey(value.name)) {
                    Object val = this.values.get(value.name);
                    if (val instanceof Double) {
                        writer.value((double) val);
                    } else if (val instanceof Boolean) {
                        writer.value((boolean) val);
                    } else if (val instanceof String) {
                        writer.value((String) val);
                    }
                    extra.remove(value.name);
                } else {
                    Object val = value.value;
                    if (val instanceof Double) {
                        writer.value((double) val);
                    } else if (val instanceof Boolean) {
                        writer.value((boolean) val);
                    } else if (val instanceof String) {
                        writer.value((String) val);
                    }
                }
                this.values.put(value.name, value.value);
            }
            for (Map.Entry<String, Object> entry : extra.entrySet()) {
                writer.name(entry.getKey());
                Object val = entry.getValue();
                if (val instanceof Double) {
                    writer.value((double) val);
                } else if (val instanceof Boolean) {
                    writer.value((boolean) val);
                } else if (val instanceof String) {
                    writer.value((String) val);
                }
            }
            writer.endObject();
        } catch (IOException e) {
            logger.error("Exception while creating config", e);
        }
    }
}
