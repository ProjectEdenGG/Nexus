package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Color;

import java.util.List;

public class Dyeable extends DecorationConfig implements Colorable {
	private final ColorableType colorableType;
	private String hexOverride = null;

	public Dyeable(String name, CustomMaterial material, ColorableType colorableType, String hexOverride) {
		super(name, material);
		this.colorableType = colorableType;
		this.hexOverride = hexOverride;
	}

	public Dyeable(String name, CustomMaterial material, ColorableType colorableType) {
		super(name, material);
		this.colorableType = colorableType;
	}

	public Dyeable(String name, CustomMaterial material, ColorableType colorableType, List<Hitbox> hitboxes) {
		super(name, material, hitboxes);
		this.colorableType = colorableType;
	}

	public Dyeable(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, List<Hitbox> hitboxes) {
		super(name, material, hitboxes);
		this.colorableType = colorableType;
		this.hexOverride = hexOverride;
	}

	@Override
	public ColorableType getColorableType() {
		return this.colorableType;
	}

	@Override
	public Color getColor() {
		if (hexOverride == null)
			return Colorable.super.getColor();

		return ColorType.hexToBukkit(hexOverride);
	}
}
