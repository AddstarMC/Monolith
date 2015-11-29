package au.com.addstar.monolith.attachments;

import org.bukkit.Location;

import com.google.common.base.Supplier;

public class ComputedAttachment extends Attachment
{
	private final Supplier<Location> supplier;
	
	public ComputedAttachment(Supplier<Location> supplier)
	{
		this.supplier = supplier;
	}
	
	@Override
	public Location getLocation()
	{
		return supplier.get();
	}
}
