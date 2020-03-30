package me.pugabyte.bncore.features.particles;

import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.entity.Player;

import java.awt.*;
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

	public static double incHue(double hue) {
		if (hue >= 20.0) hue = 0.0;
		hue += 0.1;
		return hue;
	}

	public static double[] incRainbow(double hue) {
		int argb = Color.HSBtoRGB((float) (hue / 20.0F), 1.0F, 1.0F);
		float r = (float) (argb >> 16 & 255) / 255.0F;
		float g = (float) (argb >> 8 & 255) / 255.0F;
		float b = (float) (argb & 255) / 255.0F;
		r = r == 0.0F ? 0.001F : r;

		double[] rgb = new double[3];
		rgb[0] = r;
		rgb[1] = g;
		rgb[2] = b;
		return rgb;
	}
}
