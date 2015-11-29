package au.com.addstar.monolith.effects;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

public class EffectParticle extends BaseEffect
{
	private Effect type;
	private Vector range;

	private float speed;
	private int count;

	private Color colour;

	private Object data;

	public EffectParticle( Effect type, Vector range, float speed, int count )
	{
		this(type, null, range, speed, count);
	}

	public EffectParticle( Effect type, Color colour, Vector range, float speed, int count )
	{
		Preconditions.checkArgument(type.getType() == Effect.Type.PARTICLE);
		Preconditions.checkArgument(speed > 0);
		Preconditions.checkArgument(count > 0);

		this.type = type;
		this.range = range.clone();
		this.speed = speed;
		this.count = count;
		this.colour = colour;
	}

	public EffectParticle()
	{
		range = new Vector();
	}

	public Effect getType()
	{
		return type;
	}

	public void setType( Effect type )
	{
		Preconditions.checkArgument(type.getType() == Effect.Type.PARTICLE);
		Preconditions.checkArgument(type.getData() == null);
		this.type = type;
		this.data = null;
	}

	public void setType( Effect type, Object data )
	{
		Preconditions.checkArgument(type.getType() == Effect.Type.PARTICLE);
		Preconditions.checkArgument(type.getData() != null);
		Preconditions.checkArgument(type.getData().isInstance(data), "You can only assign the specific data type required by the effect");

		this.type = type;
		this.data = data;
	}

	public Vector getRange()
	{
		return range;
	}

	public void setRange( Vector range )
	{
		this.range = range;
	}

	public float getSpeed()
	{
		return speed;
	}

	public void setSpeed( float speed )
	{
		Preconditions.checkArgument(speed >= 0);
		this.speed = speed;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount( int count )
	{
		Preconditions.checkArgument(count > 0);
		this.count = count;
	}

	public Color getColour()
	{
		return colour;
	}

	public void setColour( Color colour )
	{
		this.colour = colour;
	}

	public Object getData()
	{
		return data;
	}

	/**
	 * Emits this particle effect so everyone can see it
	 * 
	 * @param location The location to emit at
	 */
	@Override
	public void spawn( Location location )
	{
		// For the effects that need data
		int matId = 0;
		int matData = 0;

		if ( data instanceof Material )
		{
			matId = ((Material) data).getId();
		}
		else if ( data instanceof MaterialData )
		{
			matId = ((MaterialData) data).getItemTypeId();
			matData = ((MaterialData) data).getData();
		}

		int viewRange = 16;

		// To emit a specific colour, you need to set the offsets to the colour
		// code, then set speed to 1 and count to 0
		if ( colour != null )
		{
			float r, g, b;
			switch ( type )
			{
			case COLOURED_DUST:
				// Redstone dust has a default red value that is from 0.8 to
				// 1.0, its random so just average it out
				r = -0.9f + colour.getRed() / 255f;
				g = colour.getGreen() / 255f;
				b = colour.getBlue() / 255f;
				break;
			case POTION_SWIRL:
			case POTION_SWIRL_TRANSPARENT:
				r = colour.getRed() / 255f;
				g = colour.getGreen() / 255f;
				b = colour.getBlue() / 255f;
				break;
			default:
				// Not coloured
				r = g = b = 0;
				break;
			}

			Location temp = new Location(location.getWorld(), 0, 0, 0);
			// Because we have to use very specific params when sending coloured
			// particles, we have to do much of it manually
			for (int i = 0; i < count; ++i)
			{
				double x, y, z;
				x = location.getX() + (RandomUtils.nextDouble() * 2 - 1) * range.getX();
				y = location.getY() + (RandomUtils.nextDouble() * 2 - 1) * range.getY();
				z = location.getZ() + (RandomUtils.nextDouble() * 2 - 1) * range.getZ();

				temp.setX(x);
				temp.setY(y);
				temp.setZ(z);

				location.getWorld().spigot().playEffect(temp, type, matId, matData, r, g, b, 1, 0, viewRange);
			}
		}
		else
		{
			location.getWorld().spigot().playEffect(location, type, matId, matData, (float) range.getX(), (float) range.getY(), (float) range.getZ(), speed, count, viewRange);
		}
	}

	/**
	 * Emits this particle effect so that only {@code player}
	 * can see it
	 * 
	 * @param player The player to emit to
	 * @param location The location to emit at
	 */
	@Override
	public void spawn( Player player, Location location )
	{
		// For the effects that need data
		int matId = 0;
		int matData = 0;

		if ( data instanceof Material )
		{
			matId = ((Material) data).getId();
		}
		else if ( data instanceof MaterialData )
		{
			matId = ((MaterialData) data).getItemTypeId();
			matData = ((MaterialData) data).getData();
		}

		int viewRange = 16;

		// To emit a specific colour, you need to set the offsets to the colour
		// code, then set speed to 1 and count to 0
		if ( colour != null )
		{
			float r, g, b;
			switch ( type )
			{
			case COLOURED_DUST:
				// Redstone dust has a default red value that is from 0.8 to
				// 1.0, its random so just average it out
				r = -0.9f + colour.getRed() / 255f;
				g = colour.getGreen() / 255f;
				b = colour.getBlue() / 255f;
				break;
			case POTION_SWIRL:
			case POTION_SWIRL_TRANSPARENT:
				r = colour.getRed() / 255f;
				g = colour.getGreen() / 255f;
				b = colour.getBlue() / 255f;
				break;
			default:
				// Not coloured
				r = g = b = 0;
				break;
			}

			Location temp = new Location(location.getWorld(), 0, 0, 0);
			// Because we have to use very specific params when sending coloured
			// particles, we have to do much of it manually
			for (int i = 0; i < count; ++i)
			{
				double x, y, z;
				x = location.getX() + (RandomUtils.nextDouble() * 2 - 1) * range.getX();
				y = location.getY() + (RandomUtils.nextDouble() * 2 - 1) * range.getY();
				z = location.getZ() + (RandomUtils.nextDouble() * 2 - 1) * range.getZ();

				temp.setX(x);
				temp.setY(y);
				temp.setZ(z);

				player.spigot().playEffect(temp, type, matId, matData, r, g, b, 1, 0, viewRange);
			}
		}
		else
		{
			player.spigot().playEffect(location, type, matId, matData, (float) range.getX(), (float) range.getY(), (float) range.getZ(), speed, count, viewRange);
		}
	}

	@Override
	public String toString()
	{
		if ( colour != null )
		{
			return String.format("Effect: %s colour: %s count: %d speed: %.1f range: [%.1f,%.1f,%.1f]", type.name(), colour, count, speed, range.getX(), range.getY(), range.getZ());
		}
		else
		{
			return String.format("Effect: %s count: %d speed: %.1f range: [%.1f,%.1f,%.1f]", type.name(), count, speed, range.getX(), range.getY(), range.getZ());
		}
	}
	
	@Override
	public void load( ConfigurationSection section )
	{
		type = Effect.valueOf(section.getString("type"));
		range = section.getVector("range", new Vector());
		speed = (float)section.getDouble("speed");
		count = section.getInt("count");
		
		if (section.contains("color"))
			colour = Color.fromRGB(section.getInt("color"));
		else
			colour = null;
		
		data = null;
		if (section.isConfigurationSection("data"))
		{
			ConfigurationSection dataSection = section.getConfigurationSection("data");
			
			switch (dataSection.getString("="))
			{
			case "mat":
				data = Material.valueOf(dataSection.getString("value"));
				break;
			case "matdata":
				data = Material.valueOf(dataSection.getString("type")).getNewData((byte)dataSection.getInt("data"));
				break;
			}
		}
	}
	
	@Override
	public void save( ConfigurationSection section )
	{
		section.set("type", type.name());
		section.set("range", range);
		section.set("speed", speed);
		section.set("count", count);
		if (colour != null)
			section.set("color", colour.asRGB());
		
		if (data instanceof Material)
		{
			ConfigurationSection dataSection = section.createSection("data");
			dataSection.set("=", "mat");
			dataSection.set("value", ((Material)data).name());
		}
		else if (data instanceof MaterialData)
		{
			ConfigurationSection dataSection = section.createSection("data");
			dataSection.set("=", "matdata");
			dataSection.set("type", ((MaterialData)data).getItemType().name());
			dataSection.set("data", ((MaterialData)data).getData());
		}
	}
}
