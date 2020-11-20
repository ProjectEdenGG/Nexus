package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tasks;

public class CountdownCommand extends CustomCommand {

	public CountdownCommand(CommandEvent event) {
		super(event);
	}

	@Path("[duration]")
	void countdown(@Arg("5") int duration) {
		Tasks.Countdown.builder()
				.duration(duration * 20)
				.onSecond(i -> runCommand("ch qm l " + i + "..."))
				.onComplete(() -> runCommand("ch qm l Go!"))
				.start();
	}
}
