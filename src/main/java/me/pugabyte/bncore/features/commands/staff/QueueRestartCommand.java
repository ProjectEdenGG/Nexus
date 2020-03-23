package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;

@Permission("group.seniorstaff")
public class QueueRestartCommand extends CustomCommand {
	private static boolean restart = false;

	public QueueRestartCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(15), () -> {
			if (restart && AFK.getActivePlayers() == 0) {
				BNCore.log("Restart is queued");
				Tasks.wait(30 * 20, () -> {
					if (restart && AFK.getActivePlayers() == 0)
						Utils.runConsoleCommand("inject plugins/wget/restart.sh");
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
