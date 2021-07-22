package gg.projecteden.nexus.features.particles;

import gg.projecteden.nexus.Nexus;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.awt.*;


public class ParticleUtils {

	public static double incHue(double hue) {
		if (hue >= 20.0) hue = 0.0;
		hue += 0.1;
		return hue;
	}

	public static int[] incRainbow(double hue) {
		int argb = Color.HSBtoRGB((float) (hue / 20.0F), 1.0F, 1.0F);
		int r = argb >> 16 & 255;
		int g = argb >> 8 & 255;
		int b = argb & 255;

		int[] rgb = new int[3];
		rgb[0] = r;
		rgb[1] = g;
		rgb[2] = b;
		return rgb;
	}

	public static void display(Particle particle, Location location, int count, double x, double y, double z, double speed) {
		if (location.getWorld() != null)
			location.getWorld().spawnParticle(particle, location, count, x, y, z, speed);
	}

	public static void display(Particle particle, Location location, int count, double x, double y, double z, double speed, Particle.DustOptions dustOptions) {
		if (!particle.equals(Particle.REDSTONE) && dustOptions != null)
			Nexus.warn("Tried to use DustOptions with " + particle);
		else if (location.getWorld() != null)
			location.getWorld().spawnParticle(particle, location, count, x, y, z, speed, dustOptions);
	}

	public static Particle.DustOptions newDustOption(Particle particle, int[] rgb) {
		return newDustOption(particle, rgb[0], rgb[1], rgb[2]);
	}

	public static Particle.DustOptions newDustOption(Particle particle, int red, int green, int blue) {
		if (particle.equals(Particle.REDSTONE)) {
			org.bukkit.Color color = org.bukkit.Color.fromRGB(red, green, blue);
			return new Particle.DustOptions(color, 1.0F);
		}

		return null;
	}

//	Only works with async
//	public static boolean isActive(int taskId) {
//		return Nexus.getInstance().getServer().getScheduler().isCurrentlyRunning(taskId);
//	}

}
