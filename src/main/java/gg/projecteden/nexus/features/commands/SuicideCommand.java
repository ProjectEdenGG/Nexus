package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.GameMode;

public class SuicideCommand extends CustomCommand {

	public SuicideCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(TickTime.MINUTE)
	void suicide() {
		if (WorldGroup.of(player()) != WorldGroup.SURVIVAL)
			error("You can only do this command in the survival world.");
		if (isStaff())
			runCommand("god off");
		player().setGameMode(GameMode.SURVIVAL);
		player().setHealth(0.0);
	}

}
