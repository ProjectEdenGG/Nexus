package gg.projecteden.nexus.features.radar;

import gg.projecteden.nexus.features.chat.commands.MessageCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@Permission(Group.MODERATOR)
public class CommandWatchCommand extends CustomCommand implements Listener {
	private static final Map<UUID, List<UUID>> WATCH_MAP = new HashMap<>();
	private static final List<String> MESSAGE_ALIASES = Arrays.asList(MessageCommand.class.getAnnotation(Aliases.class).value());
	private static final String PREFIX = "&7&l[&cRadar&7&l]&f ";

	public CommandWatchCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Description("View commands a player is running")
	void commandWatch(Player target) {
		if (!isAdmin() && !Rank.GUEST.equals(Rank.of(target)))
			error("You can only command watch guests");

		List<UUID> watchList = WATCH_MAP.computeIfAbsent(target.getUniqueId(), $ -> new ArrayList<>());

		if (watchList.contains(uuid())) {
			watchList.remove(uuid());
			send(PREFIX + "Command Watcher &cdisabled &ffor " + target.getName());
		} else {
			watchList.add(uuid());
			send(PREFIX + "Command Watcher &aenabled &ffor " + target.getName());
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (WATCH_MAP.get(player.getUniqueId()) == null || WATCH_MAP.get(player.getUniqueId()).isEmpty())
			return;

		String message = event.getMessage();
		String command = message.split(" ")[0].replace("/", "");

		WATCH_MAP.get(player.getUniqueId()).forEach(staff -> {
			if (MESSAGE_ALIASES.contains(command)) {
				if (Rank.of(staff).isAdmin())
					send(staff, PREFIX + player.getName() + ":&7 " + message);
			} else
				send(staff, PREFIX + player.getName() + ":&7 " + message);
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if (!WATCH_MAP.containsKey(uuid))
			return;

		WATCH_MAP.remove(uuid).forEach(staff ->
			send(staff, PREFIX + "&c" + player.getName() + " went offline"));
	}
}
