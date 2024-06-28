package gg.projecteden.nexus.features.forcefield;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.forcefield.ForceFieldUser;
import gg.projecteden.nexus.models.forcefield.ForceFieldUserService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ForceField extends Feature {
	static ForceFieldUserService userService = new ForceFieldUserService();

	@Override
	public void onStart() {
		getUsers().forEach(ForceFieldUser::refreshEffect);

		Tasks.repeat(2, TickTime.TICK.x(2), () -> getUsers().forEach(ForceField::moveNearby));
	}

	@Override
	public void onStop() {
		for (ForceFieldUser user : userService.getAll()) {
			if (user.getParticleTaskId() == -1)
				continue;

			Tasks.cancel(user.getParticleTaskId());
			user.setParticleTaskId(-1);
			userService.save(user);
		}
	}

	private static List<ForceFieldUser> getUsers() {
		return userService.getOnline().stream()
			.filter(ForceFieldUser::isEnabled)
			.filter(forceFieldUser -> WorldGroup.of(forceFieldUser) != WorldGroup.MINIGAMES)
			.collect(Collectors.toList());
	}

	private static void moveNearby(ForceFieldUser user) {
		Player player = user.getOnlinePlayer();
		UUID uuid = user.getUuid();
		double radius = user.getRadius();

		boolean movePlayers = user.isMovePlayers();
		boolean moveProjectiles = user.isMoveProjectiles();

		LinkedHashMap<Entity, Long> entities = EntityUtils.getNearbyEntities(player.getLocation(), radius);
		for (Entity entity : entities.keySet()) {
			if (entity.getUniqueId().equals(uuid))
				continue;

			if (!(entity instanceof Item) && !(entity instanceof Projectile) && !(entity instanceof Player))
				continue;

			if (CitizensUtils.isNPC(entity)) {
				continue;
			}

			if (entity instanceof Player) {
				if (!movePlayers)
					continue;

				if (user.getIgnored().contains(entity.getUniqueId()))
					continue;

			} else if (entity instanceof Projectile && !moveProjectiles)
				continue;

			push(player, entity);
		}
	}

	private static void push(Player pusher, Entity pushee) {
		Vector launchDir = EntityUtils.getForcefieldVelocity(pushee, pusher.getLocation());

		try {
			pushee.setVelocity(launchDir);
		} catch (IllegalArgumentException ignore) {}
	}
}
