package au.com.addstar.monolith.internal.lookup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;

import au.com.addstar.monolith.lookup.MaterialDefinition;

import com.google.common.collect.HashMultimap;

public class ItemDB
{
	private HashMap<String, MaterialDefinition> mNameMap;
	private HashMultimap<MaterialDefinition, String> mIdMap;
	
	public ItemDB()
	{
		mNameMap = new HashMap<String, MaterialDefinition>();
		mIdMap = HashMultimap.create();
	}
	
	public MaterialDefinition getByName(String name)
	{
		return mNameMap.get(name.toLowerCase());
	}
	
	public Set<String> getById(MaterialDefinition item)
	{
		return mIdMap.get(item);
	}
	
	public Set<String> getById(Material material, int data)
	{
		return getById(new MaterialDefinition(material, (short)data));
	}
	
	public void load(File file) throws IOException
	{
		FileInputStream stream = new FileInputStream(file);
		
		try
		{
			load(stream);
		}
		finally
		{
			stream.close();
		}
	}
	
	@SuppressWarnings( "deprecation" )
	public void load(InputStream stream) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		mNameMap.clear();
		mIdMap.clear();
		
		while(reader.ready())
		{
			String line = reader.readLine();
			if(line.startsWith("#"))
				continue;
			
			String[] parts = line.split(",");
			if(parts.length != 3)
				continue;
			
			String name = parts[0];
			Material material = null;
			try
			{
				int id = Integer.parseInt(parts[1]);
				material = Material.getMaterial(id);
			}
			catch(NumberFormatException e)
			{
			}
			
			if(material == null)
				material = Material.getMaterial(parts[1]);
			
			if(material == null)
				continue;
			
			byte data = 0;
			try
			{
				data = Byte.parseByte(parts[2]);
				if(data < 0)
					continue;
			}
			catch(NumberFormatException e)
			{
				continue;
			}
			
			MaterialDefinition def = new MaterialDefinition(material, (short)data);
			mNameMap.put(name.toLowerCase(), def);
			mIdMap.put(def, name);
		}
	}
}
