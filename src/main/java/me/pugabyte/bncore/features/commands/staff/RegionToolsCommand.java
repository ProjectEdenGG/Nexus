package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGuardUtils;

@Permission("group.staff")
public class RegionToolsCommand extends CustomCommand {
	WorldGuardUtils wgUtils;

	public RegionToolsCommand(@NonNull CommandEvent event) {
		super(event);
		wgUtils = new WorldGuardUtils(player());
	}

	@Path("getRegionsLikeAt <filter>")
	void getRegionsLikeAt(String filter) {
		send(PREFIX + "Found regions:");
		wgUtils.getRegionsLikeAt(filter, player().getLocation()).forEach(region -> send(region.getId()));
	}

}
