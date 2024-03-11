package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxWall;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Toggleable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class Curtain extends DyeableWallThing implements Toggleable {
	@Getter
	private final CurtainType curtainType;

	public Curtain(String name, CurtainType curtainType) {
		super(true, name, curtainType.getMaterial(), ColorableType.DYE, curtainType.getHitbox());
		this.curtainType = curtainType;
	}

	@AllArgsConstructor
	public enum CurtainType {
		_1x2_OPEN(true, CustomMaterial.CURTAINS_1x2_OPENED, CustomMaterial.CURTAINS_1x2_CLOSED, CustomMaterial.CURTAINS_1x2_OPENED, HitboxWall._1x2V_LIGHT),
		_2x2_OPEN(true, CustomMaterial.CURTAINS_2x2_OPENED, CustomMaterial.CURTAINS_2x2_CLOSED, CustomMaterial.CURTAINS_2x2_OPENED, HitboxWall._2x2_LIGHT),
		_2x3H_OPEN(true, CustomMaterial.CURTAINS_2x3H_OPENED, CustomMaterial.CURTAINS_2x3H_CLOSED, CustomMaterial.CURTAINS_2x3H_OPENED, HitboxWall._2x3H_LIGHT),
		_2x3V_OPEN(true, CustomMaterial.CURTAINS_2x3V_OPENED, CustomMaterial.CURTAINS_2x3V_CLOSED, CustomMaterial.CURTAINS_2x3V_OPENED, HitboxWall._2x3V_LIGHT),
		_3x3_OPEN(true, CustomMaterial.CURTAINS_3x3_OPENED, CustomMaterial.CURTAINS_3x3_CLOSED, CustomMaterial.CURTAINS_3x3_OPENED, HitboxWall._3x3_LIGHT),

		// Internal Only
		_1x2_CLOSED(false, CustomMaterial.CURTAINS_1x2_CLOSED, CustomMaterial.CURTAINS_1x2_OPENED, CustomMaterial.CURTAINS_1x2_OPENED, HitboxWall._1x2V_LIGHT),
		_2x2_CLOSED(false, CustomMaterial.CURTAINS_2x2_CLOSED, CustomMaterial.CURTAINS_2x2_OPENED, CustomMaterial.CURTAINS_2x2_OPENED, HitboxWall._2x2_LIGHT),
		_2x3H_CLOSED(false, CustomMaterial.CURTAINS_2x3H_CLOSED, CustomMaterial.CURTAINS_2x3H_OPENED, CustomMaterial.CURTAINS_2x3H_OPENED, HitboxWall._2x3H_LIGHT),
		_2x3V_CLOSED(false, CustomMaterial.CURTAINS_2x3V_CLOSED, CustomMaterial.CURTAINS_2x3V_OPENED, CustomMaterial.CURTAINS_2x3V_OPENED, HitboxWall._2x3V_LIGHT),
		_3x3_CLOSED(false, CustomMaterial.CURTAINS_3x3_CLOSED, CustomMaterial.CURTAINS_3x3_OPENED, CustomMaterial.CURTAINS_3x3_OPENED, HitboxWall._3x3_LIGHT),

		;

		@Getter
		private final boolean opened;
		@Getter
		private final CustomMaterial material;
		@Getter
		private final CustomMaterial oppositeMaterial;
		@Getter
		private final CustomMaterial droppedMaterial;
		@Getter
		private final CustomHitbox hitbox;
	}

	@Override
	public CustomMaterial getDroppedMaterial() {
		return curtainType.getDroppedMaterial();
	}

	@Override
	public CustomMaterial getToggledMaterial() {
		return curtainType.getOppositeMaterial();
	}

	@Override
	public void playToggledSound(@NotNull Block origin) {
		DecorationUtils.getSoundBuilder(CustomSound.DECOR_CURTAINS_USE).volume(0.5).location(origin).play();
	}
}
