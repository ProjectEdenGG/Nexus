package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks.Countdown;

public class CountdownCommand extends CustomCommand {

	public CountdownCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
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
