package gg.projecteden.nexus.features.store.perks.workbenches;

import gg.projecteden.nexus.features.custombenches.CustomBench.CustomBenchType;
import gg.projecteden.nexus.features.custombenches.DyeStation;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public abstract class _WorkbenchCommand extends CustomCommand {
	public static final String PERMISSION = "workbenches";

	public _WorkbenchCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			if (worldGroup() == WorldGroup.EVENTS)
				permissionError();
	}

	@Path
	void run() {
		getType().open(player());
	}

	protected abstract Workbench getType();

	@Getter
	@AllArgsConstructor
	public enum Workbench {
		CRAFTING_TABLE(Material.CRAFTING_TABLE, player -> player.openWorkbench(null, true)),
		STONE_CUTTER(Material.STONECUTTER, player -> player.openStonecutter(null, true)),
		SMITHING_TABLE(Material.SMITHING_TABLE, player -> player.openSmithingTable(null, true)),
		GRINDSTONE(Material.GRINDSTONE, player -> player.openGrindstone(null, true)),
		LOOM(Material.LOOM, player -> player.openLoom(null, true)),
		CARTOGRAPHY_TABLE(Material.CARTOGRAPHY_TABLE, player -> player.openCartographyTable(null, true)),
		DYE_STATION(CustomBenchType.DYE_STATION, DyeStation::open);

		private final Material material;
		private final int customModelData;
		private final Consumer<Player> open;

		Workbench(Material material, Consumer<Player> open) {
			this.material = material;
			this.customModelData = 0;
			this.open = open;
		}

		Workbench(CustomBenchType customBenchType, Consumer<Player> open) {
			this.material = customBenchType.getMaterial();
			this.customModelData = customBenchType.getModelData();
			this.open = open;
		}

		public void open(Player player) {
			open.accept(player);
		}
	}

}
