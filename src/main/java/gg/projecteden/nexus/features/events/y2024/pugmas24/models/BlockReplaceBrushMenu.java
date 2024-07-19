package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Rows(4)
@Title("Select a color")
public class BlockReplaceBrushMenu extends InventoryProvider {

	private static final ItemBuilder BRUSH = new ItemBuilder(CustomMaterial.EVENT_PAINTBRUSH)
			.name("Block Replacer Brush")
			.lore("&3Color: " + StringUtils.colorLabel(BalloonEditor.getBrushColor()) + " Wool")
			.lore("")
			.lore("&3How to use:")
			.lore("&eLClick &3to change the color")
			.lore("&eRClick &3a block to replace it")
			.dyeColor(ColorType.RED)
			.glow()
			.itemFlags(ItemBuilder.ItemFlags.HIDE_ALL);

	public BlockReplaceBrushMenu(ItemStack brushItem) {
		this.brushItem = brushItem;
	}

	public static ItemBuilder getBrushItem() {
		return BRUSH.clone();
	}

	private SlotPos slotPos = SlotPos.of(1, 0);
	private final ItemStack brushItem;

	@Override
	public void init() {
		addCloseItem();

		for (Material woolType : colorOrder) {
			contents.set(slotPos, ClickableItem.of(new ItemStack(woolType), e -> {
				new SoundBuilder(Sound.UI_STONECUTTER_SELECT_RECIPE).receiver(BalloonEditor.getEditor()).play();

				ColorType colorType = ColorType.of(woolType);
				BalloonEditor.setBrushColor(colorType);
				dyeBrush(colorType);

				close();
			}));

			nextSlot();
		}
	}

	private void dyeBrush(ColorType brushColor) {
		if (brushItem == null)
			return;

		ItemBuilder newBrush = new ItemBuilder(getBrushItem()).dyeColor(brushColor);
		List<String> finalLore = new ArrayList<>();

		// change lore
		List<String> lore = newBrush.getLore();
		if (Nullables.isNotNullOrEmpty(lore))
			lore = new ArrayList<>();

		List<String> newLore = new ArrayList<>();
		for (String line : lore) {
			String _line = stripColor(line);
			// remove color line
			if (_line.contains("Color: "))
				continue;

			newLore.add(line);
		}

		// add color line
		finalLore.add("&3Color: " + StringUtils.colorLabel(brushColor) + " Wool");

		finalLore.addAll(newLore);
		newBrush.lore(finalLore);

		ItemStack result = newBrush.build();
		brushItem.setItemMeta(result.getItemMeta());
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
}
