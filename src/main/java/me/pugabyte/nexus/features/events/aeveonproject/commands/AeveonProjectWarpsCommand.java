package me.pugabyte.nexus.features.events.aeveonproject.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.warps.commands._WarpCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.StringUtils;

@Aliases({"apw"})
@NoArgsConstructor
@Permission("group.staff")
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
