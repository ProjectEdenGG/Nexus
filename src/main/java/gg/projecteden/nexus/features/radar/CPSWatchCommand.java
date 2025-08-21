package gg.projecteden.nexus.features.radar;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
import java.util.UUID;

@NoArgsConstructor
@Permission(Group.MODERATOR)
public class CPSWatchCommand extends CustomCommand implements Listener {
	private static final Map<UUID, Integer> CPS_MAP = new HashMap<>();
	private static final Map<UUID, List<UUID>> WATCH_MAP = new HashMap<>();
	private static final String PREFIX = "&7&l[&cRadar&7&l]&f ";

	public CPSWatchCommand(@NonNull CommandEvent event) {
		super(event);
		super.PREFIX = PREFIX;
	}

	@Path("<player>")
	@Description("Monitor a player's click speed")
	void cpsWatch(Player player) {
		List<UUID> watchList = WATCH_MAP.computeIfAbsent(player.getUniqueId(), $ -> new ArrayList<>());

		if (watchList.contains(uuid())) {
			watchList.remove(uuid());
			send(PREFIX + "CPS Watcher &cdisabled &ffor " + player.getName());
		} else {
			watchList.add(uuid());
			send(PREFIX + "CPS Watcher &aenabled &ffor " + player.getName());
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.PHYSICAL)
			return;

		UUID uuid = player.getUniqueId();
		if (WATCH_MAP.get(uuid) == null || WATCH_MAP.get(uuid).isEmpty())
			return;

		CPS_MAP.putIfAbsent(uuid, 0);
		CPS_MAP.replace(uuid, CPS_MAP.get(uuid) + 1);

		if (CPS_MAP.get(uuid) != 1)
			return;

		Tasks.wait(20, () -> {
			Integer cps = CPS_MAP.get(uuid);
			if (cps == null)
				return;

			if (cps > 2) {
				final String color = (cps > 20) ? "&c" : (cps > 15) ? "&6" : "&e";
				WATCH_MAP.get(uuid).forEach(staff -> send(staff, PREFIX + player.getName() + "'s CPS is " + color + cps));
			}

			CPS_MAP.remove(uuid);
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!WATCH_MAP.containsKey(player.getUniqueId()))
			return;

		WATCH_MAP.remove(player.getUniqueId()).forEach(staff ->
				send(staff, PREFIX + "&c" + player.getName() + " went offline"));
	}

}
