package au.com.addstar.monolith.attributes;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.attribute.Attribute;

/**
 * Represents attribute modifiers stored on an item
 */
public interface ItemAttributes
{
	/**
	 * Gets all modifiers that modify the given attribute
	 * @param attribute The modified attribute
	 * @return A collection of ItemAttributeModifier objects
	 */
	public Collection<ItemAttributeModifier> getModifiers(Attribute attribute);
	
	/**
	 * Adds a modifier to this item
	 * @param attribute The attribute to modify
	 * @param modifier The modifier to add. The UUID of this modifier MUST be unique
	 * @throws IllegalArgumentException Thrown if the uuid is not unique
	 */
	public void addModifier(Attribute attribute, ItemAttributeModifier modifier) throws IllegalArgumentException;
	
	/**
	 * Removes a modifier from this item
	 * @param modifier The modifier to remove
	 */
	public void removeModifier(ItemAttributeModifier modifier);
	
	/**
	 * Clears all modifiers that modify the given attribute
	 * @param attribute The attribute to clear modifiers for
	 */
	public void clearModifiers(Attribute attribute);
	
	/**
	 * Clears all modifiers from the item
	 */
	public void clearModifiers();
	
	/**
	 * Gets a modifier by its UUID
	 * @param id The id of the modifier
	 * @return The modifier or null
	 */
	public ItemAttributeModifier getModifier(UUID id);
	
	/**
	 * Gets all modifiers that have the given name
	 * @param name The name, case sensitive
	 * @return A collection of ItemAttributeModifier instances
	 */
	public Collection<ItemAttributeModifier> getModifiers(String name);
}
