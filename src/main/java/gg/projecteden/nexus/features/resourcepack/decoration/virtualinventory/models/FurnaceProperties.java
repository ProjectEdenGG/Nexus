package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models;

import gg.projecteden.nexus.Nexus;
import lombok.Getter;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class FurnaceProperties extends Properties implements Keyed {
	@Getter
	private double cookMultiplier;
	@Getter
	private double fuelMultiplier;

	public static final FurnaceProperties FURNACE = build("furnace", 1.0, 1.0);
	public static final FurnaceProperties BLAST_FURNACE = build("blast_furnace", 2.0, 1.0);
	public static final FurnaceProperties SMOKER = build("smoker", 2.0, 1.0);

	private static FurnaceProperties build(String key, double cookX, double fuelX) {
		return new FurnaceProperties("properties_" + key).cookMultiplier(cookX).fuelMultiplier(fuelX);
	}

	private static FurnaceProperties getProperty(String key) {
		for (NamespacedKey namespacedKey : KEY_MAP.keySet()) {
			if (namespacedKey.getKey().equalsIgnoreCase(key)) {
				return (FurnaceProperties) KEY_MAP.get(namespacedKey);
			}
		}
		return null;
	}

	public FurnaceProperties(String key) {
		super(new NamespacedKey(Nexus.getInstance(), key.toLowerCase()));
		this.cookMultiplier = 1.0;
		this.fuelMultiplier = 1.0;
	}

	public FurnaceProperties cookMultiplier(double amount) {
		this.cookMultiplier = amount;
		return this;
	}

	public FurnaceProperties fuelMultiplier(double amount) {
		this.fuelMultiplier = amount;
		return this;
	}

	@Override
	public @NotNull NamespacedKey getKey() {
		return this.key;
	}

	@Override
	public String toString() {
		return "Properties{" +
			"key=" + key +
			", cookX=" + cookMultiplier +
			", fuelX=" + fuelMultiplier +
			'}';
	}
}
