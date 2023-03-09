package gg.projecteden.nexus.features.survival.structures;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.features.survival.structures.Structures.SpecialBlockType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@HideFromWiki // TODO
@Permission(Group.ADMIN)
public class StructuresCommand extends CustomCommand {

	public StructuresCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("cut")
	void cut() {
		Region region = worldedit().getPlayerSelection(player());

		player().teleport(worldedit().toLocation(region.getMinimumPoint()).clone().add(-1, 0, 0).toCenterLocation());

		cmd("/cut");
	}

	@Path("corners")
	void corners() {
		Region region = worldedit().getPlayerSelection(player());


		Location min = worldedit().toLocation(region.getMinimumPoint());
		Location max = worldedit().toLocation(region.getMaximumPoint());
		int xDiff = max.getBlockX() - min.getBlockX();
		int zDiff = max.getBlockZ() - min.getBlockZ();

		List<Location> corners = new ArrayList<>(List.of(min.clone(), max.clone()));

		corners.add(min.clone().add(xDiff, 0, 0));
		corners.add(min.clone().add(0, 0, zDiff));
		corners.add(min.clone().add(xDiff, 0, zDiff));

		corners.add(max.clone().subtract(xDiff, 0, 0));
		corners.add(max.clone().subtract(0, 0, zDiff));
		corners.add(max.clone().subtract(xDiff, 0, zDiff));

		for (Location corner : corners) {
			if (MaterialTag.ALL_AIR.isTagged(corner.getBlock()))
				corner.getBlock().setType(Material.TINTED_GLASS);
		}

		Block stand = min.clone().add(-1, -1, 0).getBlock();
		if (MaterialTag.ALL_AIR.isTagged(stand))
			stand.setType(Material.BLACK_CONCRETE);
	}

	@Path("schem <name>")
	void save(String name) {
		if (!Structures.isInBuildRegion(player()))
			error("This command can only be used in the build region!");

		// ensure player has selection
		worldedit().getPlayerSelection(player());

		cmd("blockcenter");

		// replace special blocks
		int undoCount = 0;
		for (SpecialBlockType type : SpecialBlockType.values()) {
			undoCount++;

			String from = type.getFromType().name().toLowerCase();
			String to = type.getToType().name().toLowerCase();

			cmd("/replace " + from + " " + to);
		}

		// copy selection
		cmd("/copy");

		// make schematic
		cmd("/schem save betterstructures/custom/betterstructures_" + name);

		// undo
		cmd("/undo " + undoCount);
	}

	private void cmd(String noSlash) {
		PlayerUtils.runCommandAsOp(player(), noSlash);
	}

}
