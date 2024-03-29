package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Color;

public class Dyeable extends DecorationConfig implements Colorable {
	private final ColorableType colorableType;
	private String hexOverride = null;

	public Dyeable(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType) {
		this(multiblock, name, material, colorableType, HitboxSingle.NONE);
	}

	public Dyeable(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, CustomHitbox hitbox) {
		this(multiblock, name, material, colorableType, null, hitbox);
	}

	public Dyeable(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, String hexOverride) {
		this(multiblock, name, material, colorableType, hexOverride, HitboxSingle.NONE);
	}

	public Dyeable(boolean multiblock, String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CustomHitbox hitbox) {
		super(multiblock, name, material, hitbox);
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
