package gg.projecteden.nexus.features.events.y2021.easter21;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Disabled
@Permission(Group.ADMIN)
public class Easter21WarpsCommand extends _WarpCommand {

	public Easter21WarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.EASTER21;
	}

}
