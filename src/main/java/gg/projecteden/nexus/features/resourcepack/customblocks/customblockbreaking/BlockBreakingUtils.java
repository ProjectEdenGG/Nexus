package gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.NMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlockBreakingUtils {
	public static void addSlowDig(Player player, int duration) {
		if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
			removeSlowDig(player);

		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, duration, -1, false, false, false));
	}

	public static void removeSlowDig(Player player) {
		player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
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
			player.spawnParticle(Particle.BLOCK_CRACK, loc, 25, 0.25, 0.25, 0.25, 0.1, blockData);
		}
	}

	private static int getBlockEntityId(Block block) {
		return ((block.getX() & 0xFFF) << 20 | (block.getZ() & 0xFFF) << 8) | (block.getY() & 0xFF);
	}
}
