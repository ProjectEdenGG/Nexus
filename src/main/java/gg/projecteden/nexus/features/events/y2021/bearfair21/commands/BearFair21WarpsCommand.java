package gg.projecteden.nexus.features.events.y2021.bearfair21.commands;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Disabled
@HideFromWiki
@Aliases({"bearfair21warp", "bf21warps", "bf21warp"})
public class BearFair21WarpsCommand extends _WarpCommand {

	public BearFair21WarpsCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	public void bearfair() {
		if (!isStaff())
			if (!BearFair21.canWarp())
				error("Soon™");

		teleport(getWarpType().get("bearfair"));
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
