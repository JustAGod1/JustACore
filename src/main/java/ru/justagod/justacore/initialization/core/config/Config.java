package ru.justagod.justacore.initialization.core.config;

import com.google.common.base.Strings;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by JustAGod on 03.01.2018.
 */
public abstract class Config {

    protected final File file;
    protected final Class<?> clazz;
    protected final boolean empty;

    protected LinkedHashMap<String, Object> values = new LinkedHashMap<>();

    public Config(File file, boolean empty) {
        this(file, null, empty);
    }

    public Config(File file, Class<?> clazz, boolean empty) {
        this.file = file;
        this.clazz = clazz;
        this.empty = empty;
        if (!empty) {
            load();
        }
    }

    protected abstract void load();

    public abstract void addDefaultValues(List<ConfigEntry> values);

    public Config(String name, boolean empty) {
        this(new File(name), empty);
    }

    public Config(String name, Class<?> clazz, boolean empty) {
        this(new File(name), clazz, empty);
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

    public static class ConfigEntry {
        final String name;
        final String[] comment;
        final Object value;

        public ConfigEntry(String name, Object value, String... comment) {
            this.name = name;
            this.comment = comment;
            this.value = value;
        }

        public ConfigEntry(String name, Object value) {
            this.name = name;
            this.value = value;
            comment = null;
        }
    }
}
