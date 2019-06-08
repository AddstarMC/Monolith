package au.com.addstar.monolith.internal.lookup;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
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
		try {
			EntityType type = EntityType.valueOf(string[0].toUpperCase());
			String subType = null;
			if (string.length == 2)
				subType = string[1].toUpperCase();

			return new EntityDefinition(type, subType);
		}
		catch(IllegalArgumentException e){
			Logger.getLogger(this.getClass().getCanonicalName()).warning(StringUtils.join(string,",")+" cannot be parsed to an entity");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	void saveObject(String string, EntityDefinition object) {
		mNameMap.put(string.toLowerCase(), object);
		mIdMap.put(object, string);
	}
}
