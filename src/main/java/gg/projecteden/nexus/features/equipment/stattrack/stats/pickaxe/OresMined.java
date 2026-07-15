package gg.projecteden.nexus.features.equipment.stattrack.stats.pickaxe;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

@Id("ores_mined")
@DisplayName("Ores Mined")
public class OresMined extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.PICKAXES;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (!MaterialTag.MINERAL_ORES.append(MaterialTag.MINERAL_RAW_BLOCKS).isTagged(event.getBlock().getType()))
			return;
		track(ItemUtils.getTool(event.getPlayer()));
	}

}
