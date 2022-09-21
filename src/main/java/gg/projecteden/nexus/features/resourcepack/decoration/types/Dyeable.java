package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Color;

import java.util.List;

public class Dyeable extends DecorationConfig implements Colorable {
	private final Type type;
	private String hexOverride = null;

	public Dyeable(String name, CustomMaterial material, Type type, String hexOverride) {
		super(name, material);
		this.type = type;
		this.hexOverride = hexOverride;
	}

	public Dyeable(String name, CustomMaterial material, Type type) {
		super(name, material);
		this.type = type;
	}

	public Dyeable(String name, CustomMaterial material, Type type, List<Hitbox> hitboxes) {
		super(name, material, hitboxes);
		this.type = type;
	}

	@Override
	public Type getType() {
		return this.type;
	}

	@Override
	public Color getColor() {
		if (hexOverride == null)
			return Colorable.super.getColor();

		return ColorType.hexToBukkit(hexOverride);
	}
}
