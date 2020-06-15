package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.List;

public class TrashCommand extends CustomCommand {

	public TrashCommand(CommandEvent event) {
		super(event);
	}

	@Path("[materials...]")
	void trash(@Arg(type = Material.class) List<Material> materials) {
		if (Utils.isNullOrEmpty(materials))
			player().openInventory(Bukkit.createInventory(null, 6 * 9, StringUtils.colorize("&4Trash Can!")));
		else {
			for (Material material : materials)
				player().getInventory().remove(material);
			send(PREFIX + "Trashed all matching materials");
		}
	}

}
