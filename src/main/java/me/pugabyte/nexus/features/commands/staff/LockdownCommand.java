package me.pugabyte.nexus.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.Channel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Aliases("ld")
@NoArgsConstructor
@Permission("group.moderator")
public class LockdownCommand extends CustomCommand implements Listener {
	private static boolean lockdown = false;
	private static String reason = null;

	public LockdownCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("start <reason...>")
	void start(String reason) {
		if (lockdown)
			send(PREFIX + "Overriding previous lockdown: &c" + LockdownCommand.reason);

		lockdown = true;
		LockdownCommand.reason = reason;

		// TODO: Announcer helpers
		tellStaff(PREFIX + player().getName() + " initiated lockdown: &c" + reason);

		for (Player player : Bukkit.getOnlinePlayers())
			if (!canBypass(player.getUniqueId())) {
				player.kickPlayer(getLockdownReason());
				tellStaff(PREFIX + "Removed " + player.getName() + " from server");
			}
	}

	@Path("end")
	void end() {
		if (!lockdown)
			error("Lockdown not enabled");

		lockdown = false;
		reason = null;

		tellStaff(PREFIX + player().getName() + " ended lockdown");
	}

	@EventHandler
	public void onConnect(AsyncPlayerPreLoginEvent event) {
		if (lockdown && event.getLoginResult() == Result.ALLOWED)
			if (!canBypass(event.getUniqueId())) {
				event.disallow(Result.KICK_OTHER, getLockdownReason());
				tellStaff(PREFIX + "Prevented " + event.getName() + " from joining the server");
			}
	}

	private void tellStaff(String message) {
		Discord.send(message, Channel.STAFF_BRIDGE, Channel.STAFF_LOG);
		for (Player player : Bukkit.getOnlinePlayers())
			if (PlayerUtils.isModeratorGroup(player))
				send(player, message);
	}

	public boolean canBypass(UUID player) {
		Hours hours = new HoursService().get(player);
		return hours.getTotal() > (30 * 60);
	}

	private String getLockdownReason() {
		return colorize("&e&lBear Nation &3&lis in &4&llockdown &3&lmode.\n\n&eReason: &c" + reason + "\n\n&3Please check back soon!");
	}

}
