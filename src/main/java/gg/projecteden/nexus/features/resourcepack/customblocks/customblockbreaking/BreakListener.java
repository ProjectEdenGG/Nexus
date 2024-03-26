package gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BreakListener implements Listener {

	@Getter
	private static HashMap<UUID, Integer> breakWait = new HashMap<>();

	private static final Set<Material> blackListed = new HashSet<>();

	public BreakListener() {
		Nexus.registerListener(this);

		blackListed.addAll(Set.of(Material.BARRIER, Material.LIGHT));
		blackListed.addAll(MaterialTag.LIQUIDS.getValues());
		blackListed.addAll(MaterialTag.ALL_AIR.getValues());
	}

	private boolean isValid(Player player) {
		return !GameModeWrapper.of(player).isCreative();
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		if (!CustomBlockBreaking.getManager().isTracking(event.getBlock()))
			return;

		CustomBlockBreaking.getManager().removeBrokenBlock(event.getBlock());
	}

	@EventHandler
	public void on(BlockDamageEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (!isValid(player))
			return;

		// 6 tick delay after breaking a block, before able to damage another
		if (Bukkit.getCurrentTick() < (breakWait.getOrDefault(player.getUniqueId(), Bukkit.getCurrentTick()) + 6)) {
			return;
		}

		Block block = event.getBlock();
		if (CustomBlockBreaking.getManager().isTracking(block))
			return;

		ItemStack itemInHand = event.getItemInHand();
		CustomBlockBreaking.getManager().createBrokenBlock(block, player, itemInHand);
	}

	@EventHandler
	public void on(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (!isValid(player))
			return;

		Block block = player.getTargetBlockExact(5);
		if (block == null || blackListed.contains(block.getType()))
			return;

		Location blockLoc = block.getLocation();
		if (player.getLocation().distanceSquared(blockLoc) >= 1024.0D)
			return;

		if (!CustomBlockBreaking.getManager().isTracking(blockLoc))
			return;

		BrokenBlock brokenBlock = CustomBlockBreaking.getManager().getBrokenBlock(blockLoc);
		if (brokenBlock == null)
			return;

		BlockBreakingUtils.addSlowDig(player, 10);
		brokenBlock.incrementDamage(player, player.getInventory().getItemInMainHand());
	}
}
