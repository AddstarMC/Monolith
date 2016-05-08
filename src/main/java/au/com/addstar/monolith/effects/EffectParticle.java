package au.com.addstar.monolith.effects;

import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class EffectParticle extends BaseEffect
{
	private static final Map<Effect, Particle> EFFECT_CONVERTER;
	
	static
	{
		EFFECT_CONVERTER = Maps.newHashMap();
		EFFECT_CONVERTER.put(Effect.CRIT, Particle.CRIT);
		EFFECT_CONVERTER.put(Effect.MAGIC_CRIT, Particle.CRIT_MAGIC);
		EFFECT_CONVERTER.put(Effect.POTION_SWIRL, Particle.SPELL_MOB);
		EFFECT_CONVERTER.put(Effect.POTION_SWIRL_TRANSPARENT, Particle.SPELL_MOB_AMBIENT);
		EFFECT_CONVERTER.put(Effect.SPELL, Particle.SPELL);
		EFFECT_CONVERTER.put(Effect.INSTANT_SPELL, Particle.SPELL_INSTANT);
		EFFECT_CONVERTER.put(Effect.WITCH_MAGIC, Particle.SPELL_WITCH);
		EFFECT_CONVERTER.put(Effect.NOTE, Particle.NOTE);
		EFFECT_CONVERTER.put(Effect.PORTAL, Particle.PORTAL);
		EFFECT_CONVERTER.put(Effect.FLYING_GLYPH, Particle.ENCHANTMENT_TABLE);
		EFFECT_CONVERTER.put(Effect.FLAME, Particle.FLAME);
		EFFECT_CONVERTER.put(Effect.LAVA_POP, Particle.LAVA);
		EFFECT_CONVERTER.put(Effect.FOOTSTEP, Particle.FOOTSTEP);
		EFFECT_CONVERTER.put(Effect.SPLASH, Particle.WATER_SPLASH);
		EFFECT_CONVERTER.put(Effect.PARTICLE_SMOKE, Particle.SMOKE_NORMAL);
		EFFECT_CONVERTER.put(Effect.EXPLOSION_HUGE, Particle.EXPLOSION_HUGE);
		EFFECT_CONVERTER.put(Effect.EXPLOSION_LARGE, Particle.EXPLOSION_LARGE);
		EFFECT_CONVERTER.put(Effect.EXPLOSION, Particle.EXPLOSION_NORMAL);
		EFFECT_CONVERTER.put(Effect.VOID_FOG, Particle.SUSPENDED_DEPTH);
		EFFECT_CONVERTER.put(Effect.SMALL_SMOKE, Particle.TOWN_AURA);
		EFFECT_CONVERTER.put(Effect.CLOUD, Particle.CLOUD);
		EFFECT_CONVERTER.put(Effect.COLOURED_DUST, Particle.REDSTONE);
		EFFECT_CONVERTER.put(Effect.SNOWBALL_BREAK, Particle.SNOWBALL);
		EFFECT_CONVERTER.put(Effect.WATERDRIP, Particle.DRIP_WATER);
		EFFECT_CONVERTER.put(Effect.LAVADRIP, Particle.DRIP_LAVA);
		EFFECT_CONVERTER.put(Effect.SNOW_SHOVEL, Particle.SNOW_SHOVEL);
		EFFECT_CONVERTER.put(Effect.SLIME, Particle.SLIME);
		EFFECT_CONVERTER.put(Effect.HEART, Particle.HEART);
		EFFECT_CONVERTER.put(Effect.VILLAGER_THUNDERCLOUD, Particle.VILLAGER_ANGRY);
		EFFECT_CONVERTER.put(Effect.HAPPY_VILLAGER, Particle.VILLAGER_HAPPY);
		EFFECT_CONVERTER.put(Effect.LARGE_SMOKE, Particle.SMOKE_LARGE);
		EFFECT_CONVERTER.put(Effect.ITEM_BREAK, Particle.ITEM_CRACK);
		EFFECT_CONVERTER.put(Effect.TILE_BREAK, Particle.BLOCK_CRACK);
		EFFECT_CONVERTER.put(Effect.TILE_DUST, Particle.BLOCK_DUST);
	}
	
	private Particle type;
	private Vector range;

	private float speed;
	private int count;

	private Color colour;

	private Object data;

	@Deprecated
	public EffectParticle( Effect type, Vector range, float speed, int count )
	{
		this(type, null, range, speed, count);
	}
	
	public EffectParticle( Particle type, Vector range, float speed, int count )
	{
		this(type, null, range, speed, count);
	}

	@Deprecated
	public EffectParticle( Effect type, Color colour, Vector range, float speed, int count )
	{
		Preconditions.checkArgument(type.getType() == Effect.Type.PARTICLE);
		Preconditions.checkArgument(speed >= 0);
		Preconditions.checkArgument(count > 0);
		
		this.type = EFFECT_CONVERTER.get(type);
		assert(this.type != null);
		
		this.range = range.clone();
		this.speed = speed;
		this.count = count;
		this.colour = colour;
	}
	
	public EffectParticle( Particle type, Color colour, Vector range, float speed, int count )
	{
		Preconditions.checkArgument(speed >= 0);
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

	public Particle getType()
	{
		return type;
	}

	@Deprecated
	public void setType( Effect type )
	{
		Preconditions.checkArgument(type.getType() == Effect.Type.PARTICLE);
		Preconditions.checkArgument(type.getData() == null);
		this.type = EFFECT_CONVERTER.get(type);
		this.data = null;
	}
	
	public void setType( Particle type )
	{
		Preconditions.checkArgument(type.getDataType() == null);
		this.type = type;
		this.data = null;
	}

	@Deprecated
	public void setType( Effect type, Object data )
	{
		Preconditions.checkArgument(type.getType() == Effect.Type.PARTICLE);
		Preconditions.checkArgument(type.getData() != null);
		Preconditions.checkArgument(type.getData().isInstance(data), "You can only assign the specific data type required by the effect");

		this.type = EFFECT_CONVERTER.get(type);
		if (data instanceof Material)
		{
			// Backwards compatability
			this.data = new ItemStack((Material)data); 
		}
		else
			this.data = data;
	}
	
	public void setType( Particle type, Object data )
	{
		Preconditions.checkArgument(type.getDataType() != null);
		Preconditions.checkArgument(type.getDataType().isInstance(data), "You can only assign the specific data type required by the effect");

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
		// To emit a specific colour, you need to set the offsets to the colour
		// code, then set speed to 1 and count to 0
		if ( colour != null )
		{
			float r, g, b;
			switch ( type )
			{
			case REDSTONE:
				// Redstone dust has a default red value that is from 0.8 to
				// 1.0, its random so just average it out
				r = -0.9f + colour.getRed() / 255f;
				g = colour.getGreen() / 255f;
				b = colour.getBlue() / 255f;
				break;
			case SPELL_MOB:
			case SPELL_MOB_AMBIENT:
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

				if (data != null)
					location.getWorld().spawnParticle(type, temp, 0, r, g, b, 1, data);
				else
					location.getWorld().spawnParticle(type, temp, 0, r, g, b, 1);
			}
		}
		else
		{
			if (data != null)
				location.getWorld().spawnParticle(type, location, count, (float) range.getX(), (float) range.getY(), (float) range.getZ(), speed, data);
			else
				location.getWorld().spawnParticle(type, location, count, (float) range.getX(), (float) range.getY(), (float) range.getZ(), speed);
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
		// To emit a specific colour, you need to set the offsets to the colour
		// code, then set speed to 1 and count to 0
		if ( colour != null )
		{
			float r, g, b;
			switch ( type )
			{
			case REDSTONE:
				// Redstone dust has a default red value that is from 0.8 to
				// 1.0, its random so just average it out
				r = -0.9f + colour.getRed() / 255f;
				g = colour.getGreen() / 255f;
				b = colour.getBlue() / 255f;
				break;
			case SPELL_MOB:
			case SPELL_MOB_AMBIENT:
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

				if (data != null)
					player.spawnParticle(type, temp, 0, r, g, b, 1, data);
				else
					player.spawnParticle(type, temp, 0, r, g, b, 1);
			}
		}
		else
		{
			if (data != null)
				player.spawnParticle(type, location, count, (float) range.getX(), (float) range.getY(), (float) range.getZ(), speed, data);
			else
				player.spawnParticle(type, location, count, (float) range.getX(), (float) range.getY(), (float) range.getZ(), speed);
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
		if (section.isString("ptype"))
			type = Particle.valueOf(section.getString("ptype"));
		else
		{
			Effect oldType = Effect.valueOf(section.getString("type"));
			type = EFFECT_CONVERTER.get(oldType);
		}
		
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
				// Backwards compatability
				data = new ItemStack(Material.valueOf(dataSection.getString("value")));
				break;
			case "matdata":
				data = Material.valueOf(dataSection.getString("type")).getNewData((byte)dataSection.getInt("data"));
				break;
			case "itemstack":
				data = dataSection.getItemStack("value");
				break;
			}
		}
	}
	
	@Override
	public void save( ConfigurationSection section )
	{
		section.set("ptype", type.name());
		section.set("range", range);
		section.set("speed", speed);
		section.set("count", count);
		if (colour != null)
			section.set("color", colour.asRGB());
		
		if (data instanceof MaterialData)
		{
			ConfigurationSection dataSection = section.createSection("data");
			dataSection.set("=", "matdata");
			dataSection.set("type", ((MaterialData)data).getItemType().name());
			dataSection.set("data", ((MaterialData)data).getData());
		}
		else if (data instanceof ItemStack)
		{
			ConfigurationSection dataSection = section.createSection("data");
			dataSection.set("=", "itemstack");
			dataSection.set("value", data);
		}
	}
	
	@Override
	public EffectParticle clone()
	{
		EffectParticle clone = new EffectParticle(type, range.clone(), speed, count);
		
		if (colour != null)
			clone.colour = Color.fromRGB(colour.asRGB());
		
		if (data instanceof ItemStack)
			clone.data = ((ItemStack)data).clone();
		else if (data instanceof MaterialData)
			clone.data = ((MaterialData)data).clone();
		
		return clone;
	}
}
