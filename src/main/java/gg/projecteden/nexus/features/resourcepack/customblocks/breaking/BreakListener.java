package gg.projecteden.nexus.features.resourcepack.customblocks.breaking;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.CustomBlockUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.utils.Debug.DebugType;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BreakListener implements Listener {

	@Getter
	private static final HashMap<UUID, Integer> breakWait = new HashMap<>();

	public BreakListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(BlockDamageEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "CustomBlockBreaking: BlockDamageEvent");

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock == null)
			return;

		// 6 tick delay after breaking a block, before able to damage another
		int currentTick = Bukkit.getCurrentTick();
		if (breakWait.containsKey(player.getUniqueId())) {
			if (currentTick < (6 + breakWait.get(player.getUniqueId()))) {
				CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "<-- on cooldown");
				return;
			}
		}

		if (Breaker.isTracking(block.getLocation())) {
			CustomBlockUtils.debug(player, DebugType.CUSTOM_BLOCK_DAMAGE, "<-- already tracking");
			return;
		}

		ItemStack itemInHand = event.getItemInHand();
		Breaker.startTracking(block, player, itemInHand);
	}

	@EventHandler
	public void on(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = player.getTargetBlockExact(5);
		if (block == null)
			return;

		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock == null)
			return;

		Location blockLoc = block.getLocation();
		if (player.getLocation().distanceSquared(blockLoc) >= 1024.0D)
			return;

		DamagedBlock damagedBlock = Breaker.get(blockLoc);
		if (damagedBlock == null)
			return;

		Breaker.addSlowDig(player, 10);
		damagedBlock.incrementDamage(player, player.getInventory().getItemInMainHand());
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock == null)
			return;

		CustomBlockUtils.debug(event.getPlayer(), DebugType.CUSTOM_BLOCK_DAMAGE, "CustomBlockBreaking: BlockBreakEvent");

		DamagedBlock damagedBlock = Breaker.get(block.getLocation());
		if (damagedBlock == null)
			return;

		damagedBlock.remove();
	}

	@EventHandler
	public void on(BlockDamageAbortEvent event) {
		Block block = event.getBlock();
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock == null)
			return;

		DamagedBlock damagedBlock = Breaker.get(block.getLocation());
		if (damagedBlock == null)
			return;

		damagedBlock.remove();
	}
}
