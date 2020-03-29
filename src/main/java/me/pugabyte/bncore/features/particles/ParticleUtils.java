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

	public static double[] incRainbow(double red, double green, double blue, double rate) {
		double r = (red * 255.0);
		double g = (green * 255.0);
		double b = (blue * 255.0);

		if (r > 0 && b == 0) {
			r -= rate;
			if (r < 0)
				r = 0;
			g += rate;
			if (g > 255)
				g = 255;
		}

		if (g > 0 && r == 0) {
			g -= rate;
			if (g < 0)
				g = 0;
			b += rate;
			if (b > 255)
				b = 255;
		}

		if (b > 0 && g == 0) {
			b -= rate;
			if (b < 0)
				b = 0;
			r += rate;
			if (r > 255)
				r = 255;
		}

		double[] rgb = new double[3];

		rgb[0] = r / 255;
		rgb[1] = g / 255;
		rgb[2] = b / 255;

		return rgb;
	}
}
