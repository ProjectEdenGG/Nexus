package gg.projecteden.nexus.features.workbenches.dyestation;

import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.workbenches.CustomBench;
import gg.projecteden.nexus.features.workbenches.ICraftableCustomBench;
import gg.projecteden.nexus.framework.features.Unreleased;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;

@Unreleased
@NoArgsConstructor
public class DyeStation extends CustomBench implements ICraftableCustomBench {
	public static final String USAGE_LORE = "&3Used in Dye Station";
	private static final int MAX_USES = 5;
	public static final int MAX_USES_PAINTBRUSH = MAX_USES * 2;
	public static final String USES_LORE = "&3Uses: &e";

	private static final ItemBuilder WORKBENCH = new ItemBuilder(CustomMaterial.DYE_STATION).name("Dye Station");

	public static ItemBuilder getWorkbench() {
		return WORKBENCH.clone();
	}

	private static final ItemBuilder MAGIC_DYE = new ItemBuilder(ColorChoice.ChoiceType.DYE.getBottleMaterial())
			.name(Gradient.of(List.of(ChatColor.RED, ChatColor.YELLOW, ChatColor.AQUA)).apply("Magic Dye"))
			.lore(USAGE_LORE, USES_LORE + MAX_USES);

	public static ItemBuilder getMagicDye() {
		return MAGIC_DYE.clone();
	}

	private static final ItemBuilder MAGIC_STAIN = new ItemBuilder(ColorChoice.ChoiceType.STAIN.getBottleMaterial())
			.name(Gradient.of(List.of(ChatColor.of("#e0a175"), ChatColor.of("#5c371d"))).apply("Magic Stain"))
			.lore(USAGE_LORE, USES_LORE + MAX_USES);

	public static ItemBuilder getMagicStain() {
		return MAGIC_STAIN.clone();
	}

	private static final ItemBuilder PAINTBRUSH = new ItemBuilder(CustomMaterial.PAINTBRUSH)
			.name("&ePaintbrush")
			.lore(USES_LORE + 0, "&3Used to dye placed decoration")
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
	public RecipeBuilder<?> getBenchRecipe() {
		return shaped("111", "232", "242")
			.add('1', Material.WHITE_WOOL)
			.add('2', Tag.PLANKS)
			.add('3', getMagicDye().build())
			.add('4', getMagicStain().build())
			.toMake(getWorkbench().build());
	}

	@Override
	public List<RecipeBuilder<?>> getAdditionRecipes() {
		List<RecipeBuilder<?>> recipes = new ArrayList<>();

		// Magic Dye
		recipes.add(
			shapeless(Material.GLASS_BOTTLE, Material.RED_DYE, Material.ORANGE_DYE, Material.YELLOW_DYE,
				Material.GREEN_DYE, Material.CYAN_DYE, Material.BLUE_DYE, Material.PURPLE_DYE, Material.PINK_DYE)
				.toMake(DyeStation.getMagicDye().build())
		);

		// Magic Stain
		recipes.add(
			shapeless(Material.GLASS_BOTTLE, Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS,
				Material.DARK_OAK_PLANKS, Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.BIRCH_SAPLING, Material.DARK_OAK_SAPLING)
				.toMake(DyeStation.getMagicStain().build())
		);

		return recipes;
	}

	public static void open(Player player) {
		if (!DecorationUtils.canUseFeature(player, DecorationType.DYE_STATION.getConfig())) {
			DecorationError.UNRELEASED_FEATURE.send(player);
			return;
		}

		DyeStationMenu.DyeStationMode mode = DyeStationMenu.DyeStationMode.NORMAL;
		if (DecorationUtils.hasBypass(player))
			mode = DyeStationMenu.DyeStationMode.CHEAT;

		open(player, mode);
	}

	public static void open(Player player, DyeStationMenu.DyeStationMode mode) {
		new DyeStationMenu(mode).open(player);
	}

	public static boolean isMagicPaintbrush(ItemStack item) {
		return getPaintbrush().modelId() == ModelId.of(item);
	}

	public static boolean isPaintbrush(ItemStack item) {
		return isMagicPaintbrush(item) || CreativeBrushMenu.isMasterPaintbrush(item);
	}

}
