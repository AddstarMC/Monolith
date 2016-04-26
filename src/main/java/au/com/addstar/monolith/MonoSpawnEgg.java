package au.com.addstar.monolith;

import net.minecraft.server.v1_9_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

import java.lang.reflect.Field;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 26/04/2016.
 */
public class MonoSpawnEgg extends SpawnEgg {

    private SpawnEgg egg;
    private ItemStack item;
    private EntityType spawnType;
    private String customName;
    private boolean customNameVisible;

    public MonoSpawnEgg(ItemStack egg) {
        this.egg = (SpawnEgg) egg.getData();
        this.item = egg;
        this.spawnType = EntityType.CHICKEN;
        this.customName = null;
        net.minecraft.server.v1_9_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsStack.getTag();
        if (tag != null) {
            try {
                if (tag.hasKey("EntityTag") && tag.hasKeyOfType("EntityTag", 10)) {
                    NBTTagCompound entityTag = tag.getCompound("EntityTag");
                    if (entityTag.hasKeyOfType("id", 8)) {
                        this.spawnType = EntityType.fromName(entityTag.getString("id"));
                    }
                    if (entityTag.hasKeyOfType("CustomName",8)){
                        this.customName = entityTag.getString("CustomName");
                    }
                    if (entityTag.hasKeyOfType("CustomNameVisible",1)){
                        this.customNameVisible = entityTag.getBoolean("CustomNameVisible");
                    }
                }else{
                    NBTTagCompound entityTag = new NBTTagCompound();
                    entityTag.setString("id", EntityType.CHICKEN.getName());
                    tag.set("EntityTag", entityTag);
                    nmsStack.setTag(tag);
                    this.item = CraftItemStack.asBukkitCopy(nmsStack);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
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

    public boolean setMonoSpawnedType(EntityType type) {
        net.minecraft.server.v1_9_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsStack.getTag();
        if (tag != null) {
            try {
                if (tag.hasKey("EntityTag") && tag.hasKeyOfType("EntityTag", 10)) {
                    NBTTagCompound entityTag = tag.getCompound("EntityTag");
                    if (entityTag.hasKeyOfType("id", 8)) {
                        entityTag.setString("id", type.getName());
                    }
                }else{
                    NBTTagCompound entityTag = new NBTTagCompound();
                    entityTag.setString("id", type.getName());
                    tag.set("EntityTag", entityTag);
                    nmsStack.setTag(tag);
                    this.item = CraftItemStack.asBukkitCopy(nmsStack);
                }
                this.spawnType = type;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.spawnType==type;

}

    public SpawnEgg getBukkitSpawnEgg(){
        egg.setSpawnedType(this.getMonoSpawnedType());
        return egg;
    }


}
