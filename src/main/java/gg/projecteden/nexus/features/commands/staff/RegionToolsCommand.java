package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NonNull;

@Permission("group.staff")
public class RegionToolsCommand extends CustomCommand {
	private final WorldGuardUtils worldguard;

	public RegionToolsCommand(@NonNull CommandEvent event) {
		super(event);
		worldguard = new WorldGuardUtils(player());
	}

	@Path("getRegionsAt")
	void getRegionsAt() {
		send(PREFIX + "Found regions:");
		worldguard.getRegionsAt(location()).forEach(region -> send(region.getId()));
	}

	@Path("getRegionsLikeAt <filter>")
	void getRegionsLikeAt(String filter) {
		send(PREFIX + "Found regions:");
		worldguard.getRegionsLikeAt(filter, location()).forEach(region -> send(region.getId()));
	}

}
