package ru.justagod.justacore.initialization.core.config;

import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.justagod.justacore.initialization.core.InitHandler;

import java.io.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JustAGod on 03.01.2018.
 */
public class TextConfig extends Config {

    private final Pattern BOOLEAN_FIELD = Pattern.compile("[ ]*[a-zA-Z.,]+[ ]*=[ ]*(true|false)[ ]*(//(\\w|\\W)*)?");
    private final Pattern DOUBLE_FIELD = Pattern.compile("[ ]*[a-zA-Z.,]+[ ]*=[ ]*[0-9]+[.,][0-9]+[ ]*(//(\\w|\\W)*)?");
    private final Pattern STRING_FIELD = Pattern.compile("[ ]*[a-zA-Z.,]+[ ]*=[ ]*[a-zA-Z.,а-яА-Я]+[ ]*(//(\\w|\\W)*)?");
    private final Pattern INT_FIELD = Pattern.compile("[ ]*[a-zA-Z.,]+[ ]*=[ ]*[0-9]+[ ]*(//(\\w|\\W)*)?");
    private final Pattern EMPTY_LINE = Pattern.compile("[ ]*(//(\\w|\\W)*)?");

    private final Pattern VALUE_NAME = Pattern.compile("[a-zA-Z.,]+");
    private final Pattern BOOLEAN_VALUE = Pattern.compile("(true|false)");
    private final Pattern DOUBLE_VALUE = Pattern.compile("[0-9]+[.,][0-9]+");
    private final Pattern INT_VALUE = Pattern.compile("[0-9]+");

    private final Pattern COMMENT = Pattern.compile("[ ]*(//(\\w|\\W)*)?");

    private static Logger logger = LogManager.getLogger();

    public TextConfig(File file, boolean empty) {
        super(file, empty);
    }

    public TextConfig(File file, Class<?> clazz, boolean empty) {
        super(file, clazz, empty);
    }

    public TextConfig(String name, boolean empty) {
        super(name, empty);
    }

    public TextConfig(String name, Class<?> clazz, boolean empty) {
        super(name, clazz, empty);
    }

    @SuppressWarnings("all")
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
            logger.error("Config file wasn't loaded successful", e);
        }
    }

    /**
     * Добавляет дефолтные параметры к конфигу. Если параметр был переписан пользователем, он изменен
     * не будет.
     *
     * @param values Дефолтные параметры конфига
     */
    @Override
    @SuppressWarnings({"unused", "unchecked"})
    public void addDefaultValues(List<Config.ConfigEntry> values) {
        Map<String, Object> extra = (Map<String, Object>) this.values.clone();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (ConfigEntry value : values) {
                if (value.comment != null) {
                    for (String s : value.comment) {
                        writer.write(String.format("//%s", s));
                        writer.newLine();
                    }
                }
                if (extra.containsKey(value.name)) {
                    writer.write(String.format("%s = %s", value.name, this.values.get(value.name)));
                    extra.remove(value.name);
                } else {
                    writer.write(String.format("%s = %s", value.name, value.value));
                }
                writer.newLine();
                this.values.put(value.name, value.value);
            }
            for (Map.Entry<String, Object> entry : extra.entrySet()) {
                writer.write(String.format("%s = %s", entry.getKey(), entry.getValue()));
                writer.newLine();
            }

        } catch (IOException e) {
            logger.error("Exception while creating config", e);
        }

    }

    @SuppressWarnings("all")
    private void parseLine(String line) {
        line = line.replaceAll("=", " = ");
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
        } else if (STRING_FIELD.matcher(line).matches()) {
            line = COMMENT.matcher(line).replaceAll("");

            Matcher matcher = VALUE_NAME.matcher(line);
            matcher.find();
            String name = line.substring(matcher.start(), matcher.end());


            String value = line.replaceFirst(name, "").replaceFirst("=", "").trim();

            values.put(name, value);
        } else if (!EMPTY_LINE.matcher(line).matches()) {
            throw new RuntimeException("Invalid line format: " + line + '"');
        }
    }

    @SuppressWarnings("all")
    public boolean getBooleanValue(String key) {
        return getBooleanValue(key, false);
    }

    @SuppressWarnings("all")
    public boolean getBooleanValue(String key, boolean _default) {
        if (Strings.isNullOrEmpty(key)) return true;
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

    @SuppressWarnings("unused")
    public int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    @SuppressWarnings("all")
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

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public String getStringValue(String key) {
        return getStringValue(key, "");
    }

    public String getStringValue(String key, String _default) {
        return values.getOrDefault(key, _default).toString();
    }


}
