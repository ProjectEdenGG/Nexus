package me.pugabyte.bncore.features.warps.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.WorldGroup;

import java.util.Arrays;

public class SpawnCommand extends CustomCommand {

	public SpawnCommand(CommandEvent event) {
		super(event);
	}

	WarpService service = new WarpService();

	@Path
	void run() {
		Warp warp;
		if (Arrays.asList(WorldGroup.SKYBLOCK.getWorlds()).contains(player().getWorld().getName()))
			warp = service.get("skyblock", WarpType.NORMAL);
		else if (Arrays.asList(WorldGroup.CREATIVE.getWorlds()).contains(player().getWorld().getName()))
			warp = service.get("creative", WarpType.NORMAL);
		else if (player().getWorld().getName().equalsIgnoreCase("bearchallenges"))
			warp = service.get("bfc", WarpType.NORMAL);
		else
			warp = service.get("spawn", WarpType.NORMAL);
		if (warp == null) error("There was an error while executing this command");
		if (!warp.getName().equalsIgnoreCase("spawn"))
			send(json("&3If you want to go to the survival spawn, &eclick here.").suggest("/warp spawn"));
		warp.teleport(player());
	}

}
