package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;

@Description("Count down to 0 in local chat")
public class CountdownCommand extends CustomCommand {

	public CountdownCommand(CommandEvent event) {
		super(event);
	}

	@Path("[duration]")
	void countdown(@Arg(value = "5", max = 10) int duration) {
		Tasks.Countdown.builder()
				.duration(duration * 20)
				.onSecond(i -> runCommand("ch qm l " + i + "..."))
				.onComplete(() -> runCommand("ch qm l Go!"))
				.start();
	}
}
