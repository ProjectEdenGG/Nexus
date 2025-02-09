package gg.projecteden.nexus.features.events.y2021.halloween21.models;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PumpkinType {
	AXOLOTL(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_AXOLOTL),
	BEE(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_BEE),
	CAT(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_CAT),
	CREEPER(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_CREEPER),
	CREEPY(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_CREEPY),
	FACE(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_FACE),
	FLAT(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_FLAT),
	GHOST(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_GHOST),
	GRINCH(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_GRINCH),
	HAPPY(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_HAPPY),
	HEART(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_HEART),
	HEROBRINE(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_HEROBRINE),
	OBSERVER(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_OBSERVER),
	SANS(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_SANS),
	SILLY(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_SILLY),
	SLIME(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_SLIME),
	STEVE(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_STEVE),
	SUNGLASSES(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_SUNGLASSES),
	TOOTHY(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_TOOTHY),
	UPSIDEDOWN(CustomMaterial.EXCLUSIVE_HAT_PUMPKINS_UPSIDEDOWN),
	;

	private final CustomMaterial material;
}