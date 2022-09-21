package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.Getter;
import org.bukkit.Material;

public class Counter extends DyeableFloorThing {
	@Getter
	private final CounterType type;

	public Counter(String name, CustomMaterial material, String hexOverride, CounterType type) {
		super(name, material, ColorableType.STAIN, hexOverride);
		this.type = type;
		this.rotationType = RotationType.DEGREE_90;
		this.hitboxes = Hitbox.single(Material.BARRIER);
	}

	public Counter(String name, CustomMaterial material, CounterType type) {
		super(name, material, ColorableType.STAIN);
		this.type = type;
		this.rotationType = RotationType.DEGREE_90;
		this.hitboxes = Hitbox.single(Material.BARRIER);
	}

	public enum CounterType {
		COUNTER,
		CORNER,
		DRAWER,
		SINK,
		OVEN,
		ISLAND,
		;
	}
}
