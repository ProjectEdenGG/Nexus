package me.pugabyte.bncore.features.holidays.aeveonproject.commands;

import com.sk89q.worldedit.regions.Region;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.holidays.aeveonproject.Regions;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WEUtils;
import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WGUtils;

@Aliases("ap")
@Permission("group.staff")
@NoArgsConstructor
public class AeveonProjectCommand extends CustomCommand implements Listener {

	public AeveonProjectCommand(CommandEvent event) {
		super(event);
	}

	@Path("dockingports")
	public void dockingPorts() {
		Material air = Material.AIR;

		for (int i = 1; i <= Regions.sialia_dockingports_count; i++) {
			Region region = WGUtils.getRegion(Regions.sialia_dockingports.replaceAll("#", String.valueOf(i)));
			List<Block> blocks = WEUtils.getBlocks(region);

			for (Block block : blocks) {
				if (block.getType().equals(Material.WATER))
					player().sendBlockChange(block.getLocation(), air.createBlockData());
			}
		}

	}

}
