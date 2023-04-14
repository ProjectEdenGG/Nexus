package gg.projecteden.nexus.features.atp;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.atp.ATPMenu.ATPGroup;
import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;

@Disabled
@Aliases("atp")
public class AnimalTeleportPensCommand extends _WarpCommand {

	public AnimalTeleportPensCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("ATP");
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.ATP;
	}

	@NoLiterals
	@Description("TODO")
	void menu() {
		if (!isInATP())
			error("You are not in an ATP region");

		if (new AnimalTeleportPens(player()).getEntities().size() == 0)
			error("There are no entities to teleport");

		if (player().getVehicle() != null)
			player().leaveVehicle();

		if (world().getName().equalsIgnoreCase("world"))
			new ATPMenu(ATPGroup.LEGACY).open(player());
		else
			new ATPMenu(ATPGroup.SURVIVAL).open(player());
	}

	public boolean isInATP() {
		return !new WorldGuardUtils(player()).getRegionsLikeAt("atp_.*", location()).isEmpty();
	}

}
