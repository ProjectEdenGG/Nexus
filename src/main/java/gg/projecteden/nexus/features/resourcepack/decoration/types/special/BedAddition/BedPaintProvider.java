package gg.projecteden.nexus.features.resourcepack.decoration.types.special.BedAddition;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.StainChoice;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
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
	BedColor closestBedColor;
	boolean isStained;
	BedColor bedColorRight;
	BedColor bedColorLeft;

	public BedPaintProvider(@NonNull BedInteractionData data, BedColor closestBedColor, BedColor bedColorRight, BedColor bedColorLeft) {
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

	public static void updateBedColor(BedColor bedColor, Block bed1, BlockFace headFacing) {
		_updateBedColor(bedColor, bed1, (Bed) bed1.getBlockData());

		Block bed2 = bed1.getRelative(headFacing.getOppositeFace());
		_updateBedColor(bedColor, bed2, (Bed) bed2.getBlockData());
	}

	private static void _updateBedColor(BedColor bedColor, Block bed, Bed oldData) {
		BlockData blockData = bedColor.getMaterial().createBlockData();

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
				BedColor.of(data.bedRight), BedColor.of(data.bedLeft)).open(viewer);
	}

	@AllArgsConstructor
	public enum BedColor {
		RED(DyeColor.RED, Material.RED_BED),
		ORANGE(DyeColor.ORANGE, Material.ORANGE_BED),
		YELLOW(DyeColor.YELLOW, Material.YELLOW_BED),
		LIME(DyeColor.LIME, Material.LIME_BED),
		GREEN(DyeColor.GREEN, Material.GREEN_BED),
		CYAN(DyeColor.CYAN, Material.CYAN_BED),
		LIGHT_BLUE(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_BED),
		BLUE(DyeColor.BLUE, Material.BLUE_BED),
		PURPLE(DyeColor.PURPLE, Material.PURPLE_BED),
		MAGENTA(DyeColor.MAGENTA, Material.MAGENTA_BED),
		PINK(DyeColor.PINK, Material.PINK_BED),
		WHITE(DyeColor.WHITE, Material.WHITE_BED),
		LIGHT_GRAY(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_BED),
		GRAY(DyeColor.GRAY, Material.GRAY_BED),
		BLACK(DyeColor.BLACK, Material.BLACK_BED),
		BROWN(DyeColor.BROWN, Material.BROWN_BED);

		@Getter
		private final DyeColor dyeColor;
		@Getter
		private final Material material;

		public Color getColor() {
			return dyeColor.getColor();
		}

		private static BedColor of(Color color) {
			if (color == null)
				return null;

			for (BedColor bedColor : values()) {
				if (bedColor.getColor().equals(color))
					return bedColor;
			}
			return null;
		}

		public static BedColor of(Block block) {
			if (Nullables.isNullOrAir(block))
				return null;

			for (BedColor bedColor : values()) {
				if (bedColor.getMaterial() == block.getType())
					return bedColor;
			}
			return null;
		}

		public static @Nullable BedColor ofClosest(Color bukkitColor) {
			java.awt.Color color = new java.awt.Color(bukkitColor.getRed(), bukkitColor.getGreen(), bukkitColor.getBlue(), bukkitColor.getAlpha());
			final byte index = ((byte[]) getColorModel().getDataElements(color.getRGB(), null))[0];
			return of(ColorType.toBukkit(colors[index]));
		}

		private static IndexColorModel colorModel;
		private static java.awt.Color[] colors;

		private static IndexColorModel getColorModel() {
			if (colorModel != null)
				return colorModel;

			colors = new java.awt.Color[values().length];
			for (int i = 0; i < values().length; i++) {
				colors[i] = ColorType.toJava(values()[i].getColor());
			}

			colorModel = createColorModel(colors);
			return colorModel;
		}

		private static IndexColorModel createColorModel(java.awt.Color[] colors) {
			final int[] colorMap = new int[colors.length];
			for (int i = 0; i < colors.length; i++) {
				colorMap[i] = colors[i].getRGB();
			}
			final int bits = (int) Math.ceil(Math.log(colorMap.length) / Math.log(2));
			return new IndexColorModel(bits, colorMap.length, colorMap, 0, false, -1, DataBuffer.TYPE_BYTE);
		}
	}
}
