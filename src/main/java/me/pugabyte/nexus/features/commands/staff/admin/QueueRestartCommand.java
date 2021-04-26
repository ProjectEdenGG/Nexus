package me.pugabyte.nexus.features.commands.staff.admin;

import eden.utils.TimeUtils.Time;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.afk.AFK;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;

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
