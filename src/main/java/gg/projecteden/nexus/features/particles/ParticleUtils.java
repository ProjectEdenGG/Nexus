package gg.projecteden.nexus.features.particles;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.Nexus;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

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

	@Data
	public static class ParticleColor {
		private double hue;
		private int red;
		private int green;
		private int blue;

		public ParticleColor(org.bukkit.Color color) {
			this.red = color.getRed();
			this.green = color.getGreen();
			this.blue = color.getBlue();
		}

		public void incrementRainbow() {
			if (hue >= 20.0) hue = 0.0;
			hue += 0.1;

			int argb = Color.HSBtoRGB((float) (hue / 20.0F), 1.0F, 1.0F);
			red = argb >> 16 & 255;
			green = argb >> 8 & 255;
			blue = argb & 255;
		}
	}

	public static void display(Particle particle, Location location, int count, double x, double y, double z, double speed) {
		if (location.getWorld() != null)
			location.getWorld().spawnParticle(particle, location, count, x, y, z, speed);
	}

	public static void display(Particle particle, Location location, int count, double x, double y, double z, double speed, Particle.DustOptions dustOptions) {
		if (!particle.equals(Particle.DUST) && dustOptions != null)
			Nexus.warn("Tried to use DustOptions with " + particle);
		else if (location.getWorld() != null)
			location.getWorld().spawnParticle(particle, location, count, x, y, z, speed, dustOptions);
	}

	public static void display(Player player, Particle particle, Location location, int count, double x, double y, double z, double speed, Particle.DustOptions dustOptions) {
		if (!particle.equals(Particle.DUST) && dustOptions != null)
			Nexus.warn("Tried to use DustOptions with " + particle);
		if (player == null)
			display(particle, location, count, x, y, z, speed, dustOptions);
		else if (location.getWorld() != null)
			new ParticleBuilder(particle)
				.receivers(player)
				.location(location)
				.count(count)
				.extra(speed)
				.color(dustOptions.getColor())
				.offset(x, y, z)
				.spawn();
	}

	public static Particle.DustOptions newDustOption(Particle particle, int[] rgb) {
		return newDustOption(particle, rgb[0], rgb[1], rgb[2]);
	}

	public static Particle.DustOptions newDustOption(Particle particle, int red, int green, int blue) {
		return newDustOption(particle, red, green, blue, 1);
	}

	public static Particle.DustOptions newDustOption(Particle particle, int red, int green, int blue, float dustSize) {
		if (particle.equals(Particle.DUST)) {
			org.bukkit.Color color = org.bukkit.Color.fromRGB(red, green, blue);
			return new Particle.DustOptions(color, dustSize);
		}

		return null;
	}

	public static Particle.DustOptions newDustOption(Particle particle, ParticleColor particleColor) {
		if (particle.equals(Particle.DUST)) {
			org.bukkit.Color color = org.bukkit.Color.fromRGB(particleColor.getRed(), particleColor.getGreen(), particleColor.getBlue());
			return new Particle.DustOptions(color, 1.0F);
		}

		return null;
	}

//	Only works with async
//	public static boolean isActive(int taskId) {
//		return Nexus.getInstance().getServer().getScheduler().isCurrentlyRunning(taskId);
//	}

}
