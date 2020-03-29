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

	// Change in 1.13, with new DustOptions option, also use RBA formula instead!
	public static double[] incRainbow(double red, double green, double blue, double rate) {
		if (red == 0.001) red = 0;
		int r = (int) (red * 255);
		int g = (int) (green * 255);
		int b = (int) (blue * 255);

		// Working
		if (r > 0 && b == 0) {
			r -= rate;
			g += rate;
		}

		if (g > 0 && r == 0) {
			g -= rate;
			b += rate;
		}

		if (b > 0 && g == 0) {
			b -= rate;
			r += rate;
		}

		if (r < 0) r = 0;
		if (r > 255) r = 255;
		if (g < 0) g = 0;
		if (g > 255) g = 255;
		if (b < 0) b = 0;
		if (b > 255) b = 255;

		double[] rgb = new double[3];
		rgb[0] = r;
		rgb[1] = g;
		rgb[2] = b;

		rgb[0] = rgb[0] / 255;
		rgb[1] = rgb[1] / 255;
		rgb[2] = rgb[2] / 255;

		if (rgb[0] <= 0) rgb[0] = 0.001;

		return rgb;
	}
}
