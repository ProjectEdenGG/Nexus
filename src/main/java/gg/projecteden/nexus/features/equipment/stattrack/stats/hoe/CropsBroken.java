package gg.projecteden.nexus.features.equipment.stattrack.stats.hoe;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

@Id("crops_broken")
@DisplayName("Crops Broken")
public class CropsBroken extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.HOES;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (!MaterialTag.CROPS.isTagged(event.getBlock().getType()))
			return;
		track(ItemUtils.getTool(event.getPlayer()));
	}

}
