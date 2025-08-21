package gg.projecteden.nexus.features.radar;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@Permission(Group.MODERATOR)
public class ReachWatchCommand extends CustomCommand implements Listener {
	private static final Map<UUID, List<UUID>> WATCH_MAP = new HashMap<>();
	private static final String PREFIX = "&7&l[&cRadar&7&l]&f ";

	public ReachWatchCommand(@NonNull CommandEvent event) {
		super(event);
		super.PREFIX = PREFIX;
	}

	@Path("<player>")
	@Description("Monitor a player's reach")
	void reachWatch(Player player) {
		List<UUID> watchList = WATCH_MAP.computeIfAbsent(player.getUniqueId(), $ -> new ArrayList<>());

		if (watchList.contains(uuid())) {
			watchList.remove(uuid());
			send(PREFIX + "Reach Watcher &cdisabled &ffor " + player.getName());
		} else {
			watchList.add(uuid());
			send(PREFIX + "Reach Watcher &aenabled &ffor " + player.getName());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player attacker))
			return;

		if (WATCH_MAP.get(attacker.getUniqueId()) == null || WATCH_MAP.get(attacker.getUniqueId()).isEmpty())
			return;

		if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))
			return;

		Entity victim = event.getEntity();
		String victimName = victim.getType().equals(EntityType.PLAYER) ? victim.getName() : camelCase(victim.getType());
		Distance distance = Distance.distance(attacker, victim);

		if (distance.gt(3)) {
			String color = distance.gt(5) ? "&c" : distance.gt(3.7) ? "&6" : "&e";

			WATCH_MAP.get(attacker.getUniqueId()).forEach(staff ->
				send(staff, PREFIX + attacker.getName() + " --> " + victimName + " " + color + StringUtils.getDf().format(distance.getRealDistance())));
		}
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
