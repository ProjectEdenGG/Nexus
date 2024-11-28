package gg.projecteden.nexus.features.events.y2025.pugmas25.balloons;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Rows(4)
@Title("Select a color")
public class Pugmas25BlockReplaceBrushMenu extends InventoryProvider {

	public static final CustomMaterial BRUSH_MATERIAL = CustomMaterial.EVENT_PAINTBRUSH;
	private static final ItemBuilder BRUSH = new ItemBuilder(BRUSH_MATERIAL)
			.name("&eBlock Replacer Brush")
		.lore("&3Block: " + getColorLabel(Pugmas25BalloonEditor.defaultBrushColor))
			.lore("")
			.lore("&3How to use:")
			.lore("&eLClick &3a block to change the brush color")
			.lore("&eRClick &3wool to replace it")
		.dyeColor(Pugmas25BalloonEditor.defaultBrushColor)
			.undroppable().unframeable().unplaceable().unstorable().untrashable().untradeable()
			.itemFlags(ItemBuilder.ItemFlags.HIDE_ALL)
			.updateDecorationLore(false)
			.glow();

	public static ItemBuilder getBrushItem() {
		return BRUSH.clone();
	}

	private SlotPos slotPos = SlotPos.of(1, 0);
	private final ItemStack tool;

	public Pugmas25BlockReplaceBrushMenu(ItemStack tool) {
		this.tool = tool;
	}

	@Override
	public void init() {
		addCloseItem();

		for (Material woolType : colorOrder) {
			contents.set(slotPos, ClickableItem.of(new ItemStack(woolType), e -> {
				new SoundBuilder(Sound.UI_STONECUTTER_SELECT_RECIPE).receiver(Pugmas25BalloonEditor.getEditor()).play();

				ColorType colorType = ColorType.of(woolType);
				Pugmas25BalloonEditor.setBrushColor(colorType);
				dyeBrush(colorType);

				close();
			}));

			nextSlot();
		}
	}

	private void dyeBrush(ColorType brushColor) {
		if (tool == null)
			return;

		ItemBuilder resultBuilder = getBrushItem().dyeColor(brushColor);

		// fix color line
		List<String> newLore = new ArrayList<>();
		for (String line : resultBuilder.getLore()) {
			String _line = StringUtils.stripColor(line);
			if (_line.contains("Block:")) {
				newLore.add("&3Block: " + getColorLabel(brushColor));
				continue;
			}

			newLore.add(line);
		}

		// finalize item
		resultBuilder.setLore(newLore);
		ItemStack result = resultBuilder.build();
		tool.setItemMeta(result.getItemMeta());
	}

	//

	private static final SlotPos SKIP_LEFT = SlotPos.of(2, 0);
	private static final SlotPos SKIP_RIGHT = SlotPos.of(2, 8);

	private void nextSlot() {
		int col = slotPos.getColumn();
		int row = slotPos.getRow();

		col++;
		if (col > 8) {
			col = 0;
			row++;
		}

		slotPos = SlotPos.of(row, col);

		if (SKIP_LEFT.matches(slotPos) || SKIP_RIGHT.matches(slotPos))
			nextSlot();
	}

	public static boolean isBrushItem(ItemStack item) {
		return getBrushItem().modelId() == ModelId.of(item);
	}

	private static final List<Material> colorOrder = List.of(Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
			Material.LIME_WOOL, Material.GREEN_WOOL, Material.CYAN_WOOL, Material.LIGHT_BLUE_WOOL, Material.BLUE_WOOL,
			Material.PURPLE_WOOL, Material.MAGENTA_WOOL, Material.PINK_WOOL, Material.BROWN_WOOL, Material.WHITE_WOOL,
			Material.LIGHT_GRAY_WOOL, Material.GRAY_WOOL, Material.BLACK_WOOL);

	private static @NotNull String getColorLabel(ColorType colorType) {
		return StringUtils.colorLabel(colorType, StringUtils.camelCase(colorType.getWool()));
	}
}
