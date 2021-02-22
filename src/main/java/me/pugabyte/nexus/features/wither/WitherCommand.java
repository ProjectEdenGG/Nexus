package me.pugabyte.nexus.features.wither;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.staff")
public class WitherCommand extends CustomCommand {

	public WitherCommand(CommandEvent event) {
		super(event);
	}

	@Permission("group.admin")
	@Path("cutscene [difficulty]")
	void cutscene(@Arg("easy") Wither.Difficulty difficulty) {
		Wither.activePlayers.add(uuid());
		Wither.setDifficulty(difficulty);
		new BeginningCutscene();
	}

	@Path("joinTest")
	void join() {
		Wither.activePlayers.add(uuid());
		send("Joined nerd");
	}

}
