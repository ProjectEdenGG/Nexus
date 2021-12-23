package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.NexusCommand.ReloadCondition;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.TimeUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
@Permission("group.admin")
public class MaintenanceCommand extends CustomCommand implements Listener {
	@Getter
	private static boolean queued;
	private static boolean shuttingDown;

	private static final List<ReloadCondition> conditions = EnumUtils.valuesExcept(ReloadCondition.class, ReloadCondition.SMARTINVS);

	public MaintenanceCommand(@NonNull CommandEvent event) {
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
		shutdown();
	}

	private static void shutdown() {
		if (!queued || shuttingDown) return;

		conditions.forEach(ReloadCondition::run);

		shuttingDown = true;

		Koda.say("Shutting down server for maintenance, check Discord for updates");
		sendTitle();

		Tasks.wait(TickTime.SECOND.x(10), () -> {
			shuttingDown = false;
			conditions.forEach(ReloadCondition::run);
			for (Player player : OnlinePlayers.getAll())
				player.kickPlayer(colorize("&6&lShutting down server for maintenance!\n&eCheck Discord for updates\n&f\n&7" + TimeUtils.shortDateTimeFormat(LocalDateTime.now()) + " EST"));
			PlayerUtils.runCommandAsConsole("stop");
		});
	}

	private static void sendTitle() {
		final TitleBuilder titleBuilder = buildTitle();

		titleBuilder.send();
		titleBuilder.fade(0);
		AtomicInteger titleTaskId = new AtomicInteger();
		titleTaskId.set(Tasks.repeat(0, 5, () -> {
			if (!shuttingDown) {
				Tasks.cancel(titleTaskId.get());
				return;
			}

			titleBuilder.send();
		}));
	}

	private static TitleBuilder buildTitle() {
		return new TitleBuilder()
			.allPlayers()
			.title("&cShutting down server")
			.subtitle("&cfor maintenance. Check Discord for updates");
	}

	static {
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.SECOND.x(5), () -> {
			try {
				MaintenanceCommand.shutdown();
			} catch (Exception ex) {
				Nexus.log("Shutdown failed: " + ex.getMessage());
			}
		});
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!shuttingDown)
			return;

		buildTitle().send();
		Koda.dm(event.getPlayer(), "Shutting down server for maintenance, check Discord for updates");
	}

}
