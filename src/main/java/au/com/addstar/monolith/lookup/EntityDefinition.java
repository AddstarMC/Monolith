package au.com.addstar.monolith.lookup;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.inventory.ItemStack;

public class EntityDefinition
{
	private EntityType mType;
	private String mSubType;
	
	public EntityDefinition(EntityType type, String subtype)
	{
		mType = type;
		mSubType = subtype;
	}
	
	public EntityDefinition(Entity entity)
	{
		mType = entity.getType();

		if (entity instanceof Creeper)
		{
			if (((Creeper)entity).isPowered())
				mSubType = "POWERED";
		}
		else if (entity instanceof Rabbit)
			mSubType = ((Rabbit)entity).getRabbitType().name();
		else if (entity instanceof Ocelot)
			mSubType = ((Ocelot)entity).getCatType().name();
	}
	
	public EntityType getType()
	{
		return mType;
	}
	
	public String getSubType()
	{
		return mSubType;
	}
	
	public boolean isSpawnable()
	{
		switch (mType)
		{
		case COMPLEX_PART:
		case UNKNOWN:
		case PLAYER:
		case WEATHER:
		case FISHING_HOOK:
		case SPLASH_POTION:
			return false;
		default:
			return true;
		}
	}
	
	public Entity createEntity(Location location)
	{
		World world = location.getWorld();
		Entity entity;
		
		switch (mType)
		{
		case DROPPED_ITEM:
			entity = world.dropItem(location, new ItemStack(Material.STONE));
			break;
		default:
			entity = world.spawnEntity(location, mType);
			break;
		}
		
		if (entity == null)
			return null;
		
		if (mSubType != null)
		{
			switch (mType)
			{
			case CREEPER:
				if (mSubType.equalsIgnoreCase("powered"))
					((Creeper)entity).setPowered(true);
				break;
			case RABBIT:
				((Rabbit)entity).setRabbitType(Type.valueOf(mSubType.toUpperCase()));
				break;
			case OCELOT:
				((Ocelot)entity).setCatType(org.bukkit.entity.Ocelot.Type.valueOf(mSubType.toUpperCase()));
				break;
			// No other subtypes
			default:
				break;
			}
		}
		
		return entity;
	}
	
	@Override
	public String toString()
	{
		if (mSubType != null)
			return String.format("%s:%s", mType.name(), mSubType);
		else
			return mType.name();
	}
}
