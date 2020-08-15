package me.pugabyte.bncore.features.holidays.aeveonproject.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.warps.commands._WarpCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.StringUtils;

@Aliases({"apw"})
@NoArgsConstructor
public class AeveonProjectWarpsCommand extends _WarpCommand {

	public AeveonProjectWarpsCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("APWarps");
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.AEVEON_PROJECT;
	}
}
