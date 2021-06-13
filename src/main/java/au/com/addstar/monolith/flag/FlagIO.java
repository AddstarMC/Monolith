/*
 * Copyright (c) 2021. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package au.com.addstar.monolith.flag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import com.google.common.collect.HashBiMap;

public class FlagIO {
    @SuppressWarnings("rawtypes")
    private static final HashBiMap<String, Class<? extends Flag>> mKnownTypes = HashBiMap.create();

    static {
        mKnownTypes.put("Boolean", BooleanFlag.class);
        mKnownTypes.put("Percent", PercentFlag.class);
        mKnownTypes.put("Integer", IntegerFlag.class);
        mKnownTypes.put("Time", TimeFlag.class);
        mKnownTypes.put("Enum", EnumFlag.class);
        mKnownTypes.put("String", StringFlag.class);
    }

    public static void addKnownType(String type, Class<? extends Flag<?>> typeClass) {
        mKnownTypes.put(type, typeClass);
    }

    public static void saveFlags(Map<String, Flag<?>> flags, ConfigurationSection root) {
        for (Entry<String, Flag<?>> flag : flags.entrySet()) {
            ConfigurationSection section = root.createSection(flag.getKey());
            String typeName;

            if (mKnownTypes.containsValue(flag.getValue().getClass()))
                typeName = mKnownTypes.inverse().get(flag.getValue().getClass());
            else
                typeName = flag.getValue().getClass().getName();

            section.set("~~", typeName);
            flag.getValue().save(section);
        }
    }

    @SuppressWarnings({"rawtypes"})
    public static Map<String, Flag<?>> loadFlags(ConfigurationSection root, Map<String, Flag<?>> existing) throws InvalidConfigurationException {
        HashMap<String, Flag<?>> flags = new HashMap<>();

        for (String key : root.getKeys(false)) {
            if (!root.isConfigurationSection(key))
                continue;

            ConfigurationSection section = root.getConfigurationSection(key);

            String type = section.getString("~~");

            try {
                Class<? extends Flag> clazz;
                if (mKnownTypes.containsKey(type))
                    clazz = mKnownTypes.get(type);
                else
                    clazz = Class.forName(type).asSubclass(Flag.class);

                Flag<?> eFlag = existing.get(key);
                if (eFlag != null && eFlag.getClass() == clazz) {
                    eFlag.read(section);
                    flags.put(key, eFlag);
                } else {

                    if (clazz != null) {
                        Flag<?> flag = clazz.newInstance();
                        flag.read(section);
                        flags.put(key, flag);
                    }
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Flag Load: Unknown class name " + type);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return flags;
    }
}
