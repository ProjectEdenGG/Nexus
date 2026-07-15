package gg.projecteden.nexus.features.equipment.stattrack.stats.armor;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Id("distance_crouched")
@DisplayName("Distance Crouched")
public class DistanceCrouched extends StatTrackStatistic {
	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.ALL_LEGGINGS;
	}
}
