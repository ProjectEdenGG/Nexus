package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.customblocks.CustomBlocks.SoundAction;
import gg.projecteden.parchment.HasLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.jetbrains.annotations.Nullable;

public class NMSUtils {

	public static MinecraftServer getServer() {
		return ((CraftServer) Bukkit.getServer()).getServer();
	}

	public static ServerLevel getWorldServer(Location bukkitLocation) {
		return getWorldServer(bukkitLocation.getWorld());
	}

	public static ServerLevel getWorldServer(org.bukkit.World bukkitWorld) {
		return ((CraftWorld) bukkitWorld).getHandle();
	}

	@Deprecated(forRemoval = true) // duplicate of above methods
	public static ServerLevel getWorld(Location location) {
		return ((CraftWorld) location.getWorld()).getHandle();
	}

	public static BlockPos getBlockPosition(Location location) {
		return new BlockPos(location.getX(), location.getY(), location.getZ());
	}

	public static BlockPos getBlockPosition(HasLocation location) {
		return getBlockPosition(location.getLocation());
	}

	public static Block getBlock(Location location) {
		return getWorld(location).getBlockState(getBlockPosition(location)).getBlock();
	}

	public static Block getBlock(HasLocation location) {
		return getBlock(location.getLocation());
	}

	public static Block getBlock(org.bukkit.block.Block block) {
		return ((CraftBlock) block).getNMS().getBlock();
	}

	public static BlockState getBlockData(BlockData blockData) {
		return ((CraftBlockData) blockData).getState();
	}

	public static BlockState getBlockData(Location location) {
		return getWorld(location).getBlockState(getBlockPosition(location));
	}

	public static BlockState getBlockData(org.bukkit.block.Block block) {
		return ((CraftBlock) block).getNMS();
	}

	public static boolean setBlockDataAt(BlockData blockData, Location location, boolean doPhysics) {
		ServerLevel world = getWorld(location);
		BlockPos blockPosition = getBlockPosition(location);
		BlockState iBlockData = getBlockData(blockData);

		return world.setBlock(blockPosition, iBlockData, doPhysics ? 3 : 2);
	}

	public static void applyPhysics(org.bukkit.block.Block block) {
		applyPhysics(block.getLocation());
	}

	public static void applyPhysics(Location location) {
		ServerLevel world = getWorld(location);
		BlockPos position = getBlockPosition(location);
		Block nmsBlock = getBlock(location);

		world.updateNeighborsAt(position, nmsBlock);
	}

	public static ArmorStand createHologram(Level world) {
		ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, world);
		armorStand.setMarker(true);
		armorStand.setInvisible(true);
		armorStand.setNoBasePlate(true);
		armorStand.setSmall(true);
		armorStand.setCustomNameVisible(true);

		return armorStand;
	}

	public static @Nullable Sound getSound(SoundAction soundAction, org.bukkit.block.Block block) {
		try {
			Block nmsBlock = getBlock(block.getLocation());
			SoundType soundEffectType = nmsBlock.getSoundType(getBlockData(block));
			SoundEvent nmsSound = switch (soundAction) {
				case BREAK -> soundEffectType.getBreakSound();
				case STEP -> soundEffectType.getStepSound();
				case PLACE -> soundEffectType.getPlaceSound();
				case HIT -> soundEffectType.getHitSound();
				case FALL -> soundEffectType.getFallSound();
			};

			ResourceLocation nmsString = nmsSound.getLocation();
			String soundString = nmsString.getPath().replace(".", "_").toUpperCase();
			return Sound.valueOf(soundString);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static float getDestroySpeed(org.bukkit.block.Block block, org.bukkit.inventory.ItemStack itemStack) {
		try {
			ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
			Item item = nmsItemStack.getItem();
			return item.getDestroySpeed(nmsItemStack, getBlockData(block));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 1;
	}

}
