package gg.projecteden.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	public static Block getBlock(Location location) {
		return getWorld(location).getType(new BlockPosition(location.getX(), location.getY(), location.getZ())).getBlock();
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

	public static @Nullable Sound getSound(SoundType soundType, org.bukkit.block.Block block) {
		try {
			Block nmsBlock = getBlock(block.getLocation());
			SoundEffectType soundEffectType = nmsBlock.getStepSound(nmsBlock.getBlockData());
			SoundEffect nmsSound = switch (soundType) {
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

	@AllArgsConstructor
	public enum SoundType {
		BREAK(1.0, "custom.block.wood.break"),
		STEP(0.2, "custom.block.wood.step"),
		PLACE(1.0, "custom.block.wood.place"),
		HIT(0.5, "custom.block.wood.hit"),
		FALL(1.0, "custom.block.wood.fall"),
		;

		@Getter
		private final double volume;
		@Getter
		private final String customWoodSound;

		public static @Nullable SoundType fromSound(Sound sound) {
			String soundKey = sound.getKey().getKey();
			if (soundKey.endsWith(".step"))
				return SoundType.STEP;
			else if (soundKey.endsWith(".hit"))
				return SoundType.HIT;
			else if (soundKey.endsWith(".place"))
				return SoundType.PLACE;
			else if (soundKey.endsWith(".break"))
				return SoundType.BREAK;
			else if (soundKey.endsWith(".fall"))
				return SoundType.FALL;

			return null;
		}

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

	public static float getBlockDamage(Player player, org.bukkit.block.Block block, org.bukkit.inventory.ItemStack itemStack) {
		float blockHardness = getBlockHardness(block);
		if (blockHardness == -1)
			return -1;

		float speedMultiplier = 1;

		if (!Nullables.isNullOrAir(itemStack)) {
			speedMultiplier = getDestroySpeed(block, itemStack);

			if (itemStack.getItemMeta().hasEnchants()) {
				Map<Enchantment, Integer> enchants = itemStack.getItemMeta().getEnchants();
				if (enchants.containsKey(Enchant.EFFICIENCY)) {
					speedMultiplier += 1 + Math.pow(enchants.get(Enchant.EFFICIENCY), 2);
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

			if (hasteLevel > 0) {
				speedMultiplier *= (0.2 * hasteLevel) + 1;
			}

			if (fatigueLevel > 0) {
				speedMultiplier *= Math.pow(0.3, fatigueLevel);
			}
		}


		org.bukkit.inventory.ItemStack helmet = player.getInventory().getHelmet();
		if (player.isInWater() && !Nullables.isNullOrAir(helmet) && helmet.getItemMeta().hasEnchants()) {
			int aquaAffLevel = 0;

			@NotNull Map<Enchantment, Integer> enchants = helmet.getItemMeta().getEnchants();
			if (enchants.containsKey(Enchant.AQUA_AFFINITY)) {
				aquaAffLevel = enchants.get(Enchant.AQUA_AFFINITY);
			}

			if (aquaAffLevel > 0)
				speedMultiplier /= 5;
		}

		if (!player.isOnGround())
			speedMultiplier /= 5;

		float damage = speedMultiplier / blockHardness;
		List<org.bukkit.inventory.ItemStack> drops = block.getDrops(itemStack, player).stream()
			.filter(_itemStack -> !Nullables.isNullOrAir(_itemStack)).toList();
		if (drops.size() > 0)
			damage /= 30;
		else
			damage /= 100;

		if (damage > 1)
			return 0;

		return damage;
	}
}
