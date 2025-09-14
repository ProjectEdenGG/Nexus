package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.CustomHitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxFloor;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationSnap;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Colorable;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Table extends Dyeable implements Colorable {

	public Table(TableTheme theme, TableShape shape, ItemModelType itemModelType) {
		super(shape.isMultiBlock(), theme.getName() + " - " + shape.getName(), itemModelType, theme.getColorableType(), shape.getHitbox());
		this.rotationSnap = RotationSnap.BOTH;
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();

		if (this.isMultiBlock())
			this.rotationSnap = RotationSnap.DEGREE_90;
	}

	@Getter
	@AllArgsConstructor
	public enum TableShape {
		_1X1("1x1", false, HitboxSingle._1x1_BARRIER),
		_1X2("1x2", true, HitboxFloor._1x2H),
		_1X3("1x3", true, HitboxFloor._1x3H),
		_2X2("2x2", true, HitboxFloor._2x2),
		_2X2_CORNER("2x2 Corner", true, HitboxFloor._2x2SE_CORNER),
		_2X3("2x3", true, HitboxFloor._2x3H),
		_2X3_CORNER("2x3 Corner", true, HitboxFloor._2x3SE_CORNER),
		_2X3_CORNER_FLIPPED("2x3 Corner Flipped", true, HitboxFloor._2x3SW_CORNER),
		_3X3("3x3", true, HitboxFloor._3x3),
		_3X3_CORNER("3x3 Corner", true, HitboxFloor._3x3SE_CORNER),
		;

		private final String name;
		private final boolean multiBlock;
		private final CustomHitbox hitbox;
	}

	@Getter
	@AllArgsConstructor
	public enum TableTheme {
		WOODEN("Wooden Table", ColorableType.STAIN),
		SPOOKY_WOODEN("Spooky Wooden Table", ColorableType.STAIN),
		METALLIC("Metallic Table", ColorableType.MINERAL),
		;

		private final String name;
		private final ColorableType colorableType;
	}

}
