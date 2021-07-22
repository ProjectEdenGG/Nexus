package gg.projecteden.nexus.features.store.perks.fireworks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown.Part;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;

@Aliases("multifw")
@Permission("firework.launch")
@Cooldown(value = @Part(value = Time.SECOND, x = 10), bypass = "group.staff")
public class MultiFireworkCommand extends CustomCommand {

	public MultiFireworkCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void firework() {
		Tasks.Countdown.builder()
				.duration(Time.SECOND.x(20))
				.onSecond(i -> {
					if (i % 2 == 0)
						FireworkLauncher.random(location()).launch();
				})
				.doZero(true)
				.start();
	}
}
