package au.com.addstar.monolith.template;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import au.com.addstar.monolith.lookup.EntityDefinition;

public class EntityTemplate extends AbstractTemplate<EntityDefinition, Entity>
{
	private EntityDefinition mType;
	
	public EntityTemplate(EntityDefinition type)
	{
		mType = type;
	}
	
	@Override
	public EntityDefinition getType()
	{
		return mType;
	}

	public Entity createAt(Location location)
	{
		Entity entity = mType.createEntity(location);
		if (entity == null)
			return null;
		
		applyTemplate(entity);
		return entity;
	}
}
