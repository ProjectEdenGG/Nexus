package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import org.bukkit.Color;
import org.bukkit.Material;

public class Dyeable extends Decoration {

	public Dyeable(String name, int modelData, Color defaultColor) {
		super(name, modelData, Material.LEATHER_HORSE_ARMOR);
		this.defaultColor = defaultColor;
	}

	public Dyeable(String name, int modelData) {
		super(name, modelData, Material.LEATHER_HORSE_ARMOR);
		this.defaultColor = getDefaultColor();
	}
}
