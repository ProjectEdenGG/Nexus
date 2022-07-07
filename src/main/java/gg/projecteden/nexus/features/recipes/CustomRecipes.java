package gg.projecteden.nexus.features.recipes;

import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.listeners.events.FixedCraftItemEvent;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.workbenches.DyeStation;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.CopperState;
import gg.projecteden.nexus.utils.CopperState.CopperBlockType;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.ItemUtils.ItemStackComparator;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WoodType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.api.common.utils.ReflectionUtils.subTypesOf;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.blast;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shapeless;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.smelt;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.surround;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Depends({ResourcePack.class, CustomEnchants.class})
public class CustomRecipes extends Feature implements Listener {

	@Getter
	public static List<NexusRecipe> recipes = new ArrayList<>();

	private static boolean loaded;

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent ignored) {
		if (loaded)
			return;

		loaded = true;

		Tasks.async(() -> {
			registerDyes();
			registerSlabs();
			registerQuartz();
			registerStoneBricks();
			registerFurnace();
			misc();

			subTypesOf(FunctionalRecipe.class, getClass().getPackageName()).stream()
				.map(clazz -> {
					try {
						if (!Utils.canEnable(clazz))
							return null;

						return clazz.getConstructor().newInstance();
					} catch (Exception ex) {
						Nexus.log("Error while enabling functional recipe " + clazz.getSimpleName());
						ex.printStackTrace();
						return null;
					}
				})
				.filter(obj -> Objects.nonNull(obj) && obj.getResult() != null)
				.sorted((recipe1, recipe2) -> new ItemStackComparator().compare(recipe1.getResult(), recipe2.getResult()))
				.forEach(recipe -> {
					recipe.setType(recipe.getRecipeType());
					recipe.register();
					recipes.add(recipe);
				});
		});
	}

	public static void register(Recipe recipe) {
		if (recipe == null)
			return;

		final NamespacedKey key = ((Keyed) recipe).getKey();

		try {
			for (Recipe recipe1 : Bukkit.getServer().getRecipesFor(recipe.getResult()))
				if (RecipeUtils.areEqual(recipe, recipe1))
					return;

			Tasks.sync(() -> {
				try {
					Bukkit.addRecipe(recipe);
				} catch (IllegalStateException duplicate) {
					Nexus.log(duplicate.getMessage());
				} catch (Exception ex) {
					Nexus.log("Error while adding custom recipe " + key + " to Bukkit");
					ex.printStackTrace();
				}
			});
		} catch (Exception ex) {
			Nexus.log("Error while adding custom recipe " + key);
			ex.printStackTrace();
		}
	}

	public NexusRecipe getCraftByRecipe(Recipe result) {
		return recipes.stream().filter(nexusRecipe ->
				((Keyed) nexusRecipe.getRecipe()).getKey().equals(((Keyed) result).getKey())).findFirst().orElse(null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreCraft(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player)) return;
		if (event.getRecipe() == null) return;
		NexusRecipe recipe = getCraftByRecipe(event.getRecipe());
		if (recipe == null) return;
		if (recipe.getPermission() != null && !player.hasPermission(recipe.getPermission()))
			event.getInventory().setResult(null);
		else if (recipe.getResult().hasItemMeta())
			event.getInventory().setResult(recipe.getResult());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCraft(CraftItemEvent event) {
		NexusRecipe recipe = getCraftByRecipe(event.getRecipe());
		if (recipe == null) return;
		if (recipe.getPermission() == null) return;
		if (!event.getWhoClicked().hasPermission(recipe.getPermission()))
			event.setCancelled(true);
	}

	@NotNull
	public static RecipeChoice choiceOf(MaterialTag tag) {
		return new MaterialChoice(tag.toArray());
	}

	@NotNull
	public static RecipeChoice choiceOf(Material... material) {
		return new MaterialChoice(material);
	}

	@NotNull
	public static RecipeChoice choiceOf(ItemStack... items) {
		return new ExactChoice(items);
	}

	public static RecipeChoice choiceOf(List<?> choices) {
		if (choices.isEmpty())
			return null;

		final Object object = choices.get(0);
		if (object instanceof Material)
			return new MaterialChoice((List<Material>) choices);
		else if (object instanceof ItemStack)
			return new ExactChoice((List<ItemStack>) choices);
		else
			return null;
	}

	public void registerDyes() {
		final List<MaterialTag> surround = List.of(
			MaterialTag.CONCRETE_POWDERS,
			MaterialTag.STAINED_GLASS,
			MaterialTag.STAINED_GLASS_PANES,
			MaterialTag.COLORED_TERRACOTTAS
		);

		final List<MaterialTag> shapeless = List.of(
			MaterialTag.BEDS,
			MaterialTag.STANDING_BANNERS
		);

		for (ColorType color : ColorType.getDyes()) {
			final Material dye = color.switchColor(Material.WHITE_DYE);

			BiConsumer<NexusRecipe, RecipeType> register = (recipe, type) -> recipe.type(type).register();

			surround.forEach(tag ->
				register.accept(surround(dye).with(tag).toMake(color.switchColor(tag.first()), 8).build(), RecipeType.DYES));

			shapeless.forEach(tag -> register.accept(shapeless().add(dye).add(choiceOf(tag)).toMake(color.switchColor(tag.first())).build(), RecipeType.BEDS_BANNERS));

			surround(Material.WATER_BUCKET).with(color.getConcretePowder()).toMake(color.getConcrete(), 8).build().type(RecipeType.MISC).register();
		}
	}

	public void registerSlabs() {
		Material[] slabs = new MaterialTag(Tag.SLABS).toArray();

		String[] blockNames = { "BRICKS", "_PLANKS", "_BLOCK", "" };
		for (Material slab : slabs) {
			Material blockMaterial = null;
			for (String blockName : blockNames) {
				try {
					blockMaterial = Material.valueOf(slab.name().replace("BRICK_SLAB", blockName).replace("_SLAB", blockName));
				} catch (IllegalArgumentException ignore) { }
			}

			if (slab == Material.QUARTZ_SLAB)
				blockMaterial = Material.QUARTZ_BLOCK;
			if (slab == Material.DEEPSLATE_TILE_SLAB)
				blockMaterial = Material.DEEPSLATE_TILES;

			if (blockMaterial == null) continue;

			List<Material> slabsGroup = new ArrayList<>();
			for (int i = 0; i < 4; i++)
				slabsGroup.add(slab);
			shapeless().add(slabsGroup.toArray(Material[]::new)).toMake(blockMaterial, 2).extra("slabs").build().type(RecipeType.SLABS).register();
		}
	}

	public void registerQuartz() {
		shapeless().add(Material.QUARTZ_BLOCK).toMake(Material.QUARTZ, 4).extra("quartz_uncrafting").build().type(RecipeType.QUARTZ).register();
		shapeless().add(Material.QUARTZ_PILLAR).toMake(Material.QUARTZ_BLOCK).extra("quartz_uncrafting").build().type(RecipeType.QUARTZ).register();
		shapeless().add(Material.CHISELED_QUARTZ_BLOCK).toMake(Material.QUARTZ_SLAB, 2).extra("quartz_uncrafting").build().type(RecipeType.QUARTZ).register();
		shapeless().add(Material.QUARTZ_BRICKS).toMake(Material.QUARTZ_BLOCK).extra("quartz_uncrafting_bricks").build().type(RecipeType.QUARTZ).register();
	}

	public void registerStoneBricks() {
		shapeless().add(Material.STONE_BRICKS).toMake(Material.STONE).extra("stonebrick_uncrafting").build().type(RecipeType.STONE_BRICK).register();
		shapeless().add(Material.CHISELED_STONE_BRICKS).toMake(Material.STONE_BRICK_SLAB, 2).extra("stonebrick_uncrafting").build().type(RecipeType.STONE_BRICK).register();
		shapeless().add(Material.MOSSY_STONE_BRICKS).toMake(Material.STONE_BRICKS).extra("stonebrick_uncrafting").build().type(RecipeType.STONE_BRICK).register();
		shapeless().add(Material.CHISELED_DEEPSLATE).toMake(Material.COBBLED_DEEPSLATE_SLAB, 2).extra("stonebrick_uncrafting").build().type(RecipeType.STONE_BRICK).register();
		shapeless().add(Material.DEEPSLATE_TILES).toMake(Material.DEEPSLATE_BRICKS).extra("stonebrick_uncrafting").build().type(RecipeType.STONE_BRICK).register();
		shapeless().add(Material.DEEPSLATE_BRICKS).toMake(Material.POLISHED_DEEPSLATE).extra("stonebrick_uncrafting").build().type(RecipeType.STONE_BRICK).register();
		shapeless().add(Material.POLISHED_DEEPSLATE).toMake(Material.COBBLED_DEEPSLATE).extra("stonebrick_uncrafting").build().type(RecipeType.STONE_BRICK).register();
	}

	private void registerFurnace() {
		smelt(Material.RAW_COPPER_BLOCK).toMake(Material.COPPER_BLOCK).exp(6.3f).time(1200).build().register();
		smelt(Material.RAW_IRON_BLOCK).toMake(Material.IRON_BLOCK).exp(6.3f).time(1200).build().register();
		smelt(Material.RAW_GOLD_BLOCK).toMake(Material.GOLD_BLOCK).exp(9f).time(1200).build().register();

		blast(Material.RAW_COPPER_BLOCK).toMake(Material.COPPER_BLOCK).exp(6.3f).time(600).build().register();
		blast(Material.RAW_IRON_BLOCK).toMake(Material.IRON_BLOCK).exp(6.3f).time(600).build().register();
		blast(Material.RAW_GOLD_BLOCK).toMake(Material.GOLD_BLOCK).exp(9f).time(600).build().register();
	}

	public void misc() {
		shaped("SLS", "L L", "LLL").add('S', Material.STRING).add('L', Material.LEATHER).toMake(Material.BUNDLE).build().type(RecipeType.MISC).register();
		surround(Material.WATER_BUCKET).with(MaterialTag.WOOL).toMake(Material.WHITE_WOOL, 8).build().type(RecipeType.WOOL).register();
		surround(Material.BLACKSTONE).with(Material.GOLD_NUGGET).toMake(Material.GILDED_BLACKSTONE).build().type(RecipeType.MISC).register();
		shapeless().add(Material.DROPPER).add(Material.BOW).toMake(Material.DISPENSER).build().type(RecipeType.MISC).register();
		shapeless().add(Material.NETHER_WART_BLOCK).toMake(Material.NETHER_WART, 9).build().type(RecipeType.MISC).register();
		shapeless().add(Material.BLUE_ICE).toMake(Material.PACKED_ICE, 9).build().type(RecipeType.MISC).register();
		shapeless().add(Material.PACKED_ICE).toMake(Material.ICE, 9).build().type(RecipeType.MISC).register();
		shapeless().add(Material.CHISELED_RED_SANDSTONE).toMake(Material.RED_SANDSTONE_SLAB, 2).build().type(RecipeType.MISC).register();
		shapeless().add(Material.CHISELED_SANDSTONE).toMake(Material.CHISELED_SANDSTONE, 2).build().type(RecipeType.MISC).register();
		shapeless().add(Material.GLOWSTONE).toMake(Material.GLOWSTONE_DUST, 3).build().type(RecipeType.MISC).register();
		shapeless().add(Material.BLAZE_POWDER, Material.BLAZE_POWDER).toMake(Material.BLAZE_ROD).build().type(RecipeType.MISC).register();
		shapeless().add(Material.DRIPSTONE_BLOCK).toMake(Material.POINTED_DRIPSTONE, 4).build().type(RecipeType.MISC).register();
		shapeless().add(Material.HONEYCOMB_BLOCK).toMake(Material.HONEYCOMB, 4).build().type(RecipeType.MISC).register();
		shapeless().add(Material.MELON).toMake(Material.MELON_SLICE, 5).build().type(RecipeType.MISC).register();
		shapeless().add(choiceOf(MaterialTag.WOOL)).toMake(Material.STRING, 4).build().type(RecipeType.MISC).register();
		shapeless().add(Material.PRISMARINE).toMake(Material.PRISMARINE_SHARD, 4).build().type(RecipeType.MISC).register();
		shapeless().add(Material.PRISMARINE_BRICKS).toMake(Material.PRISMARINE_SHARD, 9).build().type(RecipeType.MISC).register();
		shapeless().add(Material.MOSS_CARPET, 3).toMake(Material.MOSS_BLOCK, 2).build().type(RecipeType.MISC).register();
		shapeless().add(Material.SAND).add(Material.PAPER).toMake(CustomMaterial.SAND_PAPER.getNamedItem()).extra("sand_paper").build().type(RecipeType.MISC).register();
		shapeless().add(Material.RED_SAND).add(Material.PAPER).toMake(CustomMaterial.RED_SAND_PAPER.getNamedItem()).extra("red_sand_paper").build().type(RecipeType.MISC).register();

		for (CopperState state : CopperState.values())
			if (state.hasNext())
				for (CopperBlockType blockType : CopperState.CopperBlockType.values())
					surround(Material.WATER_BUCKET).with(blockType.of(state)).toMake(blockType.of(state.next()), 8).build().type(RecipeType.MISC).register();

		for (ColorType color : ColorType.getDyes()) {
			shapeless().add(color.getCarpet(), 3).toMake(color.getWool(), 2).extra("carpet_uncrafting").build().type(RecipeType.MISC).register();
			shapeless().add(color.getConcrete(), 2).toMake(color.getConcretePowder(), 2).extra("powderize").build().type(RecipeType.MISC).register();
		}

		final RecipeChoice sandpaper = choiceOf(CustomMaterial.SAND_PAPER.getItem(), CustomMaterial.RED_SAND_PAPER.getItem());
		for (WoodType wood : WoodType.values()) {
			shapeless().add(wood.getStrippedLog(), 2).toMake(wood.getLog(), 2).build().type(RecipeType.MISC).register();
			shapeless().add(wood.getStrippedWood(), 2).toMake(wood.getWood(), 2).build().type(RecipeType.MISC).register();
			shapeless().add(wood.getStair(), 2).toMake(wood.getPlanks(), 3).build().type(RecipeType.MISC).register();
			surround(sandpaper).with(wood.getLog()).toMake(wood.getStrippedLog(), 8).build().type(RecipeType.MISC).register();
			surround(sandpaper).with(wood.getWood()).toMake(wood.getStrippedWood(), 8).build().type(RecipeType.MISC).register();
		}

		dyeStation();
		light();

		invisibleItemFrame();
	}

	private void dyeStation() {
		if (true) return;

		// Magic Dye
		shapeless().add(Material.GLASS_BOTTLE, Material.RED_DYE, Material.ORANGE_DYE, Material.YELLOW_DYE,
			Material.GREEN_DYE, Material.CYAN_DYE, Material.BLUE_DYE, Material.PURPLE_DYE, Material.PINK_DYE)
			.toMake(DyeStation.getMagicDye().build()).build().type(RecipeType.FUNCTIONAL).register();

		// Magic Stain
		shapeless().add(Material.GLASS_BOTTLE, Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS,
			Material.DARK_OAK_PLANKS, Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.BIRCH_SAPLING, Material.DARK_OAK_SAPLING)
			.toMake(DyeStation.getMagicStain().build()).build().type(RecipeType.FUNCTIONAL).register();

		// Dye Station
		shaped("111", "232", "242")
			.add('1', Material.WHITE_WOOL)
			.add('2', new RecipeChoice.MaterialChoice(Tag.PLANKS))
			.add('3', DyeStation.getMagicDye().build())
			.add('4', DyeStation.getMagicStain().build())
			.toMake(DyeStation.getDyeStation().build())
			.build().type(RecipeType.FUNCTIONAL).register();
	}

	private void light() {
		List<ItemStack> centerItems = getInvisPotions();
		if (centerItems == null)
			return;

		surround(centerItems).with(Material.GLOWSTONE).toMake(Material.LIGHT).build().type(RecipeType.FUNCTIONAL).register();
	}

	private void invisibleItemFrame() {
		List<ItemStack> centerItems = getInvisPotions();
		if (centerItems == null)
			return;

		surround(centerItems)
			.with(Material.ITEM_FRAME)
			.toMake(new ItemBuilder(Material.ITEM_FRAME).name("Invisible Item Frame").amount(8).glow().build())
			.build()
			.type(RecipeType.FUNCTIONAL);
		// No .register() to prevent overriding the recipe of the plugin
	}

	@Nullable
	private List<ItemStack> getInvisPotions() {
		final YamlConfiguration config = IOUtils.getConfig("plugins/SurvivalInvisiframes/config.yml");
		List<ItemStack> centerItems = (List<ItemStack>) config.getList("recipe-center-items");
		if (isNullOrEmpty(centerItems))
			return null;
		return centerItems;
	}

	public static String getItemName(ItemStack result) {
		return stripColor(ItemUtils.getName(result).replaceAll(" ", "_").trim().toLowerCase());
	}

	// Stolen from https://github.com/ezeiger92/QuestWorld2/blob/70f2be317daee06007f89843c79b3b059515d133/src/main/java/com/questworld/extension/builtin/CraftMission.java
	@EventHandler
	public void fixCraftEvent(CraftItemEvent event) {
		if (event instanceof FixedCraftItemEvent) return;

		ItemStack item = event.getRecipe().getResult().clone();
		ClickType click = event.getClick();

		int recipeAmount = item.getAmount();

		switch (click) {
			case NUMBER_KEY:
				if (event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null)
					recipeAmount = 0;
				break;

			case DROP:
			case CONTROL_DROP:
				ItemStack cursor = event.getCursor();
				if (!isNullOrAir(cursor))
					recipeAmount = 0;
				break;

			case SHIFT_RIGHT:
			case SHIFT_LEFT:
				if (recipeAmount == 0)
					break;

				int maxCraftable = getMaxCraftAmount(event.getInventory());
				int capacity = fits(item, event.getView().getBottomInventory());

				if (capacity < maxCraftable)
					maxCraftable = ((capacity + recipeAmount - 1) / recipeAmount) * recipeAmount;

				recipeAmount = maxCraftable;
				break;
			default:
		}

		if (recipeAmount == 0)
			return;

		item.setAmount(recipeAmount);

		new FixedCraftItemEvent(item, event.getRecipe(), event.getView(), event.getSlotType(), event.getSlot(), event.getClick(), event.getAction(), event.getHotbarButton()).callEvent();
	}

	public static int getMaxCraftAmount(CraftingInventory inv) {
		if (inv.getResult() == null)
			return 0;

		int resultCount = inv.getResult().getAmount();
		int materialCount = Integer.MAX_VALUE;

		for (ItemStack item : inv.getMatrix())
			// this can in fact be null
			if (item != null && item.getAmount() < materialCount)
				materialCount = item.getAmount();

		return resultCount * materialCount;
	}

	public static int fits(ItemStack stack, Inventory inv) {
		ItemStack[] contents = inv.getContents();
		int result = 0;

		for (ItemStack item : contents)
			if (item == null)
				result += stack.getMaxStackSize();
			else if (item.isSimilar(stack))
				result += Math.max(stack.getMaxStackSize() - item.getAmount(), 0);

		return result;
	}


}
