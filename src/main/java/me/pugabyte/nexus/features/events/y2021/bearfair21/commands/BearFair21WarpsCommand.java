package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.warps.commands._WarpCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.warps.WarpService;
import me.pugabyte.nexus.models.warps.WarpType;

@Aliases({"bearfair21warp", "bf21warps", "bf21warp"})
public class BearFair21WarpsCommand extends _WarpCommand {
	BearFair21UserService userService = new BearFair21UserService();
	BearFair21User user;

	public BearFair21WarpsCommand(CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Path
	public void bearfair() {
		if (!isStaff()) {
			if (!BearFair21.canWarp())
				error("Soon™");

			if (user.isFirstVisit())
				error("To unlock the warp, you must first travel to Bear Fair aboard the space yacht at spawn");
		}

		teleport(new WarpService().get("bearfair", WarpType.BEAR_FAIR21));
	}

	@Override
	public void teleportNearest() {
		player().setFallDistance(0);
		super.teleportNearest();
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.BEAR_FAIR21;
	}
}
