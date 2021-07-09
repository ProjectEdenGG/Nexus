package me.pugabyte.nexus.features.commands.staff.admin;

import eden.utils.Env;
import eden.utils.TimeUtils.Time;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.NexusCommand.ReloadCondition;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils;
import me.pugabyte.nexus.utils.TitleBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
@Permission("group.seniorstaff")
public class RebootCommand extends CustomCommand implements Listener {
	@Getter
	private static boolean queued;
	private static boolean rebooting;

	private static final List<ReloadCondition> conditions = Arrays.asList(ReloadCondition.MINIGAMES, ReloadCondition.CRATES, ReloadCondition.WITHER);

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

		new TitleBuilder().allPlayers().title("&cRebooting server").subtitle("&cCome back in ~60 seconds").send();
		Koda.say("Rebooting server, come back in 60 seconds");
		rebooting = true;
		Tasks.wait(Time.SECOND.x(10), () -> {
			rebooting = false;
			conditions.forEach(ReloadCondition::run);
			for (Player player : PlayerUtils.getOnlinePlayers())
				player.kickPlayer(colorize("&6&lRebooting server!\n&eCome back in about 60 seconds\n&f\n&7" + TimeUtils.shortDateTimeFormat(LocalDateTime.now()) + " EST"));
			BashCommand.execute("mark2 send -n " + (Nexus.getEnv() == Env.PROD ? "smp" : "test") + " ~restart");
		});
	}

	static {
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(5), () -> {
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
