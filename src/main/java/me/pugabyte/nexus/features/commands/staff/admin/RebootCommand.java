package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.NexusCommand.ReloadCondition;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class RebootCommand extends CustomCommand {
	@Getter
	private static boolean queued;

	public RebootCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Confirm
	void run() {
		if (queued) {
			queued = false;
			send(PREFIX + "Cancelled");
			return;
		}

		queued = true;

		ReloadCondition.MINIGAMES.run();
		ReloadCondition.CRATES.run();
		ReloadCondition.WITHER.run();
	}

	static {
//		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(5), );
	}

}
