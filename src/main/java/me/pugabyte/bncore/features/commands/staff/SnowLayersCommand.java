package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;

@Permission("group.staff")
public class SnowLayersCommand extends CustomCommand {

	public SnowLayersCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<layers>")
	void layers(int layers) {
		Block block = player().getLocation().getBlock();
		block.setType(Material.SNOW, false);
		Snow snow = (Snow) block.getBlockData();
		snow.setLayers(layers);
		block.setBlockData(snow);
	}
}
