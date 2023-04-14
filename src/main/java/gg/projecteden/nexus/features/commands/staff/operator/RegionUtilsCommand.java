package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NonNull;

@Permission(Group.SENIOR_STAFF)
public class RegionUtilsCommand extends CustomCommand {
	private final WorldGuardUtils worldguard;

	public RegionUtilsCommand(@NonNull CommandEvent event) {
		super(event);
		worldguard = new WorldGuardUtils(player());
	}

	@Description("View regions at your current location")
	void getRegionsAt() {
		send(PREFIX + "Found regions:");
		worldguard.getRegionsAt(location()).forEach(region -> send(region.getId()));
	}

	@Description("View regions matching a filter at your current location")
	void getRegionsLikeAt(String filter) {
		send(PREFIX + "Found regions:");
		worldguard.getRegionsLikeAt(filter, location()).forEach(region -> send(region.getId()));
	}

}
