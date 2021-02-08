package me.pugabyte.nexus.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
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

import static me.pugabyte.nexus.utils.StringUtils.ellipsis;
import static me.pugabyte.nexus.utils.StringUtils.shortDateFormat;

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
		watchlisted.setWatchlistedOn(LocalDateTime.now());
		watchlisted.setReason(reason);
		service.save(watchlisted);
		send(PREFIX + "Added &e" + watchlisted.getName() + " &3to the watchlist");
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
		send(watchlisted.toPrettyString());
	}

	@Async
	@Path("list [page]")
	void list(@Arg("1") int page) {
		List<Watchlisted> watchlistedPlayers = service.getAllActive();
		if (watchlistedPlayers.isEmpty())
			error("No players have been watchlisted");

		send(PREFIX + "Watchlisted players");
		BiFunction<Watchlisted, Integer, JsonBuilder> formatter = (watchlisted, index) ->
				json("&3" + (index + 1) + " &7" + shortDateFormat(watchlisted.getWatchlistedOn().toLocalDate()) + " &e" + watchlisted.getName() + " &7- " + ellipsis(watchlisted.getReason(), 50))
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

			if (new Nerd(player).getRank().isStaff())
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					Watchlisted watchlisted = service.get(onlinePlayer);
					if (watchlisted.isActive())
						PlayerUtils.send(player, watchlisted.getMessage());
				}

			Watchlisted watchlisted = service.get(player);
			if (watchlisted.isActive())
				Chat.broadcast(watchlisted.getMessage(), StaticChannel.STAFF);
		});
	}

}
