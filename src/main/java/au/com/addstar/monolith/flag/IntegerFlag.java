
package au.com.addstar.monolith.flag;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import au.com.addstar.monolith.command.BadArgumentException;

public class IntegerFlag extends Flag<Integer>
{
	@Override
	public Integer parse( Player sender, String[] args ) throws IllegalArgumentException, BadArgumentException
	{
		if(args.length != 1)
			throw new IllegalArgumentException("<value>");
		
		try
		{
			return Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e)
		{
			throw new BadArgumentException(0, "Expected an integer");
		}
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
		value = section.getInt("value");
	}

	@Override
	public String getValueString()
	{
		return String.valueOf(value);
	}

}
