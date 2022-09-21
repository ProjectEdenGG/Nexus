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

	public Cabinet(String name, CustomMaterial material, String hexOverride, CabinetType type) {
		super(name, material, ColorableType.STAIN, hexOverride);
		this.type = type;
		this.rotationType = RotationType.DEGREE_90;
		this.hitboxes = Hitbox.single(Material.BARRIER);
	}

	public Cabinet(String name, CustomMaterial material, CabinetType type) {
		super(name, material, ColorableType.STAIN);
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
