package gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition.BedAdditionUtils.BedInteractionData;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Rows(3)
@AllArgsConstructor
public class BedPaintProvider extends InventoryProvider {

	BedInteractionData data;
	Map<ItemFrame, DecorationConfig> additions;
	ItemStack paintbrush;
	Color paintbrushDye;

	public BedPaintProvider(@NonNull BedInteractionData data) {
		this.data = data;
		this.additions = data.getAdditionsBoth();
		this.paintbrush = data.getTool();
		this.paintbrushDye = new ItemBuilder(this.paintbrush).dyeColor();
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
			DecorationConfig config = additions.get(itemFrame);
			if (config == null)
				continue;

			Decoration decoration = new Decoration(config, itemFrame);
			items.add(ClickableItem.of(decoration.getItemDrop(viewer), e -> {
				decoration.paint(viewer, paintbrush);
				reopenMenu();
			}));
		}

		paginator().items(items).perPage(9).useGUIArrows().build();
	}

	public void reopenMenu() {
		data.refreshAdditions();

		var additions = data.getAdditionsBoth();
		if (additions == null || additions.isEmpty())
			close();

		new BedPaintProvider(data, additions, paintbrush, paintbrushDye).open(viewer);
	}
}
