package au.com.addstar.monolith.lookup;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Skeleton.SkeletonType;

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
		
		if (entity instanceof Skeleton)
			mSubType = ((Skeleton)entity).getSkeletonType().name();
		else if (entity instanceof Creeper)
		{
			if (((Creeper)entity).isPowered())
				mSubType = "POWERED";
		}
		else if (entity instanceof Rabbit)
			mSubType = ((Rabbit)entity).getRabbitType().name();
		else if (entity instanceof Horse)
			mSubType = ((Horse)entity).getVariant().name();
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
	
	public Entity createEntity(Location location)
	{
		Entity entity = location.getWorld().spawnEntity(location, mType);
		
		if (entity == null)
			return null;
		
		if (mSubType != null)
		{
			switch (mType)
			{
			case SKELETON:
				((Skeleton)entity).setSkeletonType(SkeletonType.valueOf(mSubType.toUpperCase()));
				break;
			case CREEPER:
				if (mSubType.equalsIgnoreCase("powered"))
					((Creeper)entity).setPowered(true);
				break;
			case HORSE:
				((Horse)entity).setVariant(Variant.valueOf(mSubType.toUpperCase()));
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
