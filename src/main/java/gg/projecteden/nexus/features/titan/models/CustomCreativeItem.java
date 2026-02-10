package gg.projecteden.nexus.features.titan.models;

import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SerializationUtils.NBT;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class CustomCreativeItem {

	String category;
	String item;
	transient ItemStack itemStack;

	public CustomCreativeItem(CustomBlock customBlock) {
		this.itemStack = customBlock.get().getItemStack();
		this.item = NBT.serializeItemStack(itemStack);
		this.category = "Custom Blocks";
	}

	public CustomCreativeItem(DecorationType decorationType) {
		this.itemStack = decorationType.getConfig().getItem();
		this.item = NBT.serializeItemStack(itemStack);
		this.category = "Decorations: " + StringUtils.camelCase(decorationType.getTypeConfigTheme());
	}

	public CustomCreativeItem(ItemBuilder item, String category) {
		this.itemStack = item.build();
		this.item = NBT.serializeItemStack(itemStack);
		this.category = category;
	}

	public CustomCreativeItem(ItemStack item, String category) {
		this.itemStack = item;
		this.item = NBT.serializeItemStack(itemStack);
		this.category = category;
	}

}
