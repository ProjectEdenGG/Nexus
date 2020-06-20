package me.pugabyte.bncore.features.achievements;

import lombok.NonNull;
import me.pugabyte.bncore.features.achievements.menu.AchievementProvider;
import me.pugabyte.bncore.framework.annotations.Disabled;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

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

