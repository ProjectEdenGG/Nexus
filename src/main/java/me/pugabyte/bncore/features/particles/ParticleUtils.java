package me.pugabyte.bncore.features.particles;

import org.bukkit.Location;
import org.bukkit.Particle;

import java.awt.*;

public class ParticleUtils {

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

	public static void display(Particle particle, Location location, int count, double x, double y, double z, double speed) {
		location.getWorld().spawnParticle(particle, location, count, x, y, z, speed);
	}

//	Only works with async
//	public static boolean isActive(int taskId) {
//		return BNCore.getInstance().getServer().getScheduler().isCurrentlyRunning(taskId);
//	}

}
