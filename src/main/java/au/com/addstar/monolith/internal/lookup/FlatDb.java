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

package au.com.addstar.monolith.internal.lookup;

import au.com.addstar.monolith.Monolith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 13/12/2018.
 */
public abstract class FlatDb<T> {

    public void load(File file) throws IOException {

        try (FileInputStream stream = new FileInputStream(file)) {
            load(stream);
        }
    }

    public void load(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        int count = 0;
        while (reader.ready()) {
            String line = reader.readLine();
            if (line.startsWith("#"))
                continue;
            String[] parts = line.split(",");
            if (parts.length < 2)
                continue;
            String name = parts[0];
            List<String> search = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) continue;
                search.add(parts[i]);
            }
            String[] s = search.toArray(new String[0]);
            T obj = getObject(s);
            if (obj == null)
                continue;
            saveObject(name.toLowerCase(), obj);
            count++;
        }
        Monolith.getInstance().getLogger().log(Level.INFO, this.getClass().getName() + " added " + count + " search items");
    }

    abstract T getObject(String... string);

    abstract void saveObject(String string, T object);
}
