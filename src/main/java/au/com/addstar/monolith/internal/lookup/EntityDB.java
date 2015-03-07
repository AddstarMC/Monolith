package au.com.addstar.monolith.internal.lookup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.entity.EntityType;

import au.com.addstar.monolith.lookup.EntityDefinition;

import com.google.common.collect.HashMultimap;

public class EntityDB
{
	private HashMap<String, EntityDefinition> mNameMap;
	private HashMultimap<EntityDefinition, String> mIdMap;
	
	public EntityDB()
	{
		mNameMap = new HashMap<String, EntityDefinition>();
		mIdMap = HashMultimap.create();
	}
	
	public EntityDefinition getByName(String name)
	{
		return mNameMap.get(name.toLowerCase());
	}
	
	public Set<String> getByType(EntityDefinition item)
	{
		return mIdMap.get(item);
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
			if(parts.length != 2 && parts.length != 3)
				continue;
			
			String name = parts[0];
			EntityType type = EntityType.valueOf(parts[1].toUpperCase());
			if(type == null)
				continue;
			
			String subType = null;
			if (parts.length == 3)
				subType = parts[2].toUpperCase();
			
			EntityDefinition def = new EntityDefinition(type, subType);
			mNameMap.put(name.toLowerCase(), def);
			mIdMap.put(def, name);
		}
	}
}
