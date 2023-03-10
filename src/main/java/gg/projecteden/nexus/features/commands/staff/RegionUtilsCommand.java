package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NonNull;

@Permission(Group.SENIOR_STAFF)
public class RegionUtilsCommand extends CustomCommand {
	private final WorldGuardUtils worldguard;

	public RegionUtilsCommand(@NonNull CommandEvent event) {
		super(event);
		worldguard = new WorldGuardUtils(player());
	}

	@Path("getRegionsAt")
	@Description("View regions at your current location")
	void getRegionsAt() {
		send(PREFIX + "Found regions:");
		worldguard.getRegionsAt(location()).forEach(region -> send(region.getId()));
	}

	@Path("getRegionsLikeAt <filter>")
	@Description("View regions matching a filter at your current location")
	void getRegionsLikeAt(String filter) {
		send(PREFIX + "Found regions:");
		worldguard.getRegionsLikeAt(filter, location()).forEach(region -> send(region.getId()));
	}

}
