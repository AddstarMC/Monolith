package au.com.addstar.monolith.util;

import org.bukkit.attribute.Attribute;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Attributes
{
	private static BiMap<Attribute, String> ids;
	
	static
	{
		ids = HashBiMap.create();
		ids.put(Attribute.GENERIC_ARMOR, "generic.armor");
		ids.put(Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage");
		ids.put(Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed");
		ids.put(Attribute.GENERIC_FOLLOW_RANGE, "generic.followRange");
		ids.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, "generic.knockbackResistance");
		ids.put(Attribute.GENERIC_LUCK, "generic.luck");
		ids.put(Attribute.GENERIC_MAX_HEALTH, "generic.maxHealth");
		ids.put(Attribute.GENERIC_MOVEMENT_SPEED, "generic.movementSpeed");
		ids.put(Attribute.HORSE_JUMP_STRENGTH, "horse.jumpStrength");
		ids.put(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS, "zombie.spawnReinforcements");
		ids.put(Attribute.GENERIC_FLYING_SPEED,"generic.flySpeed");
		ids.put(Attribute.GENERIC_ARMOR_TOUGHNESS,"generic.armorToughness");
	}
	
	public static Attribute fromId(String id)
	{
		return ids.inverse().get(id);
	}
	
	public static String getId(Attribute attribute)
	{
		return ids.get(attribute);
	}
}
