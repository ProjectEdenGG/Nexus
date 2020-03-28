package me.pugabyte.bncore.features.warps.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;

public class SkyBlockCommand extends CustomCommand {

	public SkyBlockCommand(CommandEvent event) {
		super(event);
	}

	WarpService service = new WarpService();

	@Path
	void run() {
		Warp warp = service.get("skyblock", WarpType.NORMAL);
		if (warp == null) error("There was an error while executing this command");
		warp.teleport(player());
	}

}
