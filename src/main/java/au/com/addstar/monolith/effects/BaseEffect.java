package au.com.addstar.monolith.effects;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class BaseEffect
{
	public abstract void spawn(Location location);
	public abstract void spawn(Player player, Location location);
	
	public abstract void save(ConfigurationSection section);
	public abstract void load(ConfigurationSection section);
}
