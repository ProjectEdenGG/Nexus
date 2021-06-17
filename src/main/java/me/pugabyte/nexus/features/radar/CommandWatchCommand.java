package me.pugabyte.nexus.features.radar;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.commands.MessageCommand;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
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

@NoArgsConstructor
@Permission("group.moderator")
public class CommandWatchCommand extends CustomCommand implements Listener {
	private static final Map<Player, List<Player>> watchMap = new HashMap<>();
	private static final List<String> messageAliases = Arrays.asList(MessageCommand.class.getAnnotation(Aliases.class).value());
	private static final String PREFIX = "&7&l[&cRadar&7&l]&f ";

	public CommandWatchCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void commandWatch(Player target) {
		if (!isAdmin() && !Rank.GUEST.equals(Nerd.of(target).getRank()))
			error("You can only command watch guests");

		watchMap.putIfAbsent(target, new ArrayList<>());
		List<Player> watchList = watchMap.get(target);

		if (watchList.contains(player())) {
			watchList.remove(player());
			send(PREFIX + "Command Watcher &cdisabled &ffor " + target.getName());
		} else {
			watchList.add(player());
			send(PREFIX + "Command Watcher &aenabled &ffor " + target.getName());
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (watchMap.get(player) == null || watchMap.get(player).size() == 0)
			return;

		String message = event.getMessage();
		String command = message.split(" ")[0].replace("/", "");

		watchMap.get(player).forEach(staff -> {
			if (messageAliases.contains(command)) {
				if (isAdmin(staff))
					send(staff, PREFIX + player.getName() + ":&7 " + message);
			} else
				send(staff, PREFIX + player.getName() + ":&7 " + message);
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!watchMap.containsKey(player))
			return;

		watchMap.get(player).forEach(staff -> send(staff, PREFIX + "&c" + player.getName() + " went offline"));
		watchMap.remove(player);
	}
}
