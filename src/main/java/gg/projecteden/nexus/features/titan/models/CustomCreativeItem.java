package gg.projecteden.nexus.features.titan.models;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomCreativeItem {

	String material;
	String displayName;
	String model;
	String hex;
	String category;

	public CustomCreativeItem(CustomBlock customBlock) {
		material = ICustomBlock.itemMaterial.name().toLowerCase();
		displayName = customBlock.get().getItemName();
		model = customBlock.get().getModel();
		category = "Custom Blocks";
	}

	public CustomCreativeItem(DecorationType decorationType) {
		material = decorationType.getConfig().getMaterial().name().toLowerCase();
		displayName = decorationType.getConfig().getName();
		model = decorationType.getConfig().getModel();
		category = "Decorations: " + StringUtils.camelCase(decorationType.getTypeConfig().theme());
		if (decorationType.getConfig() instanceof Dyeable dyeable)
			hex = dyeable.getColor().asHexString();
	}

	public CustomCreativeItem(ItemBuilder item, String category) {
		material = item.material().name().toLowerCase();
		displayName = item.name();
		model = item.model();
		this.category = category;
	}

}
