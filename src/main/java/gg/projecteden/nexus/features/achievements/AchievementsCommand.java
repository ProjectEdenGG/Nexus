package gg.projecteden.nexus.features.achievements;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.achievements.menu.AchievementGroupProvider;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;

@Disabled
@Aliases("ach")
@Permission("achievements.use")
public class AchievementsCommand extends CustomCommand {

	public AchievementsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("TODO")
	void run() {
		new AchievementGroupProvider().open(player());
	}

}

