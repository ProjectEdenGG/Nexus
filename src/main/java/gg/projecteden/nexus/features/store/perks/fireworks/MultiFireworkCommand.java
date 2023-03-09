package gg.projecteden.nexus.features.store.perks.fireworks;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.Tasks;

@Aliases("multifw")
@Permission("firework.launch")
@Cooldown(value = TickTime.SECOND, x = 10, bypass = Group.STAFF)
public class MultiFireworkCommand extends CustomCommand {

	public MultiFireworkCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Launch 10 randomized fireworks")
	void firework() {
		Tasks.Countdown.builder()
				.duration(TickTime.SECOND.x(20))
				.onSecond(i -> {
					if (i % 2 == 0)
						FireworkLauncher.random(location()).launch();
				})
				.doZero(true)
				.start();
	}
}
