package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.GameMode;

public class SuicideCommand extends CustomCommand {

	public SuicideCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Cooldown(@Part(Time.MINUTE))
	void suicide() {
		if (!WorldGroup.get(player()).equals(WorldGroup.SURVIVAL))
			error("You can only do this command in the survival world.");
		if (isStaff())
			runCommand("god off");
		player().setGameMode(GameMode.SURVIVAL);
		player().setHealth(0.0);
	}

}
