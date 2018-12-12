package au.com.addstar.monolith.internal.lookup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;


import com.google.common.collect.HashMultimap;

import au.com.addstar.monolith.Monolith;

public class ItemDB
{
	private HashMap<String, Material> mNameMap;
	private HashMultimap<Material, String> mIdMap;
	
	public ItemDB()
	{
		mNameMap = new HashMap<>();
		mIdMap = HashMultimap.create();
	}
	
	public Material getByName(String name)
	{
		return mNameMap.get(name.toLowerCase());
	}
	
	public Set<String> getbyMaterial(Material mat)
	{
		return mIdMap.get(mat);
	}
	
	
	public void load(File file) throws IOException
	{
		
		try (FileInputStream stream = new FileInputStream(file)) {
			load(stream);
		}
	}
	
	public void load(InputStream stream) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		mNameMap.clear();
		mIdMap.clear();
		int count  = 0;
		while(reader.ready())
		{
			String line = reader.readLine();
			if(line.startsWith("#"))
				continue;
			String[] parts = line.split(",");
			if(parts.length != 2)
				continue;
			String name = parts[0];
			Material material;
			material = Material.getMaterial(parts[1]);
			if(material == null)
				continue;
			mNameMap.put(name.toLowerCase(), material);
			mIdMap.put(material, name);
			count++;
		}
		Monolith.getInstance().getLogger().log(Level.INFO,"ItemDB added " +count+ " search items" );
	}
}
