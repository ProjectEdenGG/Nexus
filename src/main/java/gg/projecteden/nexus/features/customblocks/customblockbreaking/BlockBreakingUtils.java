package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.utils.NMSUtils;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
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
		((CraftServer) Bukkit.getServer()).getHandle().broadcast(null, block.getX(), block.getY(), block.getZ(), 120,
			((CraftWorld) block.getWorld()).getHandle().dimension(), new ClientboundBlockDestructionPacket(getBlockEntityId(block), NMSUtils.getBlockPosition(block), animation));
	}

	public static void sendBreakBlock(Player player, Block block) {
		ServerPlayerGameMode interactManager = ((CraftPlayer) player).getHandle().gameMode;
		interactManager.destroyBlock(NMSUtils.getBlockPosition(block));
	}

	private static int getBlockEntityId(Block block) {
		return ((block.getX() & 0xFFF) << 20 | (block.getZ() & 0xFFF) << 8) | (block.getY() & 0xFF);
	}
}
