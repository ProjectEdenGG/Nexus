package gg.projecteden.nexus.utils;

import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundEffectType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.jetbrains.annotations.Nullable;

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

	public enum SoundType {
		BREAK,
		STEP,
		PLACE,
		HIT,
		FALL,
	}
}
