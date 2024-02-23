package gg.projecteden.nexus.features.workbenches.dyestation;

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

	@Getter
	@AllArgsConstructor
	enum ChoiceType {
		DYE(CustomMaterial.DYE_STATION_DYE, CustomMaterial.DYE_STATION_BUTTON_DYE),
		STAIN(CustomMaterial.DYE_STATION_STAIN, CustomMaterial.DYE_STATION_BUTTON_STAIN),
		;

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

		public ItemStack getItem(String name) {
			return getButton().getItem(ChoiceType.DYE, name);
		}

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
			for (StainChoice stain : values()) {
				if (stain.getColor().equals(color))
					return stain;
			}
			return null;
		}

		public ItemStack getItem(String name) {
			return getButton().getItem(ChoiceType.STAIN, name);
		}

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
