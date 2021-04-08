package me.pugabyte.nexus.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.watchlist.Watchlisted;
import me.pugabyte.nexus.models.watchlist.Watchlisted.Note;
import me.pugabyte.nexus.models.watchlist.WatchlistedService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;

import static me.pugabyte.nexus.utils.PlayerUtils.getPlayer;
import static me.pugabyte.nexus.utils.StringUtils.ellipsis;
import static me.pugabyte.nexus.utils.StringUtils.shortDateFormat;
import static me.pugabyte.nexus.utils.StringUtils.shortDateTimeFormat;

@NoArgsConstructor
@Permission("group.staff")
public class WatchlistCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("Watchlist");
	private final WatchlistedService service = new WatchlistedService();

	public WatchlistCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("add <player> <reason...>")
	void add(Watchlisted watchlisted, String reason) {
		if (watchlisted.isActive())
			error(watchlisted.getName() + " is already watchlisted for &7" + watchlisted.getReason());

		watchlisted.setActive(true);
		watchlisted.setWatchlister(uuid());
		watchlisted.setWatchlistedOn(LocalDateTime.now());
		watchlisted.setReason(reason);
		service.save(watchlisted);
		send(PREFIX + "Added &e" + watchlisted.getName() + " &3to the watchlist");
		Discord.send("**" + PREFIX.trim() + "** " + name() + " added " + watchlisted.getName() + " to the watchlist for " + watchlisted.getReason(), TextChannel.STAFF_WATCHLIST);
	}

	@Path("remove <player>")
	void remove(Watchlisted watchlisted) {
		if (!watchlisted.isActive())
			error(watchlisted.getName() + " is not watchlisted");

		watchlisted.setActive(false);
		service.save(watchlisted);
		send(PREFIX + "Removed &e" + watchlisted.getName() + " &3from the watchlist");
	}

	@Path("notes add <player> <note...>")
	void notesAdd(Watchlisted watchlisted, String text) {
		watchlisted.getNotes().add(new Note(uuid(), text));
		service.save(watchlisted);
		send(PREFIX + "Added note to " + watchlisted.getName());
	}

	@Path("info <player>")
	void info(Watchlisted watchlisted) {
		String playerName = watchlisted.getOfflinePlayer().getName();
		String active = StringUtils.bool(watchlisted.isActive());
		String date = watchlisted.getWatchlistedOn() == null ? "null" : StringUtils.shortDateTimeFormat(watchlisted.getWatchlistedOn());
		List<Note> notes = watchlisted.getNotes();

		line();
		send(PREFIX + playerName);
		send("&3Active: &e" + active);
		send("&3Date: &e" + date);
		send("&3Reason: &e" + watchlisted.getReason());
		send("&3Notes: ");
		for (Note entry : notes) {
			String author = getPlayer(entry.getAuthor()).getName();
			String timestamp = shortDateFormat(entry.getTimestamp().toLocalDate());
			send(json("&7- " + timestamp + " &3" + author + ": &e" + entry.getNote()).hover("&e" + shortDateTimeFormat(entry.getTimestamp())));
		}
		line();
	}

	@Async
	@Path("list [page]")
	void list(@Arg("1") int page) {
		List<Watchlisted> watchlistedPlayers = service.getAllActive();
		if (watchlistedPlayers.isEmpty())
			error("No players have been watchlisted");

		send(PREFIX + "Watchlisted players");
		BiFunction<Watchlisted, String, JsonBuilder> formatter = (watchlisted, index) ->
				json("&3" + index + " &7" + shortDateFormat(watchlisted.getWatchlistedOn().toLocalDate()) + " &e"
						+ watchlisted.getName() + " &7- " + ellipsis(watchlisted.getReason(), 50))
						.addHover("&7" + watchlisted.getReason())
						.command("/watchlist info " + watchlisted.getName());
		paginate(watchlistedPlayers, formatter, "/watchlist list", page);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Tasks.waitAsync(Time.SECOND, () -> {
			if (!player.isOnline())
				return;

			WatchlistedService service = new WatchlistedService();

			if (Nerd.of(player).getRank().isStaff())
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					Watchlisted watchlisted = service.get(onlinePlayer);
					if (watchlisted.isActive())
						PlayerUtils.send(player, json(PREFIX).next(watchlisted.getNotification()));
				}

			Watchlisted watchlisted = service.get(player);
			if (watchlisted.isActive()) {
				JsonBuilder notification = watchlisted.getNotification();
				Chat.broadcastIngame(json(PREFIX).next(notification), StaticChannel.STAFF);
			}
		});
	}

}
