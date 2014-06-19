
package au.com.addstar.monolith.flag;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import au.com.addstar.monolith.command.BadArgumentException;

public class StringFlag extends Flag<String>
{
	@Override
	public String parse( Player sender, String[] args ) throws IllegalArgumentException, BadArgumentException
	{
		if(args.length != 1)
			throw new IllegalArgumentException("<value>");
		
		return args[0];
	}

	@Override
	public List<String> tabComplete( Player sender, String[] args )
	{
		return null;
	}

	@Override
	public void save( ConfigurationSection section )
	{
		section.set("value", value);
	}

	@Override
	public void read( ConfigurationSection section ) throws InvalidConfigurationException
	{
		value = section.getString("value");
	}

	@Override
	public String getValueString()
	{
		return value;
	}

}
