package au.com.addstar.monolith.attachments;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class EntityAttachment<T extends Entity> extends Attachment
{
	private final T entity;
	private final Function<? super T, Vector> offsetFunction;
	
	private final Location locationCache;
	
	public EntityAttachment(T entity)
	{
		this(entity, Functions.constant(new Vector(0, 0, 0)));
	}
	
	public EntityAttachment(T entity, Vector offset)
	{
		this(entity, Functions.constant(offset));
	}
	
	public EntityAttachment(T entity, Function<? super T, Vector> offsetFunction)
	{
		this.entity = entity;
		this.offsetFunction = offsetFunction;
		
		locationCache = entity.getLocation();
	}
	
	@Override
	public Location getLocation()
	{
		entity.getLocation(locationCache);
		
		Vector offset = offsetFunction.apply(entity);
		locationCache.add(offset);
		return locationCache;
	}
}
