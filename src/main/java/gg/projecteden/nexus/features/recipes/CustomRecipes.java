package gg.projecteden.nexus.features.recipes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils.ItemStackComparator;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@Depends({ResourcePack.class, CustomEnchants.class})
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

	@NotNull
	private MaterialChoice choiceOf(MaterialTag tag) {
		return new MaterialChoice(tag.toArray());
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
			MaterialTag.BANNERS
		);

		for (ColorType color : ColorType.getDyes()) {
			final Material dye = color.switchColor(Material.WHITE_DYE);

			Consumer<NexusRecipe> register = recipe -> recipe.type(RecipeType.DYES).register();

			surround.forEach(tag ->
				register.accept(NexusRecipe.surround(new ItemStack(color.switchColor(tag.first()), 8), dye, choiceOf(tag))));

			shapeless.forEach(tag ->
				register.accept(NexusRecipe.shapeless(new ItemStack(color.switchColor(tag.first())), dye, choiceOf(tag))));
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
		NexusRecipe.surround(new ItemStack(Material.WHITE_WOOL, 8), Material.WATER_BUCKET, choiceOf(MaterialTag.WOOL)).type(RecipeType.WOOL).register();
		NexusRecipe.shapeless(new ItemStack(Material.NETHER_WART, 9), Material.NETHER_WART_BLOCK).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.PACKED_ICE, 9), Material.BLUE_ICE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.ICE, 9), Material.PACKED_ICE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.RED_SANDSTONE_SLAB, 2), Material.CHISELED_RED_SANDSTONE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.CHISELED_SANDSTONE, 2), Material.CHISELED_SANDSTONE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.GLOWSTONE_DUST, 3), Material.GLOWSTONE).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.BLAZE_ROD), Material.BLAZE_POWDER, Material.BLAZE_POWDER).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.POINTED_DRIPSTONE, 4), Material.DRIPSTONE_BLOCK).type(RecipeType.MISC).register();
		NexusRecipe.shapeless(new ItemStack(Material.HONEYCOMB, 4), Material.HONEYCOMB_BLOCK).type(RecipeType.MISC).register();

		// Invis Item Frame, No .register() to prevent overriding the recipe of the plugin
		NexusRecipe.surround(new ItemBuilder(Material.ITEM_FRAME).name("Invisible Item Frame").amount(8).glow().build(),
			new ItemBuilder(Material.LINGERING_POTION).potionEffect(PotionEffectType.INVISIBILITY).name("Lingering Invisibility Potion").build(),
			new RecipeChoice.MaterialChoice(Material.ITEM_FRAME)).type(RecipeType.FUNCTIONAL);
	}

}
