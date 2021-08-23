package gg.projecteden.nexus.features.recipes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils.ItemStackComparator;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.potion.PotionEffectType;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Depends(ResourcePack.class)
public class CustomRecipes extends Feature implements Listener {

	@Getter
	public static List<NexusRecipe> recipes = new ArrayList<>();

	@Override
	public void onStart() {
		Nexus.registerListener(this);
		registerDyes();
		registerSlabs();
		registerQuartz();
		registerStoneBricks();
		misc();

		new Reflections(getClass().getPackage().getName()).getSubTypesOf(FunctionalRecipe.class).stream()
			.map(clazz -> {
				try {
					if (!Utils.canEnable(clazz))
						return null;

					return clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					Nexus.log("Error while enabling functional recipe " + clazz.getSimpleName());
					e.printStackTrace();
					return null;
				}
			})
			.filter(obj -> Objects.nonNull(obj) && obj.getResult() != null)
			.sorted((recipe1, recipe2) -> new ItemStackComparator().compare(recipe1.getResult(), recipe2.getResult()))
			.forEach(recipe -> {
				recipe.setType(RecipeType.FUNCTIONAL);
				recipe.register();
				recipes.add(recipe);
			});
	}

	public static void register(Recipe recipe) {
		try {
			if (recipe == null)
				return;

			for (Recipe recipe1 : Bukkit.getServer().getRecipesFor(recipe.getResult()))
				if (RecipeUtils.areEqual(recipe, recipe1))
					return;

			Tasks.sync(() -> {
				try {
					Bukkit.addRecipe(recipe);
				} catch (IllegalStateException duplicate) {
					Nexus.log(duplicate.getMessage());
				} catch (Exception ex) {
					Nexus.log("Error while adding custom recipe " + ((Keyed) recipe).getKey() + " to Bukkit");
					ex.printStackTrace();
				}
			});
		} catch (Exception ex) {
			Nexus.log("Error while adding custom recipe " + ((Keyed) recipe).getKey());
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

	public void registerDyes() {
		String[] colors = {"WHITE", "BLACK", "BLUE", "BROWN", "CYAN", "GREEN", "GRAY", "LIGHT_BLUE", "LIGHT_GRAY",
				"LIME", "MAGENTA", "ORANGE", "PINK", "PURPLE", "RED", "YELLOW"};

		RecipeChoice.MaterialChoice concretePowder = new RecipeChoice.MaterialChoice(MaterialTag.CONCRETE_POWDERS.toArray());
		RecipeChoice.MaterialChoice stainedGlass = new RecipeChoice.MaterialChoice(MaterialTag.STAINED_GLASS.toArray());
		RecipeChoice.MaterialChoice stainedGlassPane = new RecipeChoice.MaterialChoice(MaterialTag.STAINED_GLASS_PANES.toArray());
		RecipeChoice.MaterialChoice terracotta = new RecipeChoice.MaterialChoice(MaterialTag.COLORED_TERRACOTTAS.toArray());
		RecipeChoice.MaterialChoice beds = new RecipeChoice.MaterialChoice(MaterialTag.BEDS.toArray());
		RecipeChoice.MaterialChoice banners = new RecipeChoice.MaterialChoice(MaterialTag.ITEMS_BANNERS.getValues().toArray(new Material[0]));
		for (String color : colors) {
			NexusRecipe.surround(new ItemStack(Material.valueOf(color + "_CONCRETE_POWDER"), 8), Material.valueOf(color + "_DYE"), concretePowder)
					.type(RecipeType.DYES).register();
			NexusRecipe.surround(new ItemStack(Material.valueOf(color + "_STAINED_GLASS"), 8), Material.valueOf(color + "_DYE"), stainedGlass)
					.type(RecipeType.DYES).register();
			NexusRecipe.surround(new ItemStack(Material.valueOf(color + "_STAINED_GLASS_PANE"), 8), Material.valueOf(color + "_DYE"), stainedGlassPane)
					.type(RecipeType.DYES).register();
			NexusRecipe.surround(new ItemStack(Material.valueOf(color + "_TERRACOTTA"), 8), Material.valueOf(color + "_DYE"), terracotta)
					.type(RecipeType.DYES).register();
			NexusRecipe.shapeless(new ItemStack(Material.valueOf(color + "_BED")), Material.valueOf(color + "_DYE"), beds).type(RecipeType.BEDS).register();
			NexusRecipe.shapeless(new ItemStack(Material.valueOf(color + "_BANNER")), Material.valueOf(color + "_DYE"), banners).type(RecipeType.DYES).register();
		}
	}

	public void registerSlabs() {
		Material[] slabs = MaterialTag.SLABS.getValues().toArray(new Material[0]);

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
			NexusRecipe.shapeless(new ItemStack(blockMaterial, 2), "slabs", slabsGroup.toArray(Material[]::new)).type(RecipeType.SLABS).register();
		}
	}

	public void registerQuartz() {
		NexusRecipe.shapeless(new ItemStack(Material.QUARTZ, 4), "quartz_uncrafting", Material.QUARTZ_BLOCK).type(RecipeType.QUARTZ).register();
		NexusRecipe.shapeless(new ItemStack(Material.QUARTZ_BLOCK, 1), "quartz_uncrafting", Material.QUARTZ_PILLAR).type(RecipeType.QUARTZ).register();
		NexusRecipe.shapeless(new ItemStack(Material.QUARTZ_SLAB, 2), "quartz_uncrafting", Material.CHISELED_QUARTZ_BLOCK).type(RecipeType.QUARTZ).register();
		NexusRecipe.shapeless(new ItemStack(Material.QUARTZ_BLOCK, 4), "quartz_uncrafting_bricks", Material.QUARTZ_BRICKS).type(RecipeType.QUARTZ).register();
	}

	public void registerStoneBricks() {
		NexusRecipe.shapeless(new ItemStack(Material.STONE, 1), "stonebrick_uncrafting", Material.STONE_BRICKS).type(RecipeType.STONE_BRICK).register();
		NexusRecipe.shapeless(new ItemStack(Material.STONE_BRICK_SLAB, 2), "stonebrick_uncrafting", Material.CHISELED_STONE_BRICKS).type(RecipeType.STONE_BRICK).register();
		NexusRecipe.shapeless(new ItemStack(Material.STONE_BRICKS), "stonebrick_uncrafting", Material.MOSSY_STONE_BRICKS).type(RecipeType.STONE_BRICK).register();
		NexusRecipe.shapeless(new ItemStack(Material.COBBLED_DEEPSLATE_SLAB, 2), "stonebrick-uncrafting", Material.CHISELED_DEEPSLATE).type(RecipeType.STONE_BRICK).register();
		NexusRecipe.shapeless(new ItemStack(Material.DEEPSLATE_BRICKS), "stonebrick-uncrafting", Material.DEEPSLATE_TILES).type(RecipeType.STONE_BRICK).register();
		NexusRecipe.shapeless(new ItemStack(Material.POLISHED_DEEPSLATE), "stonebrick-uncrafting", Material.DEEPSLATE_BRICKS).type(RecipeType.STONE_BRICK).register();
		NexusRecipe.shapeless(new ItemStack(Material.COBBLED_DEEPSLATE), "stonebrick-uncrafting", Material.POLISHED_DEEPSLATE).type(RecipeType.STONE_BRICK).register();
	}

	public void misc() {
		NexusRecipe.surround(new ItemStack(Material.WHITE_WOOL, 8), Material.WATER_BUCKET, new RecipeChoice.MaterialChoice(MaterialTag.WOOL.toArray())).type(RecipeType.WOOL).register();
		NexusRecipe.shapeless(new ItemStack(Material.NETHER_WART, 9), Material.NETHER_WART_BLOCK).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.PACKED_ICE, 9), Material.BLUE_ICE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.ICE, 9), Material.PACKED_ICE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.RED_SANDSTONE_SLAB, 2), Material.CHISELED_RED_SANDSTONE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.CHISELED_SANDSTONE, 2), Material.CHISELED_SANDSTONE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.GLOWSTONE_DUST, 3), Material.GLOWSTONE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.BLAZE_ROD), Material.BLAZE_POWDER, Material.BLAZE_POWDER).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.POINTED_DRIPSTONE, 4), Material.DRIPSTONE_BLOCK).type(RecipeType.MISC).register();

		// Invis Item Frame, No .register() to prevent overriding the recipe of the plugin
		NexusRecipe.surround(new ItemBuilder(Material.ITEM_FRAME).name("Invisible Item Frame").amount(8).glow().build(),
			new ItemBuilder(Material.LINGERING_POTION).potionEffect(PotionEffectType.INVISIBILITY).name("Lingering Invisibility Potion").build(),
			new RecipeChoice.MaterialChoice(Material.ITEM_FRAME)).type(RecipeType.FUNCTIONAL);
	}

}
