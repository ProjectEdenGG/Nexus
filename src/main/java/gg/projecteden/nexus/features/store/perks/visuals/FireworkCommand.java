package gg.projecteden.nexus.features.store.perks.visuals;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Cooldown;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CommandCooldownException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.Tasks;

import static gg.projecteden.nexus.features.store.perks.visuals.FireworkCommand.PERMISSION;

@Aliases("fw")
@Permission(PERMISSION)
@Cooldown(value = TickTime.SECOND, bypass = Group.STAFF)
@WikiConfig(rank = "Store", feature = "Visuals")
public class FireworkCommand extends CustomCommand {
	public static final String PERMISSION = "firework.launch";

	public FireworkCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[count]")
	@Description("Launch randomized fireworks")
	void firework(@Arg(value = "1", min = 1, max = 10) int count) {
		final long duration = TickTime.SECOND.x(count * 2);

		if (!new CooldownService().check(uuid(), "firework", duration))
			throw new CommandCooldownException(uuid(), "firework");

		Tasks.Countdown.builder()
			.duration(duration)
			.onSecond(i -> {
				if (i % 2 == 0)
					FireworkLauncher.random(location()).launch();
			})
			.doZero(true)
			.start();
	}

}
