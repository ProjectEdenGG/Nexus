package me.pugabyte.nexus.features.radar;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Permission("group.moderator")
public class CPSWatchCommand extends CustomCommand implements Listener {
	private static Map<Player, Integer> cpsMap = new HashMap<>();
	private static Map<Player, List<Player>> watchMap = new HashMap<>();
	private static String PREFIX = "&7&l[&cRadar&7&l]&f ";

	public CPSWatchCommand(@NonNull CommandEvent event) {
		super(event);
		super.PREFIX = PREFIX;
	}

	@Path("<player>")
	void cpsWatch(Player player) {
		watchMap.putIfAbsent(player, new ArrayList<>());
		List<Player> watchList = watchMap.get(player);

		if (watchList.contains(player())) {
			watchList.remove(player());
			send(PREFIX + "CPS Watcher &cdisabled &ffor " + player.getName());
		} else {
			watchList.add(player());
			send(PREFIX + "CPS Watcher &aenabled &ffor " + player.getName());
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.PHYSICAL) return;

		if (watchMap.get(player) == null || watchMap.get(player).size() == 0)
			return;

		cpsMap.putIfAbsent(player, 0);
		cpsMap.replace(player, cpsMap.get(player) + 1);

		if (cpsMap.get(player) != 1)
			return;

		Tasks.wait(20, () -> {
			Integer cps = cpsMap.get(player);
			if (cps == null)
				return;

			if (cps > 2) {
				final String color = (cps > 20) ? "&c" : (cps > 15) ? "&6" : "&e";
				watchMap.get(player).forEach(staff -> send(staff, PREFIX + player.getName() + "'s CPS is " + color + cps));
			}

			cpsMap.remove(player);
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!watchMap.containsKey(player))
			return;

		watchMap.get(player).forEach(staff ->
				send(staff, PREFIX + "&c" + player.getName() + " went offline"));
		watchMap.remove(player);
	}


}
