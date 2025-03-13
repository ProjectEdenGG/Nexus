package gg.projecteden.nexus.features.titan.models;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SerializationUtils.NBT;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomCreativeItem {

	String category;
	String item;

	public CustomCreativeItem(CustomBlock customBlock) {
		item = NBT.serializeItemStack(customBlock.get().getItemStack());
		category = "Custom Blocks";
	}

	public CustomCreativeItem(DecorationType decorationType) {
		category = "Decorations: " + StringUtils.camelCase(decorationType.getTypeConfig().theme());
		item = NBT.serializeItemStack(decorationType.getConfig().getItem());
	}

	public CustomCreativeItem(ItemBuilder item, String category) {
		this.category = category;
		this.item = NBT.serializeItemStack(item.build());
	}

}
