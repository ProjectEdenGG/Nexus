package me.pugabyte.bncore.features.warps.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.WorldGroup;

import java.util.Arrays;

public class MarketCommand extends CustomCommand {

	public MarketCommand(CommandEvent event) {
		super(event);
	}

	WarpService service = new WarpService();

	@Path
	void run() {
		Warp warp;
		if (Arrays.asList(WorldGroup.SKYBLOCK.getWorlds()).contains(player().getWorld().getName()))
			warp = service.get("skyblock-market", WarpType.NORMAL);
		else
			warp = service.get("market", WarpType.NORMAL);
		if (warp == null) error("There was an error while executing this command");
		warp.teleport(player());
	}

}
