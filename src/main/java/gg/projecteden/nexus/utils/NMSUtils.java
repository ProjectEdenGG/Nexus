package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.NonNull;
import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundEffectType;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NMSUtils {

	public static MinecraftServer getServer() {
		return ((CraftServer) Bukkit.getServer()).getServer();
	}

	public static WorldServer getWorldServer(Location bukkitLocation) {
		return getWorldServer(bukkitLocation.getWorld());
	}

	public static WorldServer getWorldServer(org.bukkit.World bukkitWorld) {
		return ((CraftWorld) bukkitWorld).getHandle();
	}

	public static World getWorld(Location location) {
		return ((CraftWorld) location.getWorld()).getHandle();
	}

	public static BlockPosition getBlockPosition(Location location) {
		return new BlockPosition(location.getX(), location.getY(), location.getZ());
	}

	public static Block getBlock(Location location) {
		return getWorld(location).getType(getBlockPosition(location)).getBlock();
	}

	public static IBlockData getBlockData(BlockData blockData) {
		return ((CraftBlockData) blockData).getState();
	}

	public static boolean setBlockDataAt(BlockData blockData, Location location, boolean doPhysics) {
		World world = getWorld(location);
		BlockPosition blockPosition = getBlockPosition(location);
		IBlockData iBlockData = getBlockData(blockData);

		return world.setTypeAndData(blockPosition, iBlockData, doPhysics ? 3 : 2);
	}

	public static void applyPhysics(org.bukkit.block.Block block) {
		applyPhysics(block.getLocation());
	}

	public static void applyPhysics(Location location) {
		World world = getWorld(location);
		BlockPosition position = getBlockPosition(location);
		Block nmsBlock = getBlock(location);

		world.applyPhysics(position, nmsBlock);
	}

	public static EntityArmorStand createHologram(World world) {
		EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.c, world);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setBasePlate(true);
		armorStand.setSmall(true);
		armorStand.setCustomNameVisible(true);

		return armorStand;
	}

	public static @Nullable Sound getSound(SoundAction soundAction, org.bukkit.block.Block block) {
		try {
			Block nmsBlock = getBlock(block.getLocation());
			SoundEffectType soundEffectType = nmsBlock.getStepSound(nmsBlock.getBlockData());
			SoundEffect nmsSound = switch (soundAction) {
				case BREAK -> soundEffectType.c();
				case STEP -> soundEffectType.getStepSound();
				case PLACE -> soundEffectType.getPlaceSound();
				case HIT -> soundEffectType.f();
				case FALL -> soundEffectType.getFallSound();
			};

			MinecraftKey nmsString = nmsSound.a();
			String soundString = nmsString.getKey().replace(".", "_").toUpperCase();
			return Sound.valueOf(soundString);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static float getBlockHardness(org.bukkit.block.Block block) {
		try {
			BlockBase blockBase = getBlock(block.getLocation());
			return blockBase.t();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return -1;
	}

	public static float getBlockDurability(org.bukkit.block.Block block) {
		try {
			Block nmsBlock = getBlock(block.getLocation());
			return nmsBlock.getDurability();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return -1;
	}

	public static float getDestroySpeed(org.bukkit.block.Block block, org.bukkit.inventory.ItemStack itemStack) {
		try {
			ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
			Block nmsBlock = getBlock(block.getLocation());
			Item item = nmsItemStack.getItem();
			return item.getDestroySpeed(nmsItemStack, nmsBlock.getBlockData());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 1;
	}

	private enum ToolType {
		NONE,
		WOODEN,
		STONE,
		IRON,
		DIAMOND,
		NETHERITE,
		GOLDEN,
		SHEARS,
		SWORD,
		;

		public static @NonNull ToolType of(@Nullable org.bukkit.inventory.ItemStack itemStack) {
			if (Nullables.isNullOrAir(itemStack))
				return NONE;

			String material = itemStack.getType().name().toLowerCase();
			if (material.contains("sword"))
				return SWORD;
			if (material.equals("shears"))
				return SHEARS;

			for (ToolType toolType : values()) {
				if (material.startsWith(toolType.name().toLowerCase()))
					return toolType;
			}

			return NONE;
		}

		public List<org.bukkit.inventory.ItemStack> getTools() {
			if (this == NONE || this == SHEARS)
				return new ArrayList<>();

			Material shovel = Material.valueOf(this.name() + "_SHOVEL");
			Material pickaxe = Material.valueOf(this.name() + "_PICKAXE");
			Material axe = Material.valueOf(this.name() + "_AXE");
			Material hoe = Material.valueOf(this.name() + "_HOE");
			Material sword = Material.valueOf(this.name() + "_SWORD");
			Material shears = Material.SHEARS;

			return List.of(
				new org.bukkit.inventory.ItemStack(shovel),
				new org.bukkit.inventory.ItemStack(pickaxe),
				new org.bukkit.inventory.ItemStack(axe),
				new org.bukkit.inventory.ItemStack(hoe),
				new org.bukkit.inventory.ItemStack(sword),
				new org.bukkit.inventory.ItemStack(shears)
			);
		}
	}

	public static boolean canHarvest(Player player, org.bukkit.block.Block block, org.bukkit.inventory.ItemStack itemStack) {
		return block.getDrops(itemStack, player).stream()
			.filter(Nullables::isNotNullOrAir)
			.toList()
			.size() > 0;
	}

	public static float getBlockDamage(Player player, org.bukkit.block.Block block, org.bukkit.inventory.ItemStack itemStack) {
		float blockHardness = getBlockHardness(block);
		if (blockHardness == -1)
			return -1;

		boolean canHarvest = canHarvest(player, block, itemStack);

		float speedMultiplier = getDestroySpeed(block, itemStack);

		Dev.WAKKA.send("init speed multiplier = " + speedMultiplier);

		// if (isBestTool): speedMultiplier = toolMultiplier
		if (block.isPreferredTool(itemStack)) {
			Dev.WAKKA.send("is best tool, speed multiplier = " + speedMultiplier);

			// if (not canHarvest): speedMultiplier = 1
			if (!canHarvest) {
				speedMultiplier = 1;
				Dev.WAKKA.send("can't harvest, speed multiplier = " + speedMultiplier);
			}

			// else if (toolEfficiency): speedMultiplier += efficiencyLevel ^ 2 + 1
			else if (!Nullables.isNullOrAir(itemStack)) {
				if (itemStack.getItemMeta().hasEnchants()) {
					Map<Enchantment, Integer> enchants = itemStack.getItemMeta().getEnchants();
					if (enchants.containsKey(Enchant.EFFICIENCY)) {
						speedMultiplier += Math.pow(enchants.get(Enchant.EFFICIENCY), 2) + 1;
						Dev.WAKKA.send("efficiency speed multiplier = " + speedMultiplier);
					}
				}
			}
		}

		if (!player.getActivePotionEffects().isEmpty()) {
			int hasteLevel = 0;
			int fatigueLevel = 0;
			for (PotionEffect potionEffect : player.getActivePotionEffects()) {
				int amplifier = potionEffect.getAmplifier();
				if (potionEffect.getType().equals(PotionEffectType.FAST_DIGGING)) {
					if (amplifier > hasteLevel)
						hasteLevel = amplifier;
				} else if (potionEffect.getType().equals(PotionEffectType.SLOW_DIGGING)) {
					if (amplifier > fatigueLevel)
						fatigueLevel = amplifier;
				}
			}

			// if (hasteEffect): speedMultiplier *= 0.2 * hasteLevel + 1
			if (hasteLevel > 0) {
				speedMultiplier *= (0.2 * hasteLevel) + 1;
				Dev.WAKKA.send("Player has haste, speed multiplier = " + speedMultiplier);
			}

			// if (miningFatigue): speedMultiplier *= 0.3 ^ min(miningFatigueLevel, 4)
			if (fatigueLevel > 0) {
				speedMultiplier *= Math.pow(0.3, Math.min(fatigueLevel, 4));
				Dev.WAKKA.send("Player has fatigue, speed multiplier = " + speedMultiplier);
			}
		}

		org.bukkit.inventory.ItemStack helmet = player.getInventory().getHelmet();
		if (!Nullables.isNullOrAir(helmet) && helmet.getItemMeta().hasEnchants()) {
			boolean hasAquaAffinity = false;

			@NotNull Map<Enchantment, Integer> enchants = helmet.getItemMeta().getEnchants();
			if (enchants.containsKey(Enchant.AQUA_AFFINITY))
				hasAquaAffinity = true;

			// if (inWater and not hasAquaAffinity): speedMultiplier /= 5
			if (player.isInWater() && !hasAquaAffinity) {
				speedMultiplier /= 5;
				Dev.WAKKA.send("Player is in water with no AquaAffinity, speed multiplier = " + speedMultiplier);
			}
		}

		// if (not onGround): speedMultiplier /= 5
		if (!player.isOnGround()) {
			speedMultiplier /= 5;
			Dev.WAKKA.send("Player is not on ground, speed multiplier = " + speedMultiplier);
		}

		Dev.WAKKA.send("final speed multiplier = " + speedMultiplier);
		// damage = speedMultiplier / blockHardness
		float damage = speedMultiplier / blockHardness;

		Dev.WAKKA.send("init damage = " + damage);

		// if (canHarvest): damage /= 30
		if (canHarvest) {
			damage /= 30;
			Dev.WAKKA.send("can harvest, damage = " + damage);
		}
		// else: damage /= 100
		else {
			damage /= 100;
			Dev.WAKKA.send("can't harvest, damage = " + damage);
		}

		// Instant Breaking:
		// if (damage > 1): return 0
		if (damage > 1) {
			Dev.WAKKA.send("instant breaking, damage = 0");
			return 0;
		}

		return damage;
	}
}
