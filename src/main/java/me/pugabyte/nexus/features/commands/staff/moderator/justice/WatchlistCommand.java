package me.pugabyte.nexus.features.commands.staff.moderator.justice;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

import static me.pugabyte.nexus.utils.TimeUtils.shortDateFormat;

@NoArgsConstructor
@Permission("group.moderator")
public class WatchlistCommand extends _PunishmentCommand implements Listener {

	public WatchlistCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <reason...>")
	void run(@Arg(type = Punishments.class) List<Punishments> players, String input) {
		punish(players, input);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.WATCHLIST;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Tasks.waitAsync(Time.SECOND, () -> {
			if (!player.isOnline())
				return;

			if (Nerd.of(player).getRank().isMod())
				for (Player onlinePlayer : Bukkit.getOnlinePlayers())
					Punishments.of(onlinePlayer).getActiveWatchlist().ifPresent(watchlist ->
							PlayerUtils.send(player, json(PREFIX).next(getNotification(watchlist))));

			Punishments.of(player).getActiveWatchlist().ifPresent(watchlist ->
				Chat.broadcastIngame(json(PREFIX).next(getNotification(watchlist)), StaticChannel.STAFF));
		});
	}

	public JsonBuilder getNotification(Punishment watchlist) {
		return new JsonBuilder("&e" + getName() + " &cwas watchlisted for &e" + watchlist.getReason() + " &cby &e"
				+ Nickname.of(watchlist.getPunisher()) + " &con &e" + shortDateFormat(watchlist.getTimestamp().toLocalDate()))
				.hover("&eClick for more information")
				.command("/history " + watchlist.getName());
	}

}
