package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.NexusCommand.ReloadCondition;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.Utils;
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

@NoArgsConstructor
@Permission(Group.SENIOR_STAFF)
public class RebootCommand extends CustomCommand implements Listener {
	@Getter
	private static boolean queued;
	private static boolean rebooting;
	@Getter
	private static boolean passive;
	private static List<ReloadCondition> excludedConditions;

	public static final String TIME = "2 minutes";

	private static final List<ReloadCondition> conditions = EnumUtils.valuesExcept(ReloadCondition.class, ReloadCondition.SMARTINVS);

	public RebootCommand(@NonNull CommandEvent event) {
		super(event);
	}

	public static boolean isForcedRebootQueued() {
		return queued && !passive;
	}

	@Path("[--excludedConditions]")
	@Confirm
	@Description("Queues a reboot as soon as possible")
	void queue(@Switch @Arg(type = ReloadCondition.class) List<ReloadCondition> excludedConditions) {
		queued = true;
		RebootCommand.excludedConditions = excludedConditions;
		tryReboot();
	}

	@Path("passive [--excludedConditions]")
	@Description("Queues a reboot for when there are no active players")
	void passive(@Switch @Arg(type = ReloadCondition.class) List<ReloadCondition> excludedConditions) {
		RebootCommand.passive = true;
		queue(excludedConditions);
		send(PREFIX + "Queued passive reboot");
	}

	@Path("cancel")
	@Description("Cancel a pending reboot")
	void cancel() {
		queued = false;
		rebooting = false;
		passive = false;
		excludedConditions = null;
		send(PREFIX + "Cancelled");
	}

	private static void tryReboot() {
		if (!queued || rebooting)
			return;

		final List<ReloadCondition> conditions = RebootCommand.conditions.stream()
			.filter(condition -> Nullables.isNullOrEmpty(excludedConditions) || !excludedConditions.contains(condition))
			.toList();

		conditions.forEach(ReloadCondition::run);

		if (passive)
			if (AFK.getActivePlayers() != 0)
				return;

		rebooting = true;

		Koda.say("Rebooting server, come back in " + TIME);
		title();

		Tasks.wait(TickTime.SECOND.x(10), () -> {
			rebooting = false;
			conditions.forEach(ReloadCondition::run);
			OnlinePlayers.getAll().forEach(RebootCommand::kick);
			Utils.bash("mark2 send -n " + (Nexus.getEnv() == Env.PROD ? "smp" : "update") + " ~restart");
		});
	}

	public static void kick(Player player) {
		player.kickPlayer(StringUtils.colorize("&6&lRebooting server!\n&eCome back in about " + TIME + "\n&f\n&7" + TimeUtils.shortDateTimeFormat(LocalDateTime.now()) + " EST"));
	}

	private static void title() {
		final TitleBuilder titleBuilder = new TitleBuilder()
			.allPlayers()
			.title("&cRebooting server")
			.subtitle("&cCome back in ~" + TIME);

		titleBuilder.send();

		titleBuilder.fade(0);
		AtomicInteger titleTaskId = new AtomicInteger();
		titleTaskId.set(Tasks.repeat(0, 5, () -> {
			if (!rebooting) {
				Tasks.cancel(titleTaskId.get());
				return;
			}

			titleBuilder.send();
		}));
	}

	static {
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.SECOND.x(5), () -> {
			try {
				RebootCommand.tryReboot();
			} catch (Exception ex) {
				Nexus.log("Reboot failed: " + ex.getMessage());
			}
		});
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!rebooting)
			return;

		new TitleBuilder().players(event.getPlayer()).title("&cRebooting server").subtitle("&cCome back in ~" + TIME).send();
		Koda.dm(event.getPlayer(), "Rebooting server, come back in " + TIME);
	}

}
