package me.pugabyte.nexus.utils;

import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class RandomUtils extends eden.utils.RandomUtils {
	public static Vector randomVector() {
		double x = random.nextDouble() * 2 - 1;
		double y = random.nextDouble() * 2 - 1;
		double z = random.nextDouble() * 2 - 1;

		return new Vector(x, y, z).normalize();
	}

	public static Vector randomCircleVector() {
		double rnd = random.nextDouble() * 2 * Math.PI;
		double x = Math.cos(rnd);
		double z = Math.sin(rnd);

		return new Vector(x, 0, z);
	}

	public static Material randomMaterial() {
		return randomMaterial(Material.values());
	}

	public static Material randomMaterial(MaterialTag tag) {
		return randomMaterial(tag.getValues().toArray(Material[]::new));
	}

	public static Material randomMaterial(Material[] materials) {
		return materials[random.nextInt(materials.length)];
	}

	public static int randomInt(int min, int max) throws InvalidInputException {
		try {
			return eden.utils.RandomUtils.randomInt(min, max);
		} catch (IllegalArgumentException exc) {
			throw new InvalidInputException(exc.getMessage());
		}
	}

	public static double randomDouble(double min, double max) throws InvalidInputException {
		try {
			return eden.utils.RandomUtils.randomDouble(min, max);
		} catch (IllegalArgumentException exc) {
			throw new InvalidInputException(exc.getMessage());
		}
	}
}
