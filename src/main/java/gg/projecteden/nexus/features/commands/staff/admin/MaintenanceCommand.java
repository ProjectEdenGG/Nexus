package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.NexusCommand.ReloadCondition;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.ErasureType;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.Confirm;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
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

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class MaintenanceCommand extends CustomCommand implements Listener {
	@Getter
	private static boolean queued;
	private static boolean shuttingDown;
	private static List<ReloadCondition> excludedConditions;

	private static final List<ReloadCondition> conditions = EnumUtils.valuesExcept(ReloadCondition.class, ReloadCondition.SMARTINVS);

	public MaintenanceCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Confirm
	@NoLiterals
	@Description("Shut down the server for maintenance")
	void run(@Switch @ErasureType(ReloadCondition.class) List<ReloadCondition> excludedConditions) {
		queued = true;
		MaintenanceCommand.excludedConditions = excludedConditions;
		shutdown();
	}

	@Description("Cancel a queued shutdown")
	void cancel() {
		queued = false;
		MaintenanceCommand.excludedConditions = null;
		send(PREFIX + "Cancelled");
	}

	private static void shutdown() {
		if (!queued || shuttingDown) return;

		final List<ReloadCondition> conditions = MaintenanceCommand.conditions.stream()
			.filter(condition -> isNullOrEmpty(excludedConditions) || !excludedConditions.contains(condition))
			.toList();

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
