package gg.projecteden.nexus.features.resourcepack.decoration.types.cycle;

import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Cyclable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class SnowGlobe extends DyeableFloorThing implements Cyclable {
	private final SnowGlobeType snowGlobeType;

	public SnowGlobe(SnowGlobeType snowGlobeType) {
		super(false, "Snow Globe", snowGlobeType.getItemModelType(), ColorableType.DYE);
		this.snowGlobeType = snowGlobeType;
	}

	@Getter
	@AllArgsConstructor
	public enum SnowGlobeType implements IterableEnum {
		TREE(ItemModelType.SNOWGLOBE_TREE),
		PUG(ItemModelType.SNOWGLOBE_PUG),
		SNOWMAN(ItemModelType.SNOWGLOBE_SNOWMAN),
		PRESENT(ItemModelType.SNOWGLOBE_PRESENT),
		;

		private final ItemModelType itemModelType;
	}

	@Override
	public ItemModelType getBaseItemModel() {
		return SnowGlobeType.PRESENT.getItemModelType();
	}

	@Override
	public ItemModelType getNextItemModel(Player player, ItemStack tool) {
		return ((SnowGlobeType) snowGlobeType.nextWithLoop()).getItemModelType();
	}

	@Override
	public ItemModelType getPreviousItemModel(Player player, ItemStack tool) {
		return ((SnowGlobeType) snowGlobeType.previousWithLoop()).getItemModelType();
	}
}
