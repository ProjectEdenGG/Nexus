package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxSingle;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Cyclable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClownInTheBox extends FloorThing implements Cyclable {
	private final ClownInTheBoxType type;

	public ClownInTheBox(ClownInTheBoxType type) {
		super(false, "Clown-in-the-box", type.getModel(), HitboxSingle._1x1_BARRIER);
		this.type = type;
	}

	@Override
	public ItemModelType getNextItemModel(Player player, ItemStack tool) {
		return ((ClownInTheBoxType) type.nextWithLoop()).getModel();
	}

	@Override
	public ItemModelType getPreviousItemModel(Player player, ItemStack tool) {
		return ((ClownInTheBoxType) type.previousWithLoop()).getModel();
	}

	@Override
	public ItemModelType getBaseItemModel() {
		return ClownInTheBoxType.CLOSED_OFF.getModel();
	}

	@Getter
	@AllArgsConstructor
	public enum ClownInTheBoxType implements IterableEnum {
		CLOSED_OFF(ItemModelType.CLOWN_IN_THE_BOX_CLOSED_OFF),
		CLOSED_ON(ItemModelType.CLOWN_IN_THE_BOX_CLOSED_ON),
		OPEN_ON(ItemModelType.CLOWN_IN_THE_BOX_ON),
		OPEN_OFF(ItemModelType.CLOWN_IN_THE_BOX_OFF),
		;

		private final ItemModelType model;
	}
}
