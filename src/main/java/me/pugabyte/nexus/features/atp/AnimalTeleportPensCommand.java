package me.pugabyte.nexus.features.atp;

import me.pugabyte.nexus.features.atp.ATPMenu.ATPGroup;
import me.pugabyte.nexus.features.warps.commands._WarpCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;

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

	@Path
	void menu() {
		if (!isInATP())
			error("You are not in an ATP region.");

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
