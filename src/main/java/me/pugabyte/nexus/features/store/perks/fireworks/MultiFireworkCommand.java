package me.pugabyte.nexus.features.store.perks.fireworks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.FireworkLauncher;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;

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
