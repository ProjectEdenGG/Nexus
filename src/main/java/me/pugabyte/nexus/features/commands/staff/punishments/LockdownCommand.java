package me.pugabyte.nexus.features.commands.staff.punishments;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Aliases("ld")
@NoArgsConstructor
@Permission("group.moderator")
public class LockdownCommand extends CustomCommand implements Listener {
	private static boolean lockdown = false;
	private static String reason = null;
	private static Set<UUID> bypass = new HashSet<>();

	public LockdownCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("start <reason...>")
	void start(String reason) {
		if (lockdown)
			send(PREFIX + "Overriding previous lockdown: &c" + LockdownCommand.reason);

		lockdown = true;
		LockdownCommand.reason = reason;

		String message = name() + " initiated lockdown: &c" + reason;
		broadcast(message);

		for (Player player : Bukkit.getOnlinePlayers())
			if (!canBypass(player.getUniqueId())) {
				player.kickPlayer(getLockdownReason());
				broadcast("Removed " + player.getName() + " from server");
			}
	}

	@Path("end")
	void end() {
		if (!lockdown)
			error("Lockdown not enabled");

		lockdown = false;
		reason = null;
		bypass.clear();

		broadcast(name() + " ended lockdown");
	}

	@Path("bypass add <player>")
	void bypassAdd(OfflinePlayer player) {
		bypass.add(player.getUniqueId());
		send(PREFIX + "Added " + player.getName() + " to bypass list");
	}

	@Path("bypass remove <player>")
	void bypassRemove(OfflinePlayer player) {
		bypass.remove(player.getUniqueId());
		send(PREFIX + "Removed " + player.getName() + " from bypass list");
	}

	@EventHandler
	public void onConnect(AsyncPlayerPreLoginEvent event) {
		if (lockdown && event.getLoginResult() == Result.ALLOWED)
			if (!canBypass(event.getUniqueId())) {
				event.disallow(Result.KICK_OTHER, getLockdownReason());
				broadcast("Prevented " + event.getName() + " from joining the server");
			}
	}

	private void broadcast(String message) {
		Chat.broadcastIngame(PREFIX + message, StaticChannel.STAFF);
		Chat.broadcastDiscord(DISCORD_PREFIX + message, StaticChannel.STAFF);
		Discord.send(DISCORD_PREFIX + message, TextChannel.STAFF_LOG);
	}

	public boolean canBypass(UUID player) {
		if (bypass.contains(player))
			return true;

		Hours hours = new HoursService().get(player);
		return hours.getTotal() > (30 * 60);
	}

	private String getLockdownReason() {
		return colorize("&e&lBear Nation &3&lis in &4&llockdown &3&lmode.\n\n&eReason: &c" + reason + "\n\n&3Please check back soon!");
	}

}
