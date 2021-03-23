package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.features.NexusCommand.ReloadCondition;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.TitleUtils;

@Permission("group.admin")
public class RebootCommand extends CustomCommand {
	@Getter
	private static boolean queued;

	public RebootCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Confirm
	void reboot() {
		if (queued) {
			queued = false;
			send(PREFIX + "Cancelled");
			return;
		}

		queued = true;

		ReloadCondition.MINIGAMES.run();
		ReloadCondition.CRATES.run();
		ReloadCondition.WITHER.run();

		// For testing purposes
		TitleUtils.sendTitle(player(), "&cRebooting server,", "&cbrb ~60 seconds");
		Tasks.wait(Time.SECOND.x(10), () -> player().kickPlayer("Rebooting server! Join back in about 60 seconds"));

		// Actual code
//		TitleUtils.sendTitleToAllPlayers("&cRebooting server,", "&cbrb ~60 seconds");
//		Tasks.wait(Time.SECOND.x(10), () -> {
//			for (Player player : Bukkit.getOnlinePlayers())
//				player.kickPlayer("Rebooting server! Join back in about 60 seconds");
//		});


	}

	static {
//		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(5), );
	}

}
