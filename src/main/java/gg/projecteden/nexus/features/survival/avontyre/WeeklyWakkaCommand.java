package gg.projecteden.nexus.features.survival.avontyre;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;

@Permission(Permission.Group.STAFF)
public class WeeklyWakkaCommand extends CustomCommand {

	public WeeklyWakkaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("getDevice")
	void getDevice() {
		PlayerUtils.giveItem(player(), WeeklyWakka.getTrackingDevice());
	}

}
