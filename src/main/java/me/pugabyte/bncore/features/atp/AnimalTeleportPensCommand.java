package me.pugabyte.bncore.features.atp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.atp.ATPMenuProvider.ATPGroup;
import me.pugabyte.bncore.features.warps.commands._WarpCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;

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
		if (player().getWorld().getName().equalsIgnoreCase("world"))
			new ATPMenu().open(player(), ATPGroup.LEGACY);
		else
			new ATPMenu().open(player(), ATPGroup.SURVIVAL);
	}

	public boolean isInATP() {
		WorldGuardUtils WGUtils = new WorldGuardUtils(player().getWorld());
		for (ProtectedRegion region : WGUtils.getRegionsAt(player().getLocation())) {
			if (region.getId().contains("atp"))
				return true;
		}
		return false;
	}

}
