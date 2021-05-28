package me.pugabyte.nexus.features.recipes;

import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.recipes.models.FunctionalRecipe;
import me.pugabyte.nexus.features.recipes.models.NexusRecipe;
import me.pugabyte.nexus.features.recipes.models.RecipeType;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tasks;
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
import java.util.Set;

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

		Set<Class<? extends FunctionalRecipe>> functionals = new Reflections(getClass().getPackage().getName()).getSubTypesOf(FunctionalRecipe.class);

		// Need to wait for ResourcePack feature to register
		// TODO Create dependency system for features
		Tasks.wait(1, () -> functionals.forEach(clazz -> {
			try {
				FunctionalRecipe recipe = clazz.newInstance();
				recipe.setType(RecipeType.FUNCTIONAL);
				recipe.register();
				recipes.add(recipe);
			} catch (InstantiationException | IllegalAccessException e) {
				Nexus.log("Error while enabling functional recipe " + clazz.getSimpleName());
				e.printStackTrace();
			}
		}));
	}

	public NexusRecipe getCraftByRecipe(Recipe result) {
		return recipes.stream().filter(nexusRecipe ->
				((Keyed) nexusRecipe.getRecipe()).getKey().equals(((Keyed) result).getKey())).findFirst().orElse(null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreCraft(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player)) return;
		if (event.getRecipe() == null) return;
		NexusRecipe recipe = getCraftByRecipe(event.getRecipe());
		if (recipe == null) return;
		if (recipe.getPermission() != null && !event.getView().getPlayer().hasPermission(recipe.getPermission()))
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
		}
	}

	public void registerSlabs() {
		Material[] blocks = {Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS, Material.JUNGLE_PLANKS, Material.ACACIA_PLANKS, Material.DARK_OAK_PLANKS,
				Material.STONE, Material.SANDSTONE, Material.COBBLESTONE, Material.BRICKS, Material.STONE_BRICKS, Material.NETHER_BRICKS, Material.QUARTZ_BLOCK,
				Material.RED_SANDSTONE, Material.PURPUR_BLOCK, Material.PRISMARINE, Material.PRISMARINE_BRICKS, Material.DARK_PRISMARINE};

		for (Material block : blocks) {
			Material slab = Material.valueOf(block.name()
					.replace("BRICKS", "BRICK")
					.replace("_PLANKS", "")
					.replace("_BLOCK", "") + "_SLAB");
			List<Material> slabs = new ArrayList<>();
			for (int i = 0; i < 4; i++)
				slabs.add(slab);
			NexusRecipe.shapeless(new ItemStack(block, 2), "slabs", slabs.toArray(Material[]::new)).type(RecipeType.SLABS).register();
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
	}

	public void misc() {
		NexusRecipe.surround(new ItemStack(Material.WHITE_WOOL, 8), Material.WATER_BUCKET, new RecipeChoice.MaterialChoice(MaterialTag.WOOL.toArray())).type(RecipeType.WOOL).register();
		NexusRecipe.shapeless(new ItemStack(Material.NETHER_WART, 9), Material.NETHER_WART_BLOCK).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.PACKED_ICE, 9), Material.BLUE_ICE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.ICE, 9), Material.PACKED_ICE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.RED_SANDSTONE_SLAB, 2), Material.CHISELED_RED_SANDSTONE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.CHISELED_SANDSTONE, 2), Material.CHISELED_SANDSTONE).type(RecipeType.MISC).register();
		// Invis Item Frame, No .register() to prevent overriding the recipe of the plugin
		NexusRecipe.surround(new ItemBuilder(Material.ITEM_FRAME).name("Invisible Item Frame").amount(8).glow().build(),
				new ItemBuilder(Material.LINGERING_POTION).potionEffect(PotionEffectType.INVISIBILITY).name("Lingering Invisibility Potion").build(),
				new RecipeChoice.MaterialChoice(Material.ITEM_FRAME)).type(RecipeType.FUNCTIONAL);
	}
}
