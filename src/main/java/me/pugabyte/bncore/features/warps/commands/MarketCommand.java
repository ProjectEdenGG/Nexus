package me.pugabyte.bncore.features.warps.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.WorldGroup;

public class MarketCommand extends CustomCommand {
	WarpService service = new WarpService();

	public MarketCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		Warp warp;
		if (WorldGroup.get(player().getWorld()) == WorldGroup.SKYBLOCK)
			warp = service.get("skyblock-market", WarpType.NORMAL);
		else
			warp = service.get("market", WarpType.NORMAL);
		warp.teleport(player());
	}

}
