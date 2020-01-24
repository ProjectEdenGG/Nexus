package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;

public class TrashCommand extends CustomCommand {

	public TrashCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void trash() {
		player().openInventory(Bukkit.createInventory(null, 6 * 9, Utils.colorize("&4Trash Can!")));
	}

}
