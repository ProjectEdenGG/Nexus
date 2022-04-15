package gg.projecteden.nexus.features.customblocks.customblockbreaking;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class BreakListener implements Listener {

	private static final Set<Material> blackListed = new HashSet<>();

	public BreakListener() {
		Nexus.registerListener(this);

		blackListed.addAll(Set.of(Material.BARRIER, Material.LIGHT));
		blackListed.addAll(MaterialTag.LIQUIDS.getValues());
		blackListed.addAll(MaterialTag.ALL_AIR.getValues());
	}

	private boolean isValid(Player player) {
		if (GameModeWrapper.of(player).isCreative())
			return false;

		return true;
	}

	@EventHandler
	public void on(BlockDamageEvent event) {
		if (event.isCancelled())
			return;

		if (!isValid(event.getPlayer()))
			return;

		CustomBlockBreaking.getManager().createBrokenBlock(event.getBlock(), 30);
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
		if (!CustomBlockBreaking.getManager().isTracking(blockLoc)) {
			Dev.WAKKA.send("already being tracked");
			return;
		}

		ItemStack itemStack = player.getInventory().getItemInMainHand();

		Location playerLoc = player.getLocation();
		double distanceX = blockLoc.getX() - playerLoc.getX();
		double distanceY = blockLoc.getY() - playerLoc.getY();
		double distanceZ = blockLoc.getZ() - playerLoc.getZ();

		if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ >= 1024.0D)
			return;

		BlockBreakingUtils.addSlowDig(event.getPlayer(), 200);
		CustomBlockBreaking.getManager().getBrokenBlock(blockLoc).incrementDamage(player, 1);
	}
}
