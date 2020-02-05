/*
 * Copyright (c) 2020. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package au.com.addstar.monolith.template;

import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.Rotation;
import org.bukkit.TreeSpecies;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.google.common.collect.Iterables;

import au.com.addstar.monolith.template.internal.EntityTemplateSetting;
import au.com.addstar.monolith.util.DynamicEnum;
import static au.com.addstar.monolith.template.internal.EntityTemplateSetting.*;

@SuppressWarnings("rawtypes")
public final class EntitySettings
{
	public static final class All extends DynamicEnum<EntityTemplateSetting>
	{
		private All()
		{
			super(EntityTemplateSetting.class);
		}
		
		public static final EntityTemplateSetting<String> CustomName = create("customName", Entity.class, "setCustomName", String.class, "displayName", "name");
		public static final EntityTemplateSetting<Boolean> CustomNameVisible = createWithDefault("customNameVisible", Entity.class, "setCustomNameVisible", false, "displayNameVisible", "nameVisible");
		public static final EntityTemplateSetting<Vector> Velocity = create("velocity", Entity.class, "setVelocity", Vector.class);
		public static final EntityTemplateSetting<Float> FallDistance = create("fallDistance", Entity.class, "setFallDistance", Float.class);
		public static final EntityTemplateSetting<Integer> FireTicks = create("fireTicks", Entity.class, "setFireTicks", Integer.class);
		public static final EntityTemplateSetting<Boolean> Gravity = create("gravity", Entity.class, "setGravity", Boolean.class);
		public static final EntityTemplateSetting<Boolean> Silent = createWithDefault("silent", Entity.class, "setSilent", Boolean.class, false);
		public static final EntityTemplateSetting<Boolean> Invulnerable = createWithDefault("invulnerable", Entity.class, "setInvulnerable", Boolean.class, false, "invincible");
		public static final EntityTemplateSetting<Boolean> Glowing = createWithDefault("glowing", Entity.class, "setGlowing", Boolean.class, false);
		
		private static final All instance = new All();
		public static All getInstance()
		{
			return instance;
		}
	}
	
	public static final class Living extends DynamicEnum<EntityTemplateSetting>
	{
		private Living()
		{
			super(EntityTemplateSetting.class);
		}
		
		public static final EntityTemplateSetting<Boolean> CanPickupItems = createWithDefault("canPickupItems", LivingEntity.class, "setCanPickupItems", false, "allowPickup");
		public static final EntityTemplateSetting<Integer> MaximumAir = create("maximumAir", LivingEntity.class, "setMaximumAir", Integer.class, "maxAir");
		public static final EntityTemplateSetting<Integer> MaximumNoDamageTicks = create("maximumNoDamageTicks", LivingEntity.class, "setMaximumNoDamageTicks", Integer.class, "maxNoDamageTicks", "maxNoDmgTicks");
		public static final EntityTemplateSetting<Integer> NoDamageTicks = create("noDamageTicks", LivingEntity.class, "setNoDamageTicks", Integer.class, "noDmgTicks");
		public static final EntityTemplateSetting<Boolean> Persistent = createWithDefault("persistent", LivingEntity.class, "setRemoveWhenFarAway", Boolean.class, false);
		public static final EntityTemplateSetting<Integer> RemainingAir = create("remainingAir", LivingEntity.class, "setRemainingAir", Integer.class);
		public static final EntityTemplateSetting<Boolean> Collidable = create("collidable", LivingEntity.class, "setCollidable", Boolean.class);
		public static final EntityTemplateSetting<Boolean> AI = create("ai", LivingEntity.class, "setAI", Boolean.class);
		public static final EntityTemplateSetting<Boolean> Gliding = createWithDefault("gliding", LivingEntity.class, "setGliding", Boolean.class, false);
		
		private static final Living instance = new Living();
		public static Living getInstance()
		{
			return instance;
		}
	}
	
	public static final class Damageable extends DynamicEnum<EntityTemplateSetting>
	{
		private Damageable()
		{
			super(EntityTemplateSetting.class);
		}
		
		public static final EntityTemplateSetting<Double> Health = createWithDefault("health", org.bukkit.entity.Damageable.class, "setHealth", 20.0);
		private static final Damageable instance = new Damageable();
		public static Damageable getInstance()
		{
			return instance;
		}
	}
	
	public static final class Explosive extends DynamicEnum<EntityTemplateSetting>
	{
		private Explosive()
		{
			super(EntityTemplateSetting.class);
		}
		
		public static final EntityTemplateSetting<Float> Yield = create("yield", org.bukkit.entity.Explosive.class, "setYield", Float.class);
		public static final EntityTemplateSetting<Boolean> Incendiary = create("incendiary", org.bukkit.entity.Explosive.class, "setIsIncendiary", Boolean.class);
		
		private static final Explosive instance = new Explosive();
		public static Explosive getInstance()
		{
			return instance;
		}
	}
	
	public static final class Projectile extends DynamicEnum<EntityTemplateSetting>
	{
		private Projectile()
		{
			super(EntityTemplateSetting.class);
		}
		
		public static final EntityTemplateSetting<Boolean> Bounce = create("bounce", org.bukkit.entity.Projectile.class, "setBounce", Boolean.class);
		
		private static final Projectile instance = new Projectile();
		public static Projectile getInstance()
		{
			return instance;
		}
	}
	
	public static final class Ageable extends DynamicEnum<EntityTemplateSetting>
	{
		private Ageable()
		{
			super(EntityTemplateSetting.class);
		}
		
		public static final EntityTemplateSetting<Integer> Age = create("age", org.bukkit.entity.Ageable.class, "setAge", Integer.class);
		public static final EntityTemplateSetting<Boolean> CanBreed = create("canBreed", org.bukkit.entity.Ageable.class, "setBreed", Boolean.class);
		public static final EntityTemplateSetting<Boolean> AgeLocked = create("ageLocked", org.bukkit.entity.Ageable.class, "setAgeLock", Boolean.class);
		public static final EntityTemplateSetting<Boolean> Baby = create("baby", org.bukkit
				.entity.Ageable.class, "setBaby", Boolean.class);
		public static final EntityTemplateSetting<Boolean> Adult = create("adult", org.bukkit
				.entity.Ageable.class, "setAdult", Boolean.class);
		
		private static final Ageable instance = new Ageable();
		public static Ageable getInstance()
		{
			return instance;
		}
	}
	public static final class MineCart extends DynamicEnum<EntityTemplateSetting>{
		private MineCart(){
			super(EntityTemplateSetting.class);
		}
		public static final EntityTemplateSetting<Double> Damage = create("damage", Minecart
				.class, "setDamage", Double.class);
		public static final EntityTemplateSetting<BlockData> DisplayBlock = create
				("displayBlock", Minecart.class, "setDisplayBlockData", BlockData.class);
		public static final EntityTemplateSetting<Integer> DisplayBlockOffset = create("displayBlockOffset", Minecart.class, "setDisplayBlockOffset", Integer.class);
		public static final EntityTemplateSetting<Double> MaxSpeed = create("maxSpeed", Minecart.class, "setMaxSpeed", Double.class);
		public static final EntityTemplateSetting<Boolean> SlowWhenEmpty = create("slowWhenEmpty", Minecart.class, "setSlowWhenEmpty", Boolean.class);
		public static final EntityTemplateSetting<Vector> DerailedVelocityMod = create("derailedVelocity", Minecart.class, "setDerailedVelocityMod", Vector.class);
		public static final EntityTemplateSetting<Vector> FlyingVelocityMod = create("flyingVelocity", Minecart.class, "setFlyingVelocityMod", Vector.class);
		
	}
	public static final class Armorstand extends DynamicEnum<EntityTemplateSetting>{
		private Armorstand(){
			super(EntityTemplateSetting.class);
		}
		public static final EntityTemplateSetting<ItemStack> Boots = create("boots", ArmorStand.class, "setBoots", ItemStack.class);
		public static final EntityTemplateSetting<ItemStack> Chestplate = create("chestplate", ArmorStand.class, "setChestplate", ItemStack.class);
		public static final EntityTemplateSetting<ItemStack> Helmet = create("helmet", ArmorStand.class, "setHelmet", ItemStack.class);
		public static final EntityTemplateSetting<ItemStack> Leggings = create("leggings", ArmorStand.class, "setLeggings", ItemStack.class);
		public static final EntityTemplateSetting<ItemStack> HeldItem = create("heldItem", ArmorStand.class, "setItemInHand", ItemStack.class, "hand", "held");
		public static final EntityTemplateSetting<Boolean> Base = createWithDefault("baseplate", ArmorStand.class, "setBasePlate", Boolean.class, true);
		public static final EntityTemplateSetting<Boolean> Arms = createWithDefault("arms", ArmorStand.class, "setArms", Boolean.class, false);
		public static final EntityTemplateSetting<Boolean> IsSmall = createWithDefault("small", ArmorStand.class, "setSmall", Boolean.class, false);
		public static final EntityTemplateSetting<Boolean> Visible = createWithDefault("visible", ArmorStand.class, "setVisible", Boolean.class, true);
		public static final EntityTemplateSetting<Boolean> Marker = createWithDefault("marker", ArmorStand.class, "setMarker", Boolean.class, false);
		public static final EntityTemplateSetting<EulerAngle> PoseBody = create("bodyPose", ArmorStand.class, "setBodyPose", EulerAngle.class);
		public static final EntityTemplateSetting<EulerAngle> PoseHead = create("headPose", ArmorStand.class, "setHeadPose", EulerAngle.class);
		public static final EntityTemplateSetting<EulerAngle> PoseLeftArm = create("leftArmPose", ArmorStand.class, "setLeftArmPose", EulerAngle.class);
		public static final EntityTemplateSetting<EulerAngle> PoseRightArm = create("rightArmPose", ArmorStand.class, "setRightArmPose", EulerAngle.class);
		public static final EntityTemplateSetting<EulerAngle> PoseLeftLeg = create("leftLegPose", ArmorStand.class, "setLeftLegPose", EulerAngle.class);
		public static final EntityTemplateSetting<EulerAngle> PoseRightLeg = create("rightLegpose", ArmorStand.class, "setRightLegPose", EulerAngle.class);
	}
	
	public static final class Specific extends DynamicEnum<EntityTemplateSetting>
	{
		private Specific()
		{
			super(EntityTemplateSetting.class);
		}
		
		public static final EntityTemplateSetting<ItemStack> Item_ItemStack = create("item", org.bukkit.entity.Item.class, "setItemStack", ItemStack.class, "itemstack");
		public static final EntityTemplateSetting<Integer> Item_PickupDelay = create("pickupDelay", org.bukkit.entity.Item.class, "setPickupDelay", Integer.class);
		
		public static final EntityTemplateSetting<Integer> XPOrb_Experience = create("experience", org.bukkit.entity.ExperienceOrb.class, "setExperience", Integer.class, "exp", "xp");
		
		public static final EntityTemplateSetting<Boolean> FallingBlock_DropItem = create("dropItem", org.bukkit.entity.FallingBlock.class, "setDropItem", Boolean.class);
		public static final EntityTemplateSetting<Boolean> FallingBlock_HurtEntity = create("hurt", org.bukkit.entity.FallingBlock.class, "setHurtEntities", Boolean.class);
		
		public static final EntityTemplateSetting<org.bukkit.inventory.meta.FireworkMeta> Firework_FireworkMeta = create("fireworkMeta", org.bukkit.entity.Firework.class, "setFireworkMeta", org.bukkit.inventory.meta.FireworkMeta.class);
		
		public static final EntityTemplateSetting<Integer> Slime_Size = create("size", org.bukkit.entity.Slime.class, "setSize", Integer.class);
		
		public static final EntityTemplateSetting<Boolean> Creeper_Powered = create("powered", org.bukkit.entity.Creeper.class, "setPowered", Boolean.class, "charged");
		
		public static final EntityTemplateSetting<BlockData> Enderman_CarriedBlockData = create
				("carriedMaterial", org.bukkit.entity.Enderman.class, "setCarriedBlock",
						BlockData.class);
		
		public static final EntityTemplateSetting<Boolean> Zombie_Baby = create("baby", org.bukkit.entity.Zombie.class, "setBaby", Boolean.class);
		public static final EntityTemplateSetting<Boolean> Zombie_Villager = create("villager", org.bukkit.entity.ZombieVillager.class, "setVillager", Boolean.class);
		public static final EntityTemplateSetting<Profession> Zombie_Villager_Profession = create("profession", org.bukkit.entity.ZombieVillager.class, "setVillagerProfession", Profession.class);
		//Villager Stuff
		public static final EntityTemplateSetting<Profession> Villager_Profession = create("profession", org.bukkit.entity.Villager.class, "setProfession", Profession.class);

		public static final EntityTemplateSetting<Color> Horse_Color = create("color", Horse.class, "setColor", Color.class, "colour");
		public static final EntityTemplateSetting<Integer> Horse_Domestication = create("domestication", Horse.class, "setDomestication", Integer.class);
		public static final EntityTemplateSetting<Double> Horse_JumpStrength = create("jumpStrength", Horse.class, "setJumpStrength", Double.class);
		public static final EntityTemplateSetting<Integer> Horse_MaxDomestication = create("maxDomestication", Horse.class, "setMaxDomestication", Integer.class);
		public static final EntityTemplateSetting<Style> Horse_Style = create("style", Horse.class, "setStyle", Style.class);
		
		public static final EntityTemplateSetting<Boolean> Pig_Saddle = create("saddle", Pig.class, "setSaddle", Boolean.class);
		
		public static final EntityTemplateSetting<TreeSpecies> Boat_Type = create("type", Boat.class, "setWoodType", TreeSpecies.class);
		
		public static final EntityTemplateSetting<String> CommandMinecart_Command = create("command", CommandMinecart.class, "setCommand", String.class);
		public static final EntityTemplateSetting<String> CommandMinecart_Name = create("name", CommandMinecart.class, "setName", String.class);
		
		public static final EntityTemplateSetting<Boolean> HopperMinecart_Enable = create("enable", HopperMinecart.class, "setEnabled", Boolean.class);
		
		public static final EntityTemplateSetting<Art> Painting_Art = create("art", Painting.class, "setArt", Art.class);
		public static final EntityTemplateSetting<ItemStack> ItemFrame_Item = create("item", ItemFrame.class, "setItem", ItemStack.class, "itemstack");
		public static final EntityTemplateSetting<Rotation> ItemFrame_Rotation = create("rotation", ItemFrame.class, "setRotation", Rotation.class);
		

		
		public static final EntityTemplateSetting<Integer> TNTPrimed_FuseTicks = create("fuseTicks", TNTPrimed.class, "setFuseTicks", Integer.class, "fuse");
		
		public static final EntityTemplateSetting<Vector> Fireball_Direction = create("direction", Fireball.class, "setDirection", Vector.class);
		
		public static final EntityTemplateSetting<Boolean> WitherSkull_Charged = create("charged", WitherSkull.class, "setCharged", Boolean.class);
		
		public static final EntityTemplateSetting<Boolean> Bat_Awake = create("awake", Bat.class, "setAwake", Boolean.class);
		
		public static final EntityTemplateSetting<Boolean> Sitting = createWithDefault("sitting", Sittable.class, "setSitting", Boolean.class, false);
		public static final EntityTemplateSetting<Cat.Type> Cat_Type = create("type", Cat.class, "setCatType", Cat.Type.class);
		
		public static final EntityTemplateSetting<Rabbit.Type> Rabbit_Type = create("type", Rabbit.class, "setRabbitType", Rabbit.Type.class);
		
		public static final EntityTemplateSetting<Boolean> Sheep_Sheared = createWithDefault("sheared", Sheep.class, "setSheared", Boolean.class, false);
		public static final EntityTemplateSetting<DyeColor> Sheep_Color = create("color", Colorable.class, "setColor", DyeColor.class, "colour");
		
		public static final EntityTemplateSetting<Boolean> Wolf_Angry = createWithDefault("angry", Wolf.class, "setAngry", Boolean.class, false);
		public static final EntityTemplateSetting<Boolean> Wolf_Sitting = createWithDefault("sitting", Wolf.class, "setSitting", Boolean.class, false);
		public static final EntityTemplateSetting<DyeColor> Wolf_CollarColor = create("collarColor", Wolf.class, "setCollarColor", DyeColor.class, "collar", "collarColour");
		
		public static final EntityTemplateSetting<Boolean> Arrow_Critical = createWithDefault("critical", Arrow.class, "setCritical", Boolean.class, false);
		public static final EntityTemplateSetting<Integer> Arrow_Knockback = create("knockbackStrength", Arrow.class, "setKnockbackStrength", Integer.class, "knockback");
		
		public static final EntityTemplateSetting<ItemStack> ThrownPotion_Item = create("item", ThrownPotion.class, "setItem", ItemStack.class, "itemstack");
		
		public static final EntityTemplateSetting<Boolean> EnderCrystal_ShowBottom = create("showBottom", EnderCrystal.class, "setShowingBottom", Boolean.class);
		
		public static final EntityTemplateSetting<Boolean> Snowman_Derp = createWithDefault("derp", Snowman.class, "setDerp", Boolean.class, false);
		
		public static final EntityTemplateSetting<Boolean> PigZombie_Angry = create("angry", PigZombie.class, "setAngry", Boolean.class);
		public static final EntityTemplateSetting<Integer> PigZombie_Anger = create("anger", PigZombie.class, "setAnger", Integer.class);
		
		public static final EntityTemplateSetting<Phase> EnderDragon_Phase = create("phase", EnderDragon.class, "setPhase", Phase.class);
		
		private static final Specific instance = new Specific();
		public static Specific getInstance()
		{
			return instance;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Iterable<EntityTemplateSetting> values()
	{
		return Iterables.concat(All.getInstance().values(), Living.getInstance().values(), Damageable.getInstance().values(), Explosive.getInstance().values(), Projectile.getInstance().values(), Ageable.getInstance().values(), Specific.getInstance().values());
	}
}
