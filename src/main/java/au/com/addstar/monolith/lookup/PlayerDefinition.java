package au.com.addstar.monolith.lookup;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerDefinition
{
	private UUID mId;
	private String mName;
	
	public PlayerDefinition(UUID id, String name)
	{
		mId = id;
		mName = name;
	}
	
	public String getName()
	{
		return mName;
	}
	
	public UUID getUniqueId()
	{
		return mId;
	}
	
	public boolean isLocal()
	{
		return getPlayer() != null;
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayer(mId);
	}
	
	public OfflinePlayer getOfflinePlayer()
	{
		return Bukkit.getOfflinePlayer(mId);
	}
}
