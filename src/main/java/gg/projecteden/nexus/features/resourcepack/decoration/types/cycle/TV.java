package gg.projecteden.nexus.features.resourcepack.decoration.types.cycle;

import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.resourcepack.decoration.common.HitboxEnums.HitboxUnique;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Cyclable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class TV extends WallThing implements Cyclable {
	private final ChannelType channelType;

	public TV(String name, ChannelType channelType) {
		super(true, name, channelType.getItemModelType(), HitboxUnique.TV);
		this.channelType = channelType;
	}

	@Getter
	@AllArgsConstructor
	public enum ChannelType implements IterableEnum {
		OFF(ItemModelType.FLAT_SCREEN_TV_OFF),
		TEST_PATTERN(ItemModelType.FLAT_SCREEN_TV_TEST_PATTERN),
		STATIC(ItemModelType.FLAT_SCREEN_TV_STATIC),
		//
		BACK_TO_THE_FUTURE(ItemModelType.FLAT_SCREEN_TV_BACK_TO_THE_FUTURE),
		BEE_MOVIE(ItemModelType.FLAT_SCREEN_TV_BEE_MOVIE),
		BREAKING_BAD(ItemModelType.FLAT_SCREEN_TV_BREAKING_BAD),
		DEXTER(ItemModelType.FLAT_SCREEN_TV_DEXTER),
		ET(ItemModelType.FLAT_SCREEN_TV_ET),
		FOREST_GUMP(ItemModelType.FLAT_SCREEN_TV_FOREST_GUMP),
		HOUSE_MD(ItemModelType.FLAT_SCREEN_TV_HOUSE_MD),
		HUB(ItemModelType.FLAT_SCREEN_TV_HUB),
		INTERSTELLAR(ItemModelType.FLAT_SCREEN_TV_INTERSTELLAR),
		INVINCIBLE(ItemModelType.FLAT_SCREEN_TV_INVINCIBLE),
		JOKER(ItemModelType.FLAT_SCREEN_TV_JOKER),
		LION_KING(ItemModelType.FLAT_SCREEN_TV_LION_KING),
		LORD_OF_THE_RINGS(ItemModelType.FLAT_SCREEN_TV_LORD_OF_THE_RINGS),
		MATRIX(ItemModelType.FLAT_SCREEN_TV_MATRIX),
		ROKU(ItemModelType.FLAT_SCREEN_TV_ROKU),
		SHINING(ItemModelType.FLAT_SCREEN_TV_SHINING),
		SMILING_FRIENDS(ItemModelType.FLAT_SCREEN_TV_SMILING_FRIENDS),
		STAR_WARS(ItemModelType.FLAT_SCREEN_TV_STAR_WARS),
		TITANIC(ItemModelType.FLAT_SCREEN_TV_TITANIC),
		TRUMAN_SHOW(ItemModelType.FLAT_SCREEN_TV_TRUMAN_SHOW),
		WALL_E(ItemModelType.FLAT_SCREEN_TV_WALL_E),
		WIZARD_OF_OZ(ItemModelType.FLAT_SCREEN_TV_WIZARD_OF_OZ),
		;

		private final ItemModelType itemModelType;
	}

	@Override
	public ItemModelType getBaseItemModel() {
		return ChannelType.OFF.getItemModelType();
	}

	@Override
	public ItemModelType getNextItemModel(Player player, ItemStack tool) {
		return ((ChannelType) channelType.nextWithLoop()).getItemModelType();
	}

	@Override
	public ItemModelType getPreviousItemModel(Player player, ItemStack tool) {
		return ((ChannelType) channelType.previousWithLoop()).getItemModelType();
	}

	// TODO
	@Override
	public void playSound(@NonNull Block origin) {
		Cyclable.super.playSound(origin);
	}
}
