package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.NexusCommand.ReloadCondition;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.Env;
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
@Permission(Group.SENIOR_STAFF)
public class RebootCommand extends CustomCommand implements Listener {
	@Getter
	private static boolean queued;
	private static boolean rebooting;

	private static final List<ReloadCondition> conditions = EnumUtils.valuesExcept(ReloadCondition.class, ReloadCondition.SMARTINVS);

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
		reboot();
	}

	private static void reboot() {
		if (!queued || rebooting) return;

		conditions.forEach(ReloadCondition::run);

		rebooting = true;

		Koda.say("Rebooting server, come back in 60 seconds");
		title();

		Tasks.wait(TickTime.SECOND.x(10), () -> {
			rebooting = false;
			conditions.forEach(ReloadCondition::run);
			for (Player player : OnlinePlayers.getAll())
				player.kickPlayer(colorize("&6&lRebooting server!\n&eCome back in about 60 seconds\n&f\n&7" + TimeUtils.shortDateTimeFormat(LocalDateTime.now()) + " EST"));
			Utils.bash("mark2 send -n " + (Nexus.getEnv() == Env.PROD ? "smp" : "test") + " ~restart");
		});
	}

	private static void title() {
		final TitleBuilder titleBuilder = new TitleBuilder()
			.allPlayers()
			.title("&cRebooting server")
			.subtitle("&cCome back in ~60 seconds");

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
				RebootCommand.reboot();
			} catch (Exception ex) {
				Nexus.log("Reboot failed: " + ex.getMessage());
			}
		});
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!rebooting)
			return;

		new TitleBuilder().players(event.getPlayer()).title("&cRebooting server").subtitle("&cCome back in ~60 seconds").send();
		Koda.dm(event.getPlayer(), "Rebooting server, come back in 60 seconds");
	}

}
