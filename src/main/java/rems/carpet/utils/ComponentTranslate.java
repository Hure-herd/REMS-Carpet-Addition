/*
 * This file is part of the Carpet AMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 A Minecraft Server and contributors
 *
 * Carpet AMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet AMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet AMS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

package rems.carpet.utils;

import carpet.utils.Messenger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.java.accessibility.util.Translator;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ComponentTranslate {
    public static Map<String, String> getTranslationFromResourcePath(String lang)
    {
        InputStream langFile = ComponentTranslate.class.getClassLoader().getResourceAsStream("assets/rems/lang/%s.json".formatted(lang));
        if (langFile == null) {
            return Collections.emptyMap();
        }
        String jsonData;
        try {
            jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        Gson gson = new GsonBuilder().setLenient().create();

        Map<String, String> map = gson.fromJson(jsonData, new TypeToken<Map<String, String>>() {}.getType());

        if (map != null) {
            localDictionary = map;
        }

        return map;
    }

    private static Map<String, String> localDictionary = new HashMap<>();

    public static MutableText tr(String path, Object... args) {
        String fullKey = "rems." + path;
        String text = localDictionary.getOrDefault(fullKey, fullKey);

        if (args != null && args.length > 0 && !text.equals(fullKey)) {
            text = String.format(text, args);
        }

        return  Text.literal(text);
    }

    public static MutableText error(String path, Object... args) {
        return tr(path, args).formatted(Formatting.RED);
    }
}