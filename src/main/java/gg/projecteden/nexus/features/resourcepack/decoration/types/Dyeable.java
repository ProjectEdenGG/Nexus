package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.Basic;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Color;

public class Dyeable extends DecorationConfig implements Colorable {
	private final ColorableType colorableType;
	private String hexOverride = null;

	public Dyeable(String name, CustomMaterial material, ColorableType colorableType) {
		this(name, material, colorableType, Basic.NONE);
	}

	public Dyeable(String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox) {
		this(name, material, colorableType, null, hitbox);
	}

	public Dyeable(String name, CustomMaterial material, ColorableType colorableType, String hexOverride) {
		this(name, material, colorableType, hexOverride, Basic.NONE);
	}

	public Dyeable(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox) {
		super(name, material, hitbox);
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
