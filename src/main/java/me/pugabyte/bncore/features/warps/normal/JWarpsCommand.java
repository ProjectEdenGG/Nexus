package me.pugabyte.bncore.features.warps.normal;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class JWarpsCommand extends CustomCommand {

	public JWarpsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void menu() {
		WarpsMenu.open(player(), WarpMenu.MAIN);
	}
}
