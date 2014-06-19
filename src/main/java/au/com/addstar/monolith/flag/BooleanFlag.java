
package au.com.addstar.monolith.flag;

import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import au.com.addstar.monolith.Monolith;
import au.com.addstar.monolith.command.BadArgumentException;

public class BooleanFlag extends Flag<Boolean>
{
	@Override
	public Boolean parse( Player sender, String[] args ) throws IllegalArgumentException, BadArgumentException
	{
		if(args.length != 1)
			throw new IllegalArgumentException("<value>");
		
		if(!args[0].equalsIgnoreCase("true") && !args[0].equalsIgnoreCase("false"))
			throw new BadArgumentException(0, "Valid values are true, and false");
		
		return Boolean.parseBoolean(args[0]);
	}

	@Override
	public List<String> tabComplete( Player sender, String[] args )
	{
		if(args.length == 1)
			return Monolith.matchStrings(args[0], Arrays.asList("True", "False"));
		
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
		value = section.getBoolean("value");
	}

	@Override
	public String getValueString()
	{
		return value ? "True" : "False";
	}

}
