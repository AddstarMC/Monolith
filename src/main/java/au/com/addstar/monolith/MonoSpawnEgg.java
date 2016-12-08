package au.com.addstar.monolith;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.SpawnEgg;
import org.bukkit.inventory.meta.SpawnEggMeta;

/**
 *
 * Deprecated since Spigot 1.11 we now have SpawnEggMeta
 * Created for the AddstarMC Project.
 * Created by Narimm on 26/04/2016.
 */
@Deprecated
public class MonoSpawnEgg extends SpawnEgg {

    private ItemStack item;
    private SpawnEggMeta meta;
    private EntityType spawnType;
    private String customName;
    private boolean customNameVisible;

    public MonoSpawnEgg(ItemStack egg) {
        this.item = egg;
        this.meta = null;
        ItemMeta newmeta = item.getItemMeta();
        try {
            this.meta = (SpawnEggMeta) newmeta;
        } catch (ClassCastException e) {
            e.printStackTrace();//You cant make an egg out of a non egg.
        }
        this.spawnType = meta.getSpawnedType();
        this.customName = meta.getDisplayName();
        this.customNameVisible = true;

    }
    public EntityType getMonoSpawnedType() {
        return this.spawnType;
    }

    public String getCustomName(){
        return this.customName;
    }

    public boolean isCustomNameVisible(){
        return this.customNameVisible;
    }

    /**
     * This sets a Mono Spawn Egg Type it also sets the NBT Data on the underlying item.
     *
     * @param type
     * @return
     */
    public boolean setMonoSpawnedType(EntityType type) {
        meta.setSpawnedType(type);

        return this.meta.getSpawnedType() == type;
}

    /*
    * Creates a 1.8 compatible spawn egg...
     */
    public SpawnEgg getBukkitSpawnEgg(){
        SpawnEgg egg = new MonoSpawnEgg(this.toItemStack());
        egg.setSpawnedType(this.getMonoSpawnedType());
        return egg;
    }

    public ItemStack getItem() {
        return item;
    }
}
