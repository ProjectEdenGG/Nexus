package gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Environments(Env.TEST)
public class CustomBlockBreaking {

	private static final Map<Location, BrokenBlock> brokenBlocks = new ConcurrentHashMap<>();

	public static void init() {
		new BreakListener();
	}

	//

	public static void createBrokenBlock(Block block, Player player, ItemStack itemStack) {
		Location location = block.getLocation();
		if (isTracking(location)) {
			CustomBlockUtils.debug(player, "&c<- already tracking");
			return;
		}

		float blockHardness = BlockUtils.getBlockHardness(block);

		boolean isCustomBlock = false;
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock != null) {
			blockHardness = (float) customBlock.get().getBlockHardness();
			isCustomBlock = true;
		}

		if (blockHardness == -1 || blockHardness > 50) { // unbreakable
			CustomBlockUtils.debug(player, "&c<- block is unbreakable");
			return;
		}

		CustomBlockUtils.debug(player, "&e- now tracking...");
		BrokenBlock brokenBlock = new BrokenBlock(block, isCustomBlock, player, itemStack, Bukkit.getCurrentTick());
		brokenBlocks.put(location, brokenBlock);
	}

	public static void removeBrokenBlock(Location location) {
		brokenBlocks.remove(location);
	}

	public static BrokenBlock getBrokenBlock(Location location) {
		return brokenBlocks.get(location);
	}

	public static boolean isTracking(Block block) {
		return isTracking(block.getLocation());
	}

	public static boolean isTracking(Location location) {
		return brokenBlocks.containsKey(location);
	}

	//

	public static void addSlowDig(Player player, int duration) {
		if (player.hasPotionEffect(PotionEffectType.MINING_FATIGUE))
			removeSlowDig(player);

		player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration, -1, false, false, false));
	}

	public static void removeSlowDig(Player player) {
		player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
	}

	public static void sendBreakPacket(int animation, Block block) {
		DedicatedPlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();
		ServerLevel serverLevel = NMSUtils.toNMS(block.getWorld());
		BlockPos blockPos = NMSUtils.toNMS(block.getLocation());

		playerList.broadcast(null, block.getX(), block.getY(), block.getZ(), 120,
			serverLevel.dimension(), new ClientboundBlockDestructionPacket(getBlockEntityId(block), blockPos, animation));
	}

	public static void sendBreakBlock(Player player, Block block, CustomBlock customBlock) {
		Location loc = block.getLocation().toCenterLocation();
		if (customBlock != null) {
			BlockUtils.tryBreakEvent(player, block, false);
		} else {

			BlockData blockData = block.getBlockData();
			player.breakBlock(block);

			// Block breaking particles appear for everyone except for the person breaking the block, most of the time
			// 	so until a better solution if found, we'll just play particles for the player breaking the block
			player.spawnParticle(Particle.BLOCK, loc, 25, 0.25, 0.25, 0.25, 0.1, blockData);
		}
	}

	private static int getBlockEntityId(Block block) {
		return ((block.getX() & 0xFFF) << 20 | (block.getZ() & 0xFFF) << 8) | (block.getY() & 0xFF);
	}
}
