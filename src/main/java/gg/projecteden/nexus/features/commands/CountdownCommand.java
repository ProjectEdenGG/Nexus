package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks.Countdown;

public class CountdownCommand extends CustomCommand {

	public CountdownCommand(CommandEvent event) {
		super(event);
	}

	@Path("[duration]")
	@Description("Count down from 5 to 0 in local chat")
	void countdown(@Arg(value = "5", max = 10) int duration) {
		Countdown.builder()
				.duration(duration * 20L)
				.onSecond(i -> runCommand("ch qm l " + i + "..."))
				.onComplete(() -> runCommand("ch qm l Go!"))
				.start();
	}
}
