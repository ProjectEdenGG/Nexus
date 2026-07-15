package gg.projecteden.nexus.features.equipment.stattrack.stats.pickaxe;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

@Id("stone_mined")
@DisplayName("Stone Mined")
public class StoneMined extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.PICKAXES;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (!MaterialTag.STONE_ORE_REPLACEABLES.isTagged(event.getBlock().getType()) && !MaterialTag.DEEPSLATE_ORE_REPLACEABLES.isTagged(event.getBlock().getType()))
			return;
		track(ItemUtils.getTool(event.getPlayer()));
	}

}
