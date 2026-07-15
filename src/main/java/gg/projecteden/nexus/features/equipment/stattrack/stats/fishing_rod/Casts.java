package gg.projecteden.nexus.features.equipment.stattrack.stats.fishing_rod;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

@Id("casts")
@DisplayName("Casts")
public class Casts extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return new MaterialTag(Material.FISHING_ROD);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRodCast(PlayerFishEvent event) {
		if (event.getState() != PlayerFishEvent.State.FISHING)
			return;

		Player player = event.getPlayer();
		ItemStack rod = player.getInventory().getItem(event.getHand());

		track(rod);
	}

}
