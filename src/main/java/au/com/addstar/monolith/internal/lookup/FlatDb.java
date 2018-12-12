package au.com.addstar.monolith.internal.lookup;

import au.com.addstar.monolith.Monolith;

import org.bukkit.Material;

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

    public void load(File file) throws IOException
    {

        try (FileInputStream stream = new FileInputStream(file)) {
            load(stream);
        }
    }

    public void load(InputStream stream) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        int count  = 0;
        while(reader.ready())
        {
            String line = reader.readLine();
            if(line.startsWith("#"))
                continue;
            String[] parts = line.split(",");
            if(parts.length < 2)
                continue;
            String name = parts[0];
            List<String> search = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                if(i == 0)continue;
                search.add(parts[i]);
            }
            String[] s = search.toArray(new String[0]);
            T obj = getObject(s);
            if(obj == null)
                continue;
            saveObject(name.toLowerCase(),obj);
            count++;
        }
        Monolith.getInstance().getLogger().log(Level.INFO,this.getClass().getName()+" added " +count+ " search items" );
    }

    abstract T getObject(String... string);

    abstract void saveObject(String string, T object);
}
