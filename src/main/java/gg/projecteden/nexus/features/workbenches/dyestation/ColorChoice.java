package gg.projecteden.nexus.features.workbenches.dyestation;

import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface ColorChoice {

	void apply(ItemStack item);

	Color getColor();

	ItemStack getItem(String itemName);

	@Getter
	@AllArgsConstructor
	enum ChoiceType {
		DYE(ColorType.hexToBukkit("#FF5555"), IDyeMenu.SLOT_CHEAT_DYE, CustomMaterial.DYE_STATION_BOTTLE_DYE, CustomMaterial.DYE_STATION_BUTTON_DYE),
		STAIN(ColorType.hexToBukkit("#F4C57A"), IDyeMenu.SLOT_CHEAT_STAIN, CustomMaterial.DYE_STATION_BOTTLE_STAIN, CustomMaterial.DYE_STATION_BUTTON_STAIN),
		MINERAL(ColorType.hexToBukkit("#6a6a6a"), IDyeMenu.SLOT_CHEAT_MINERAL, CustomMaterial.DYE_STATION_BOTTLE_MINERAL, CustomMaterial.DYE_STATION_BUTTON_MINERAL),
		;

		final Color defaultColor;
		private final SlotPos slotPos;
		private final CustomMaterial bottleMaterial;
		private final CustomMaterial buttonMaterial;

		public ItemBuilder getItem() {
			return new ItemBuilder(bottleMaterial);
		}

		public ItemBuilder getButton() {
			return new ItemBuilder(buttonMaterial);
		}

	}

	@Getter
	enum DyeChoice implements ColorChoice {
		RED("#FF0000", List.of("#FF756B", "#FF5E52", "#FF4233", "#FF0000", "#C70F00", "#9C0B00", "#6E0800")),
		ORANGE("#FF7F00", List.of("#FFBF6B", "#FFB552", "#FFA833", "#FF7F00", "#C77200", "#9C5900", "#6E3F00")),
		YELLOW("#FEFF00", List.of("#F5FF6B", "#F2FF52", "#EFFF33", "#FEFF00", "#B7C700", "#909C00", "#656E00")),
		PINK("#FF54BD", List.of("#FF9CD3", "#FF8BCA", "#FF76C0", "#FF54BD", "#FF2E9F", "#DB3690", "#B33F7E")),
		WHITE("#FFFFFF", List.of("#FFFFFF", "#C7C7C7", "#8F8F8F", "#6E6E6E", "#525252", "#333333", "#222222")),
		GREEN("#7FFF00", List.of("#ABFF6B", "#9CFF52", "#89FF33", "#7FFF00", "#54C700", "#429C00", "#2E6E00")),
		PURPLE("#A900FF", List.of("#D76BFF", "#D152FF", "#CA33FF", "#A900FF", "#9300C7", "#73009C", "#51006E")),
		BLUE("#0040FF", List.of("#6B86FF", "#5271FF", "#3357FF", "#0040FF", "#0023C7", "#001C9C", "#001D73")),
		LIGHT_BLUE("#00BEFF", List.of("#6BD0FF", "#52C7FF", "#33BCFF", "#00BEFF", "#0086C7", "#00699C", "#004A6E")),
		;

		private final ColoredButton button;
		private final List<ColoredButton> choices = new ArrayList<>();

		DyeChoice(String hex, List<String> hexes) {
			this.button = new ColoredButton(hex);
			for (String _hex : hexes)
				choices.add(new ColoredButton(_hex));
		}

		@Override
		public ItemStack getItem(String name) {
			return getButton().getItem(ChoiceType.DYE, name);
		}

		@Override
		public Color getColor() {
			return getButton().getColor();
		}

		@Override
		public void apply(ItemStack item) {
			Colored.of(this.getColor()).apply(item);
		}
	}

	@Getter
	enum StainChoice implements ColorChoice {
		BIRCH("#FEE496"),
		OAK("#F4C57A"),
		JUNGLE("#EFA777"),
		SPRUCE("#AD7B49"),
		DARK_OAK("#664421"),
		CRIMSON("#924967"),
		ACACIA("#F18648"),
		WARPED("#2FA195"),
		MANGROVE("#7F3535"),
		CHERRY("#FFBBBB"),
		BAMBOO("#F3DF5F"),
		;

		private final ColoredButton button;

		StainChoice(String hex) {
			this.button = new ColoredButton(hex);
		}

		public static StainChoice of(Color color) {
			for (StainChoice choice : values()) {
				if (choice.getColor().equals(color))
					return choice;
			}
			return null;
		}

		@Override
		public ItemStack getItem(String name) {
			return getButton().getItem(ChoiceType.STAIN, name);
		}

		@Override
		public Color getColor() {
			return getButton().getColor();
		}

		@Override
		public void apply(ItemStack item) {
			Colored.of(this.getColor()).apply(item);
		}
	}

	@Getter
	enum MineralChoice implements ColorChoice {
		NETHERITE("#484548"),
		STEEL("#6A6A6A"),
		IRON("#E0E0E0"),
		SILVER("#DEDACD"),
		ELECTRUM("#E7C697"),
		BRASS("#E1C16E"),
		GOLD("#FFD83E"),
		COPPER("#D37A5A"),
		BRONZE("#8E5A49"),
		//
		AMETHYST("#7A5BB5"),
		EMERALD("#17C544"),
		REDSTONE("#E21F08"),
		COAL("#1F1E1E"),
		LAPIS("#1F4F9A"),
		DIAMOND("#4AE9E2"),
		;

		private final ColoredButton button;

		MineralChoice(String hex) {
			this.button = new ColoredButton(hex);
		}

		public static MineralChoice of(Color color) {
			for (MineralChoice choice : values()) {
				if (choice.getColor().equals(color))
					return choice;
			}
			return null;
		}

		@Override
		public ItemStack getItem(String name) {
			return getButton().getItem(ChoiceType.MINERAL, name);
		}

		@Override
		public Color getColor() {
			return getButton().getColor();
		}

		@Override
		public void apply(ItemStack item) {
			Colored.of(this.getColor()).apply(item);
		}
	}

	@Getter
	class ColoredButton {
		private final Color color;

		public ColoredButton(String hex) {
			this.color = ColorType.hexToBukkit(hex);
		}

		public ItemStack getItem(@NonNull ColorChoice.ChoiceType dyeType, String name) {
			ItemBuilder dye = dyeType.getButton();

			if (name != null)
				dye.name(name);

			return dye.dyeColor(color).build();
		}
	}
}
