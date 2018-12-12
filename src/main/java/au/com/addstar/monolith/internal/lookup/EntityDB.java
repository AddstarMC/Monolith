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

public class EntityDB extends FlatDb<EntityDefinition>
{
	private HashMap<String, EntityDefinition> mNameMap;
	private HashMultimap<EntityDefinition, String> mIdMap;
	
	public EntityDB()
	{
		mNameMap = new HashMap<>();
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

	@Override
	EntityDefinition getObject(String... string) {
		EntityType type = EntityType.valueOf(string[0].toUpperCase());
		String subType = null;
		if (string.length == 2)
			subType = string[1].toUpperCase();

		EntityDefinition def = new EntityDefinition(type, subType);
		return def;
	}

	@Override
	void saveObject(String string, EntityDefinition object) {
		mNameMap.put(string.toLowerCase(), object);
		mIdMap.put(object, string);
	}
}
