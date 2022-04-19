package gg.projecteden.nexus.features.achievements;

import gg.projecteden.annotations.Disabled;
import gg.projecteden.nexus.features.achievements.menu.AchievementGroupProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

@Disabled
@Aliases("ach")
@Permission("achievements.use")
public class AchievementsCommand extends CustomCommand {

	public AchievementsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		new AchievementGroupProvider().open(player());
	}

}

