package me.pugabyte.bncore.features.particles;

import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParticleUtils {
	public static Map<Long, Map<UUID, Integer>> activeParticles = new HashMap<>();

	public static void addToMap(long millis, Player player, int taskId) {
		ParticleUtils.activeParticles.put(millis, new HashMap<UUID, Integer>() {{
			put(player.getUniqueId(), taskId);
		}});
	}

	public static void cancelParticle(long millis, Player player) {
		Tasks.cancel(activeParticles.get(millis).get(player.getUniqueId()));
		activeParticles.remove(millis);

	}
}
