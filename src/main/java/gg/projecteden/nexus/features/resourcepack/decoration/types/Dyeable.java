package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import org.bukkit.Color;

public class Dyeable extends DecorationConfig implements Colorable {
	private final ColorableType colorableType;
	private String hexOverride = null;

	public Dyeable(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType) {
		this(multiblock, name, itemModelType, colorableType, HitboxSingle.NONE);
	}

	public Dyeable(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, CustomHitbox hitbox) {
		this(multiblock, name, itemModelType, colorableType, null, hitbox);
	}

	public Dyeable(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, String hexOverride) {
		this(multiblock, name, itemModelType, colorableType, hexOverride, HitboxSingle.NONE);
	}

	public Dyeable(boolean multiblock, String name, ItemModelType itemModelType, ColorableType colorableType, String hexOverride, CustomHitbox hitbox) {
		super(multiblock, name, itemModelType, hitbox);
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
