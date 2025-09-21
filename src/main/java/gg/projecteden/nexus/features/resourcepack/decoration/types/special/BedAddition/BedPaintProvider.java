package gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.StainChoice;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
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
	Material closestBedColor;
	boolean isStained;
	Material bedColorRight;
	Material bedColorLeft;

	public BedPaintProvider(@NonNull BedInteractionData data, Material closestBedColor, Material bedColorRight, Material bedColorLeft) {
		this.data = data;
		this.additions = data.getAdditionsBoth();
		this.paintbrush = data.getTool();
		this.paintbrushDye = new ItemBuilder(this.paintbrush).dyeColor();
		this.closestBedColor = closestBedColor;
		this.isStained = StainChoice.of(paintbrushDye) != null;
		this.bedColorRight = bedColorRight;
		this.bedColorLeft = bedColorLeft;
	}

	@Override
	public String getTitle() {
		String hex = StringUtils.toHex(paintbrushDye);

		String colorName = hex;
		StainChoice stainChoice = StainChoice.of(paintbrushDye);
		if (stainChoice != null)
			colorName = StringUtils.camelCase(stainChoice);

		return "Choose what to dye &" + hex + colorName;
	}

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();

		if (!isStained && closestBedColor != null) {

			if (bedColorLeft != null) {
				Block bedLeft = data.getBedLeft();
				if (bedLeft != null) {
					items.add(ClickableItem.of(new ItemBuilder(bedLeft.getType()).name("Left Bed"), e -> {
						updateBedColor(closestBedColor, bedLeft, data.getBedLeftData().getFacing());
						DecorationUtils.usePaintbrush(viewer, paintbrush);
						reopenMenu();
					}));
				}
			}

			if (bedColorRight != null) {
				Block bedRight = data.getBedRight();
				if (bedRight != null) {
					items.add(ClickableItem.of(new ItemBuilder(bedRight.getType()).name("Right Bed"), e -> {
						updateBedColor(closestBedColor, bedRight, data.getBedRightData().getFacing());
						DecorationUtils.usePaintbrush(viewer, paintbrush);
						reopenMenu();
					}));
				}
			}
		}

		for (ItemFrame itemFrame : additions.keySet()) {
			DecorationConfig config = additions.get(itemFrame);
			if (config == null)
				continue;

			Decoration decoration = new Decoration(config, itemFrame);
			items.add(ClickableItem.of(decoration.getItemDrop(viewer), e -> {
				decoration.paint(viewer, data.getOrigin(), paintbrush);
				reopenMenu();
			}));
		}

		paginator().items(items).perPage(9).useGUIArrows().build();
	}

	public static void updateBedColor(Material bedColor, Block bed1, BlockFace headFacing) {
		_updateBedColor(bedColor, bed1, (Bed) bed1.getBlockData());

		Block bed2 = bed1.getRelative(headFacing.getOppositeFace());
		_updateBedColor(bedColor, bed2, (Bed) bed2.getBlockData());
	}

	private static void _updateBedColor(Material bedColor, Block bed, Bed oldData) {
		BlockData blockData = bedColor.createBlockData();

		org.bukkit.block.data.type.Bed newData = (org.bukkit.block.data.type.Bed) blockData;

		if (oldData.getPart() == org.bukkit.block.data.type.Bed.Part.HEAD)
			newData.setPart(org.bukkit.block.data.type.Bed.Part.HEAD);
		else
			newData.setPart(org.bukkit.block.data.type.Bed.Part.FOOT);

		newData.setFacing(oldData.getFacing());

		bed.setBlockData(newData, false);
	}

	public void reopenMenu() {
		data.refreshAdditions();

		var additions = data.getAdditionsBoth();
		if (additions == null || additions.isEmpty())
			close();

		new BedPaintProvider(data, additions, paintbrush, paintbrushDye, closestBedColor, isStained,
				data.bedRight.getType(), data.bedLeft.getType()).open(viewer);
	}
}
