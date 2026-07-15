package gg.projecteden.nexus.features.equipment.stattrack.stats.hoe;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrack;
import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Id("blocks_tilled")
@DisplayName("Blocks Tilled")
public class BlocksTilled extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.HOES;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCreatePath(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Block block = event.getClickedBlock();
		ItemStack tool = event.getItem();

		if (block == null || tool == null)
			return;

		if (!MaterialTag.HOES.isTagged(tool.getType()))
			return;

		Material originalType = block.getType();
		Material expectedType = getHoeResult(originalType);

		if (expectedType == null)
			return;

		UUID statTrackId = StatTrack.getStatTrackId(tool);
		if (statTrackId == null)
			return;

		Tasks.wait(1, () -> {
			if (block.getType() != expectedType) return;
			track(statTrackId, 1);
		});
	}

	private Material getHoeResult(Material originalType) {
		return switch (originalType) {
			case GRASS_BLOCK, DIRT, DIRT_PATH -> Material.FARMLAND;
			case COARSE_DIRT, ROOTED_DIRT -> Material.DIRT;
			default -> null;
		};
	}

}
