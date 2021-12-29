package gg.projecteden.nexus.features.store.perks.fireworks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.utils.TimeUtils.TickTime;

import static gg.projecteden.nexus.features.store.perks.fireworks.FireworkCommand.PERMISSION;

@Aliases("fw")
@Permission(PERMISSION)
@Cooldown(value = TickTime.SECOND, bypass = "Group.STAFF")
public class FireworkCommand extends CustomCommand {
	public static final String PERMISSION = "firework.launch";

	public FireworkCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void firework() {
		FireworkLauncher.random(location()).launch();
	}
}
