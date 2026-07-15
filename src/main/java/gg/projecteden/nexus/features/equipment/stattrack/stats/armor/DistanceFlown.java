package gg.projecteden.nexus.features.equipment.stattrack.stats.armor;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
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
}
