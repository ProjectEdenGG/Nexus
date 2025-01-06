package gg.projecteden.nexus.features.radar;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Distance;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Permission(Group.MODERATOR)
public class ReachWatchCommand extends CustomCommand implements Listener {
	private static final Map<Player, List<Player>> watchMap = new HashMap<>();
	private final DecimalFormat nf = new DecimalFormat("#.00");
	private static final String PREFIX = "&7&l[&cRadar&7&l]&f ";

	public ReachWatchCommand(@NonNull CommandEvent event) {
		super(event);
		super.PREFIX = PREFIX;
	}

	@Path("<player>")
	@Description("Monitor a player's reach")
	void reachWatch(Player player) {
		watchMap.putIfAbsent(player, new ArrayList<>());
		List<Player> watchList = watchMap.get(player);

		if (watchList.contains(player())) {
			watchList.remove(player());
			send(PREFIX + "Reach Watcher &cdisabled &ffor " + player.getName());
		} else {
			watchList.add(player());
			send(PREFIX + "Reach Watcher &aenabled &ffor " + player.getName());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player attacker))
			return;

		if (watchMap.get(attacker) == null || watchMap.get(attacker).size() == 0)
			return;

		if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))
			return;

		Entity victim = event.getEntity();
		String victimName = victim.getType().equals(EntityType.PLAYER) ? victim.getName() : camelCase(victim.getType());
		Distance distance = Distance.distance(attacker, victim);

		if (distance.gt(3)) {
			String color = distance.gt(5) ? "&c" : distance.gt(3.7) ? "&6" : "&e";

			watchMap.get(attacker).forEach(staff ->
				send(staff, PREFIX + attacker.getName() + " --> " + victimName + " " + color + nf.format(distance.getRealDistance())));
		}
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
