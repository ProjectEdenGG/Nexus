package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.Getter;
import org.bukkit.Material;

public class Cabinet extends DyeableWallThing {
	@Getter
	private final CabinetType type;

	public Cabinet(String name, CustomMaterial material, ColorableType colorableType, String hexOverride, CabinetType type) {
		super(name, material, colorableType, hexOverride);
		this.type = type;
		this.rotationType = RotationType.DEGREE_90;
		this.hitboxes = Hitbox.single(Material.BARRIER);
	}

	public Cabinet(String name, CustomMaterial material, ColorableType colorableType, CabinetType type) {
		super(name, material, colorableType);
		this.type = type;
		this.rotationType = RotationType.DEGREE_90;
		this.hitboxes = Hitbox.single(Material.BARRIER);
	}


	public enum CabinetType {
		CABINET,
		CORNER,
		;
	}
}
