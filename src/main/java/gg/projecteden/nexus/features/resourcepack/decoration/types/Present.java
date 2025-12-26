package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Present extends DyeableFloorThing {
	private final PresentType presentType;

	public Present(PresentType type) {
		super(false, type.getName(), type.getModel(), ColorableType.DYE, "FFFFFF", type.getShape().getHitbox());
		this.presentType = type;
	}

	@Getter
	@AllArgsConstructor
	public enum PresentType {
		SHORT_RED(PresentShape.SHORT, PresentRibbonColor.RED, null),
		SHORT_DOUBLE_RED(PresentShape.SHORT_DOUBLE, PresentRibbonColor.RED, ItemModelType.PRESENT_SHORT_DOUBLE_1),
		MINI_RED(PresentShape.MINI, PresentRibbonColor.RED, ItemModelType.PRESENT_MINI_1),
		BLOCK_RED(PresentShape.BLOCK, PresentRibbonColor.RED, ItemModelType.PRESENT_BLOCK_1),
		TALL_RED(PresentShape.TALL, PresentRibbonColor.RED, ItemModelType.PRESENT_TALL_1),
		;

		private final PresentShape shape;
		private final PresentRibbonColor ribbonColor;
		private final ItemModelType model;

		public String getName() {
			return StringUtils.camelCase(shape) + " Present - " + StringUtils.camelCase(ribbonColor);
		}
	}

	@Getter
	@AllArgsConstructor
	private enum PresentShape {
		SHORT(HitboxSingle._1x1_HEAD),
		SHORT_DOUBLE(HitboxSingle._1x1_HEAD),
		MINI(HitboxSingle._1x1_HEAD),
		BLOCK(HitboxSingle._1x1_BARRIER),
		TALL(HitboxFloor._1x2V),
		;

		private final CustomHitbox hitbox;
	}

	private enum PresentRibbonColor {
		RED,
		;
	}
}
