package gg.projecteden.nexus.features.events.aeveonproject.commands;

import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;

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
