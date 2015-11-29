package au.com.addstar.monolith.attachments;

import org.bukkit.Location;

public class StaticAttachment extends Attachment
{
	private final Location location;
	
	public StaticAttachment(Location location)
	{
		this.location = location.clone();
	}
	
	@Override
	public Location getLocation()
	{
		return location;
	}
}
