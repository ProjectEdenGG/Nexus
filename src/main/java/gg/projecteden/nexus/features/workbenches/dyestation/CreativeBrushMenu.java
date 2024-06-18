package gg.projecteden.nexus.features.workbenches.dyestation;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomTexture;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.ChoiceType;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.DyeChoice;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CreativeBrushMenu extends InventoryProvider implements IDyeMenu {
	private ColorChoice.ChoiceType choiceType = ChoiceType.DYE;
	private ColorChoice.DyeChoice dyeChoice;
	private int dyePage = 0;
	private ItemStack paintbrush;

	private static final ItemBuilder CREATIVE_BRUSH = new ItemBuilder(CustomMaterial.CREATIVE_PAINTBRUSH)
			.name("&eMaster Brush")
			.lore("")
			.lore("&3How to use:")
			.lore("&eSneak&3+&eRClick &3to dye the brush")
			.lore("&eRClick &3decoration to dye it")
			.lore("&eLClick &3decoration to copy the color")
			.dyeColor(ColorType.WHITE)
			.glow()
			.itemFlags(ItemBuilder.ItemFlags.HIDE_ALL);

	public static ItemBuilder getCreativeBrush() {
		return CREATIVE_BRUSH.clone();
	}

	public static boolean canOpenMenu(Player player) {
		if (!player.isSneaking())
			return false;

		if (!Rank.of(player).isAdmin()) { // Admin bypass
			if (!List.of(WorldGroup.CREATIVE, WorldGroup.STAFF).contains(WorldGroup.of(player)))
				return false;

			if (!player.getGameMode().equals(GameMode.CREATIVE))
				return false;
		}

		if (!DecorationUtils.canUseFeature(player)) { // TODO DECORATIONS - Remove on release
			DecorationError.UNRELEASED_FEATURE.send(player);
			return false;
		}

		if (!isMasterPaintbrush(ItemUtils.getToolRequired(player)))
			return false;

		return true;
	}

	public static boolean tryOpenMenu(Player player) {
		if (!canOpenMenu(player))
			return false;

		openMenu(player);
		return true;
	}

	public static void openMenu(Player player) {
		new CreativeBrushMenu().open(player);
	}

	public static void copyDye(Player player, ItemStack tool, Decoration decoration) {
		if (!(decoration.getConfig() instanceof Dyeable))
			return;

		ItemStack itemStack = decoration.getItem(player);
		if (itemStack == null)
			return;

		Color dyeColor = new ItemBuilder(itemStack).dyeColor();
		if (dyeColor == null)
			return;

		ItemStack newTool = new ItemBuilder(tool.clone()).dyeColor(dyeColor).build();
		DecorationUtils.updateLore(newTool, player);
		tool.setItemMeta(newTool.getItemMeta());
	}

	@Override
	public void open(Player viewer) {
		if (this.paintbrush == null) {
			this.paintbrush = ItemUtils.getToolRequired(viewer);
		}

		validatePaintbrush();

		super.open(viewer);
	}

	@Override
	public String getTitle() {
		return CustomTexture.GUI_DYE_STATION_CREATIVE.getMenuTexture();
	}

	@Override
	public void init() {
		validatePaintbrush();

		addCloseItem();

		emptyColorOptions(contents);

		fillColors(contents, choiceType, dyePage);

		if (choiceType.equals(ColorChoice.ChoiceType.DYE) && dyeChoice != null)
			fillChoices(contents, dyeChoice, ChoiceType.DYE);

		contents.set(SLOT_CHEAT_DYE, ClickableItem.of(DyeStation.getMagicDye().resetLore().build(),
				e -> updateDyeChoice(ChoiceType.DYE)));
		contents.set(SLOT_CHEAT_STAIN, ClickableItem.of(DyeStation.getMagicStain().resetLore().build(),
				e -> updateDyeChoice(ChoiceType.STAIN)));
	}

	private void updateDyeChoice(ChoiceType choiceType) {
		validatePaintbrush();

		this.choiceType = choiceType;
		reopenMenu(contents);
	}

	@Override
	public void fillChoices(InventoryContents contents, DyeChoice dyeChoice, ChoiceType choiceType) {
		validatePaintbrush();

		this.dyeChoice = dyeChoice;
		IDyeMenu.super.fillChoices(contents, dyeChoice, choiceType);
	}

	@Override
	public void setResultItem(Color color) {
		validatePaintbrush();

		if (color == null)
			return;

		ItemStack newPaintbrush = new ItemBuilder(paintbrush.clone()).dyeColor(color).build();
		DecorationUtils.updateLore(newPaintbrush, viewer);
		paintbrush.setItemMeta(newPaintbrush.getItemMeta());
	}

	@Override
	public void reopenMenu(InventoryContents contents) {
		reopenMenu(contents, dyePage);
	}

	@Override
	public void reopenMenu(InventoryContents contents, int dyePage) {
		this.dyePage = dyePage;
		this.paintbrush = ItemUtils.getTool(viewer);
		validatePaintbrush();

		init();
	}

	private void validatePaintbrush() {
		if (isMasterPaintbrush(this.paintbrush))
			return;

		close();
		throw new InvalidInputException("You are not holding a master brush!");
	}

	public static boolean isMasterPaintbrush(ItemStack item) {
		return getCreativeBrush().modelId() == ModelId.of(item);
	}
}
