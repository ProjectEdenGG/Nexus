package gg.projecteden.nexus.features.equipment.stattrack.stats.armor;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

@Id("distance_flown")
@DisplayName("Distance Flown")
public class DistanceFlown extends StatTrackStatistic {
	@Override
	public MaterialTag getApplicableTools() {
		return null;
	}

	// Override to support possible Elytra + Chestplate combos in future
	@Override
	public boolean canTrack(ItemStack item) {
		return item.getType() == Material.ELYTRA;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition())
			return;

		if (!event.getPlayer().isGliding())
			return;

		if (!event.getTo().getWorld().equals(event.getFrom().getWorld()))
			return;

		double x1 = event.getTo().x();
		double z1 = event.getTo().z();
		double x2 = event.getFrom().x();
		double z2 = event.getFrom().x();
		double distanceSquared = Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2); // don't count y distance, only x-z
		if (distanceSquared <= 0 || distanceSquared > 32) // teleports - insane lag won't track but willing to take that
			return;
		track(event.getPlayer().getInventory().getChestplate(), Math.sqrt(distanceSquared));
	}

}
