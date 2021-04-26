package me.pugabyte.nexus.features.achievements;

import eden.annotations.Disabled;
import lombok.NonNull;
import me.pugabyte.nexus.features.achievements.menu.AchievementProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Disabled
@Aliases("ach")
@Permission("achievements.use")
public class AchievementsCommand extends CustomCommand {

	public AchievementsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		AchievementProvider.open(player());
	}

}

