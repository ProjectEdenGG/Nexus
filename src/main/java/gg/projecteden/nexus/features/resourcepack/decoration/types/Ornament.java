package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.CeilingThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;

public class Ornament extends CeilingThing {

	public Ornament(OrnamentType type, boolean giant) {
		super(false, type.getName(giant), type.getModel(giant));
	}

	@AllArgsConstructor
	public enum OrnamentType {
		BAUBLE(ItemModelType.ORNAMENT_BAUBLE, ItemModelType.ORNAMENT_GIANT_BAUBLE),
		BAUBLE_ACCENT(ItemModelType.ORNAMENT_BAUBLE_ACCENT, ItemModelType.ORNAMENT_GIANT_BAUBLE_ACCENT),
		CANDY_CANE(ItemModelType.ORNAMENT_CANDY_CANE, ItemModelType.ORNAMENT_GIANT_CANDY_CANE),
		CONE(ItemModelType.ORNAMENT_CONE, ItemModelType.ORNAMENT_GIANT_CONE),
		GINGERBREAD(ItemModelType.ORNAMENT_GINGERBREAD, ItemModelType.ORNAMENT_GIANT_GINGERBREAD),
		SNOWFLAKE(ItemModelType.ORNAMENT_SNOWFLAKE, ItemModelType.ORNAMENT_GIANT_SNOWFLAKE),
		SNOWMAN(ItemModelType.ORNAMENT_SNOWMAN, ItemModelType.ORNAMENT_GIANT_SNOWMAN),
		STAR(ItemModelType.ORNAMENT_STAR, ItemModelType.ORNAMENT_GIANT_STAR),
		;

		private final ItemModelType smallModel;
		private final ItemModelType giantModel;

		public String getName(boolean isGiant) {
			String name = StringUtils.camelCase(this) + " Ornament";
			if (isGiant)
				return "Giant " + name;

			return name;
		}

		public ItemModelType getModel(boolean isGiant) {
			if (isGiant)
				return giantModel;

			return smallModel;
		}
	}
}
