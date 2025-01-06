package gg.projecteden.nexus.features.listeners;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Disabled
public class SafeTeleport implements Listener {

	private static final Set<Material> safeBlocks = MaterialTag.CARPETS.getValues();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event) {
		if (Minigamer.of(event.getPlayer()).isPlaying()) return;
		if (CitizensUtils.isNPC(event.getPlayer())) return;
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND && event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN)
			return;
		if (event.getPlayer().isFlying()) return;
		if (isSafe(event.getTo())) return;
		Location newLoc = getSafeLocation(event.getTo());
		if (newLoc == null) return;
		event.setTo(LocationUtils.getCenteredLocation(newLoc));
	}

	public boolean isSafe(Location location) {
		if (location.getBlock().getType() != Material.AIR) return false;
		if (location.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) return false;
		Material material = location.clone().subtract(0, 1, 0).getBlock().getType();
		return (safeBlocks.contains(material) || material.isSolid());
	}

	public Location getSafeLocation(Location location) {
		List<Block> blocks = BlockUtils.getBlocksInRadius(location, 5)
			.stream().filter(block -> block.getLocation().getBlockY() == location.getBlockY())
			.sorted(Comparator.comparing(block -> Distance.distance(block, location).get()))
			.toList();

		for (Block block : blocks) {
			Location loc = block.getLocation();
			for (int i = loc.getBlockY(); i > 0; i--) {
				Location newLoc = loc.clone();
				newLoc.setY(i);
				if (isSafe(newLoc)) return newLoc;
			}
			for (int i = loc.getBlockY(); i < 255; i++) {
				Location newLoc = loc.clone();
				newLoc.setY(i);
				if (isSafe(newLoc)) return newLoc;
			}
		}
		return null;
	}

}
