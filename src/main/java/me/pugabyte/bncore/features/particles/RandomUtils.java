package me.pugabyte.bncore.features.particles;

import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Random;

public class RandomUtils {

	public static final Random random = new Random(System.nanoTime());

	public static Vector getRandomVector() {
		double x, y, z;
		x = random.nextDouble() * 2 - 1;
		y = random.nextDouble() * 2 - 1;
		z = random.nextDouble() * 2 - 1;

		return new Vector(x, y, z).normalize();
	}

	public static Vector getRandomCircleVector() {
		double rnd, x, z;
		rnd = random.nextDouble() * 2 * Math.PI;
		x = Math.cos(rnd);
		z = Math.sin(rnd);

		return new Vector(x, 0, z);
	}

	public static Material getRandomMaterial(Material[] materials) {
		return materials[random.nextInt(materials.length)];
	}

	public static double getRandomAngle() {
		return random.nextDouble() * 2 * Math.PI;
	}
}
