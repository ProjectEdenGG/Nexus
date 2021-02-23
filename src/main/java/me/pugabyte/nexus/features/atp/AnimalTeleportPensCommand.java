package me.pugabyte.nexus.features.atp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.atp.ATPMenuProvider.ATPGroup;
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
		if (player().getVehicle() != null) {
			send(PREFIX + "You cannot be riding a mount while using an ATP");
			player().leaveVehicle();
		}
		if (world().getName().equalsIgnoreCase("world"))
			new ATPMenu().open(player(), ATPGroup.LEGACY);
		else
			new ATPMenu().open(player(), ATPGroup.SURVIVAL);
	}

	public boolean isInATP() {
		WorldGuardUtils WGUtils = new WorldGuardUtils(player());
		for (ProtectedRegion region : WGUtils.getRegionsAt(location())) {
			if (region.getId().contains("atp"))
				return true;
		}
		return false;
	}

}
