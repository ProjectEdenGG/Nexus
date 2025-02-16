package gg.projecteden.nexus.features.resourcepack.decoration.types.toggle;

import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxWall;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Toggleable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableWallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class Curtain extends DyeableWallThing implements Toggleable {
	@Getter
	private final CurtainType curtainType;

	public Curtain(String name, CurtainType curtainType) {
		super(true, name, curtainType.getItemModelType(), ColorableType.DYE, curtainType.getHitbox());
		this.curtainType = curtainType;
	}

	@AllArgsConstructor
	public enum CurtainType {
		_1x2_OPEN(true, ItemModelType.CURTAINS_1x2_OPENED, ItemModelType.CURTAINS_1x2_CLOSED, ItemModelType.CURTAINS_1x2_OPENED, HitboxWall._1x2V_LIGHT),
		_1x3_OPEN(true, ItemModelType.CURTAINS_1x3_OPENED, ItemModelType.CURTAINS_1x3_CLOSED, ItemModelType.CURTAINS_1x3_OPENED, HitboxWall._1x3V_LIGHT),
		_2x2_OPEN(true, ItemModelType.CURTAINS_2x2_OPENED, ItemModelType.CURTAINS_2x2_CLOSED, ItemModelType.CURTAINS_2x2_OPENED, HitboxWall._2x2_LIGHT),
		_2x3H_OPEN(true, ItemModelType.CURTAINS_2x3H_OPENED, ItemModelType.CURTAINS_2x3H_CLOSED, ItemModelType.CURTAINS_2x3H_OPENED, HitboxWall._2x3H_LIGHT),
		_2x3V_OPEN(true, ItemModelType.CURTAINS_2x3V_OPENED, ItemModelType.CURTAINS_2x3V_CLOSED, ItemModelType.CURTAINS_2x3V_OPENED, HitboxWall._2x3V_LIGHT),
		_3x3_OPEN(true, ItemModelType.CURTAINS_3x3_OPENED, ItemModelType.CURTAINS_3x3_CLOSED, ItemModelType.CURTAINS_3x3_OPENED, HitboxWall._3x3_LIGHT),

		// Internal Only
		_1x2_CLOSED(false, ItemModelType.CURTAINS_1x2_CLOSED, ItemModelType.CURTAINS_1x2_OPENED, ItemModelType.CURTAINS_1x2_OPENED, HitboxWall._1x2V_LIGHT),
		_1x3_CLOSED(false, ItemModelType.CURTAINS_1x3_CLOSED, ItemModelType.CURTAINS_1x3_OPENED, ItemModelType.CURTAINS_1x3_OPENED, HitboxWall._1x3V_LIGHT),
		_2x2_CLOSED(false, ItemModelType.CURTAINS_2x2_CLOSED, ItemModelType.CURTAINS_2x2_OPENED, ItemModelType.CURTAINS_2x2_OPENED, HitboxWall._2x2_LIGHT),
		_2x3H_CLOSED(false, ItemModelType.CURTAINS_2x3H_CLOSED, ItemModelType.CURTAINS_2x3H_OPENED, ItemModelType.CURTAINS_2x3H_OPENED, HitboxWall._2x3H_LIGHT),
		_2x3V_CLOSED(false, ItemModelType.CURTAINS_2x3V_CLOSED, ItemModelType.CURTAINS_2x3V_OPENED, ItemModelType.CURTAINS_2x3V_OPENED, HitboxWall._2x3V_LIGHT),
		_3x3_CLOSED(false, ItemModelType.CURTAINS_3x3_CLOSED, ItemModelType.CURTAINS_3x3_OPENED, ItemModelType.CURTAINS_3x3_OPENED, HitboxWall._3x3_LIGHT),

		;

		@Getter
		private final boolean opened;
		@Getter
		private final ItemModelType itemModelType;
		@Getter
		private final ItemModelType oppositeItemModelType;
		@Getter
		private final ItemModelType droppedItemModelType;
		@Getter
		private final CustomHitbox hitbox;
	}

	@Override
	public ItemModelType getBaseItemModel() {
		return curtainType.getDroppedItemModelType();
	}

	@Override
	public ItemModelType getToggledItemModel() {
		return curtainType.getOppositeItemModelType();
	}

	@Override
	public void playToggledSound(@NotNull Block origin) {
		DecorationUtils.getSoundBuilder(CustomSound.DECOR_CURTAINS_USE).volume(0.5).location(origin).play();
	}
}
