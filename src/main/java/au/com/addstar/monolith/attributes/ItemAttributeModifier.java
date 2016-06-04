package au.com.addstar.monolith.attributes;

import java.util.Map;
import java.util.UUID;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.NumberConversions;

/**
 * An attribute modifier for items.
 * Has an extra EquipmentSlot specification 
 * to limit where the modifier will apply.
 * This class, just like the parent class, is immutable
 */
public class ItemAttributeModifier extends AttributeModifier implements ConfigurationSerializable
{
	private final EquipmentSlot slot;
	
	public ItemAttributeModifier(String name, double amount, Operation operation)
	{
		this(UUID.randomUUID(), name, amount, operation, null);
	}
	
	public ItemAttributeModifier(String name, double amount, Operation operation, EquipmentSlot slot)
	{
		this(UUID.randomUUID(), name, amount, operation, slot);
	}
	
	public ItemAttributeModifier(UUID uuid, String name, double amount, Operation operation)
	{
		this(uuid, name, amount, operation, null);
	}
	
	public ItemAttributeModifier(UUID uuid, String name, double amount, Operation operation, EquipmentSlot slot)
	{
		super(uuid, name, amount, operation);
		
		// Slot can be null
		this.slot = slot;
	}
	
	/**
	 * Gets the slot where this modifier applies.
	 * @return The slot, or null if it applies everywhere
	 */
	public EquipmentSlot getSlot()
	{
		return slot;
	}
	
	/**
	 * Checks if this attribute modifier is specific to an equipment slot
	 * @return True if it is restricted
	 */
	public boolean isSlotSpecific()
	{
		return slot != null;
	}
	
	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = super.serialize();
		if (slot != null)
			map.put("slot", slot.name());
		
		return map;
	}
	
	public static ItemAttributeModifier deserialize(Map<String, Object> args)
	{
		EquipmentSlot slot = null;
		if (args.containsKey("slot"))
			slot = EquipmentSlot.valueOf((String)args.get("slot"));
		
		return new ItemAttributeModifier((UUID)args.get("uuid"), (String)args.get("name"), NumberConversions.toDouble(args.get("amount")), Operation.values()[NumberConversions.toInt(args.get("operation"))], slot);
	}
}
