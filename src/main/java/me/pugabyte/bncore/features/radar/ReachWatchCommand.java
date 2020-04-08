package me.pugabyte.bncore.features.radar;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

@NoArgsConstructor
@Permission("group.moderator")
public class ReachWatchCommand extends CustomCommand implements Listener {
	static Map<Player, List<Player>> watchMap = new HashMap<>();
	private DecimalFormat nf = new DecimalFormat("#.00");
	private static String PREFIX = "&7&l[&cRadar&7&l]&f ";

	public ReachWatchCommand(@NonNull CommandEvent event) {
		super(event);
		super.PREFIX = PREFIX;
	}

	@Path("<player>")
	void reachWatch(Player player) {
		watchMap.putIfAbsent(player, new ArrayList<>());
		List<Player> watchList = watchMap.get(player);

		if (watchList.contains(player())) {
			watchList.remove(player());
			send(PREFIX + "Reach Watcher &cdisabled &3for " + player.getName());
		} else {
			watchList.add(player());
			send(PREFIX + "Reach Watcher &aenabled &3for " + player.getName());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player attacker = (Player) event.getDamager();

		if (watchMap.get(attacker) == null || watchMap.get(attacker).size() == 0)
			return;

		if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))
			return;

		Entity victim = event.getEntity();
		String victimName = victim.getType().equals(EntityType.PLAYER) ? victim.getName() : camelCase(victim.getType().toString());
		double distnace = attacker.getLocation().distance(victim.getLocation());

		if (distnace > 3) {
			String color = (distnace > 5) ? "&c" : (distnace > 3.7) ? "&6" : "&e";

			watchMap.get(attacker).forEach(staff ->
					send(staff, PREFIX + attacker.getName() + " --> " + victimName + " " + color + nf.format(distnace)));
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
