package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxWall;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Toggleable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import org.bukkit.block.Block;

public class ToggleCurtain extends DyeableWallThing implements Toggleable {
	CustomMaterial toggledMaterial;

	public ToggleCurtain(String name, CustomMaterial material, CustomMaterial toggled) {
		super(true, name, material, ColorableType.DYE, HitboxWall._2x3V_LIGHT);
		this.toggledMaterial = toggled;
	}

	@Override
	public CustomMaterial getToggledMaterial() {
		return this.toggledMaterial;
	}

	@Override
	public void playToggledSound(Block origin) {
		DecorationUtils.getSoundBuilder(CustomSound.DECOR_CURTAINS_USE).volume(0.5).location(origin).play();
	}
}
