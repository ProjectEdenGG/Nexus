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
import org.bukkit.World;
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

	public static ServerLevel toNMS(org.bukkit.World bukkitWorld) {
		return ((CraftWorld) bukkitWorld).getHandle();
	}

	public static BlockPos toNMS(HasLocation hasLocation) {
		final Location location = hasLocation.getLocation();
		return new BlockPos(location.getX(), location.getY(), location.getZ());
	}

	public static Location fromNMS(World world, BlockPos pos) {
		return new Location(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static Block toNMS(org.bukkit.block.Block block) {
		return ((CraftBlock) block).getNMS().getBlock();
	}

	public static BlockState toNMS(BlockData blockData) {
		return ((CraftBlockData) blockData).getState();
	}

	public static boolean setBlockDataAt(BlockData blockData, Location location, boolean doPhysics) {
		ServerLevel world = toNMS(location.getWorld());
		BlockPos blockPosition = toNMS(location);
		BlockState iBlockData = toNMS(blockData);

		return world.setBlock(blockPosition, iBlockData, doPhysics ? 3 : 2);
	}

	public static void applyPhysics(org.bukkit.block.Block block) {
		applyPhysics(block.getLocation());
	}

	public static void applyPhysics(Location location) {
		ServerLevel world = toNMS(location.getWorld());
		BlockPos position = toNMS(location);
		Block nmsBlock = toNMS(location.getBlock());

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
			Block nmsBlock = toNMS(block);
			SoundType soundEffectType = nmsBlock.getSoundType(toNMS(block.getBlockData()));
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
			return item.getDestroySpeed(nmsItemStack, NMSUtils.toNMS(block.getBlockData()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 1;
	}

}
