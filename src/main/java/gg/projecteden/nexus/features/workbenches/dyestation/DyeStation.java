package gg.projecteden.nexus.features.workbenches.dyestation;

import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationTagType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.workbenches.CustomBench;
import gg.projecteden.nexus.features.workbenches.ICraftableCustomBench;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.ChoiceType;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class DyeStation extends CustomBench implements ICraftableCustomBench {
	public static final String USAGE_LORE = "&3Used in Dye Station";
	private static final int MAX_USES = 5;
	public static final int MAX_USES_PAINTBRUSH = MAX_USES * 2;
	public static final String USES_LORE = "&3Uses: &e";

	private static final ItemBuilder WORKBENCH = new ItemBuilder(ItemModelType.DYE_STATION)
		.name("Dye Station")
		.lore(DecorationTagType.INTERACTABLE.getTags());

	public static ItemBuilder getWorkbench() {
		return WORKBENCH.clone();
	}

	private static final ItemBuilder MAGIC_DYE = new ItemBuilder(ChoiceType.DYE.getBottleItemModelType())
		.name(Gradient.of(List.of(ChatColor.RED, ChatColor.YELLOW, ChatColor.AQUA)).apply("Magic Dye"))
		.lore(USAGE_LORE, USES_LORE + MAX_USES)
		.lore(DecorationTagType.TOOL.getTags());

	public static ItemBuilder getMagicDye() {
		return MAGIC_DYE.clone();
	}

	private static final ItemBuilder MAGIC_STAIN = new ItemBuilder(ChoiceType.STAIN.getBottleItemModelType())
		.name(Gradient.of(List.of(ChatColor.of("#e0a175"), ChatColor.of("#5c371d"))).apply("Magic Stain"))
		.lore(USAGE_LORE, USES_LORE + MAX_USES)
		.lore(DecorationTagType.TOOL.getTags());

	public static ItemBuilder getMagicStain() {
		return MAGIC_STAIN.clone();
	}

	private static final ItemBuilder MAGIC_MINERAL = new ItemBuilder(ChoiceType.MINERAL.getBottleItemModelType())
		.name(Gradient.of(List.of(ChatColor.of("#6A6A6A"), ChatColor.of("#D37A5A"), ChatColor.of("#E1C16E"))).apply("Magic Mineral"))
		.lore(USAGE_LORE, USES_LORE + MAX_USES)
		.lore(DecorationTagType.TOOL.getTags());

	public static ItemBuilder getMagicMineral() {
		return MAGIC_MINERAL.clone();
	}

	private static final ItemBuilder PAINTBRUSH = new ItemBuilder(ItemModelType.PAINTBRUSH)
		.name("&ePaintbrush")
		.lore(USES_LORE + 0)
		.lore("")
		.lore("&3How to use:")
		.lore("&eDye &3this brush in the dye station")
		.lore("&eRClick &3decoration to dye it")
		.lore(DecorationTagType.TOOL.getTags())
		.dyeColor(ColorType.WHITE)
		.itemFlags(ItemBuilder.ItemFlags.HIDE_ALL);

	public static ItemBuilder getPaintbrush() {
		return PAINTBRUSH.clone();
	}

	@Override
	public CustomBenchType getBenchType() {
		return CustomBenchType.DYE_STATION;
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.DECORATION;
	}

	@Override
	public RecipeBuilder<?> getBenchRecipe() {
		return RecipeBuilder.shaped("123", "444", "555")
			.add('1', getMagicDye().build())
			.add('2', getMagicStain().build())
			.add('3', getMagicMineral().build())
			.add('4', Material.WHITE_WOOL)
			.add('5', Tag.PLANKS)
			.toMake(getWorkbench().build());
	}

	@Override
	public List<RecipeBuilder<?>> getAdditionRecipes() {
		List<RecipeBuilder<?>> recipes = new ArrayList<>();

		// Magic Dye
		recipes.add(
			RecipeBuilder.shapeless(Material.GLASS_BOTTLE, Material.RED_DYE, Material.ORANGE_DYE, Material.YELLOW_DYE,
				Material.GREEN_DYE, Material.CYAN_DYE, Material.BLUE_DYE, Material.PURPLE_DYE, Material.PINK_DYE)
				.toMake(DyeStation.getMagicDye().build())
		);

		// Magic Stain
		recipes.add(
			RecipeBuilder.shapeless(Material.GLASS_BOTTLE, Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS,
				Material.DARK_OAK_PLANKS, Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.BIRCH_SAPLING, Material.DARK_OAK_SAPLING)
				.toMake(DyeStation.getMagicStain().build())
		);

		// Magic Mineral
		recipes.add(
			RecipeBuilder.shapeless(Material.GLASS_BOTTLE, Material.COAL, Material.IRON_INGOT, Material.COPPER_INGOT,
				Material.GOLD_INGOT, Material.EMERALD, Material.LAPIS_LAZULI, Material.AMETHYST_SHARD, Material.QUARTZ)
				.toMake(DyeStation.getMagicMineral().build())
		);

		return recipes;
	}

	public static void open(Player player) {
		DyeStationMenu.DyeStationMode mode = DyeStationMenu.DyeStationMode.NORMAL;
		if (DecorationUtils.hasBypass(player))
			mode = DyeStationMenu.DyeStationMode.CHEAT;

		open(player, mode);
	}

	public static void open(Player player, DyeStationMenu.DyeStationMode mode) {
		new DyeStationMenu(mode).open(player);
	}

	public static boolean isMagicPaintbrush(ItemStack item) {
		return Objects.equals(getPaintbrush().model(), Model.of(item));
	}

	public static boolean isPaintbrush(ItemStack item) {
		return isMagicPaintbrush(item) || CreativeBrushMenu.isCreativePaintbrush(item);
	}

}
