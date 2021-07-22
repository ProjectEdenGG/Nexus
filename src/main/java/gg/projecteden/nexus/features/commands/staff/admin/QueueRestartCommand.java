package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.NonNull;

@Permission("group.seniorstaff")
public class QueueRestartCommand extends CustomCommand {
	private static boolean restart = false;

	public QueueRestartCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(15), () -> {
			if (restart && AFK.getActivePlayers() == 0) {
				Nexus.log("Restart is queued");
				Tasks.wait(30 * 20, () -> {
					if (restart && AFK.getActivePlayers() == 0)
						PlayerUtils.runCommandAsConsole("inject plugins/wget/restart.sh");
				});
			}
		});
	}

	@Path("<true|false>")
	void toggle(boolean enable) {
		restart = enable;
		if (restart)
			send("&cRestart queued");
		else
			send("&eRestart not queued");
	}

}
