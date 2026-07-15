package gg.projecteden.nexus.features.equipment.stattrack.stats.generic;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

@Id("blocks_broken")
@DisplayName("Blocks Broken")
public class BlocksBroken extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.PICKAXES.append(MaterialTag.SHOVELS).append(MaterialTag.AXES).append(MaterialTag.HOES);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		track(ItemUtils.getTool(event.getPlayer()));
	}

}
