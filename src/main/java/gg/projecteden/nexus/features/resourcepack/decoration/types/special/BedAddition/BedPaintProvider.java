package gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Rows(3)
@AllArgsConstructor
public class BedPaintProvider extends InventoryProvider {

	@NonNull ItemStack paintbrush;
	Color paintbrushDye;
	Block origin;
	Map<ItemFrame, DecorationConfig> additions;

	public BedPaintProvider(@NonNull ItemStack tool, Block origin, Map<ItemFrame, DecorationConfig> additions) {
		this.paintbrush = tool;
		this.paintbrushDye = new ItemBuilder(paintbrush).dyeColor();
		this.origin = origin;
		this.additions = additions;
	}

	@Override
	public String getTitle() {
		String hex = StringUtils.toHex(paintbrushDye);

		String colorName = hex;
		for (ColorChoice.StainChoice stainChoice : ColorChoice.StainChoice.values()) {
			if (stainChoice.getColor().equals(paintbrushDye)) {
				colorName = StringUtils.camelCase(stainChoice.name());
				break;
			}
		}

		return "Choose what to dye &" + hex + colorName;
	}

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();

		for (ItemFrame itemFrame : additions.keySet()) {
			Decoration decoration = new Decoration(additions.get(itemFrame), itemFrame);
			items.add(ClickableItem.of(decoration.getItemDrop(viewer), e -> {
				decoration.paint(viewer, paintbrush);
				reopenMenu();
			}));
		}

		paginator().items(items).perPage(9).useGUIArrows().build();
	}

	public void reopenMenu() {
		var additions = BedAddition.BedAdditionListener.getBedAdditions(origin, (Bed) origin.getBlockData(), viewer);
		if (additions == null || additions.isEmpty())
			close();

		new BedPaintProvider(paintbrush, origin, additions).open(viewer);
	}
}
