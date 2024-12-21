package gg.projecteden.nexus.features.recipes;

import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.listeners.events.FixedCraftItemEvent;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.workbenches.CustomBench;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Depends;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.CopperState.CopperBlockType;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.ItemUtils.ItemStackComparator;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.api.common.utils.ReflectionUtils.subTypesOf;
import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.*;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.pretty;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Depends({ResourcePack.class, CustomEnchants.class})
public class CustomRecipes extends Feature implements Listener {

	@Getter
	public static Map<NamespacedKey, NexusRecipe> recipes = new ConcurrentHashMap<>();

	private static boolean loaded;

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent ignored) {
		if (loaded)
			return;

		loaded = true;

		Tasks.async(() -> {
			List<Runnable> registers = List.of(
				this::registerDyes,
				this::registerSlabs,
				this::registerQuartz,
				this::registerStoneBricks,
				this::registerFurnace,
				this::misc
			);

			for (Runnable runnable : registers) {
				try {
					runnable.run();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

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
					try {
						recipe.setType(recipe.getRecipeType());
						recipe.register();
						recipes.put(recipe.getKey(), recipe);
					} catch (Exception ex) {
						System.out.println("Error registering FunctionalRecipe " + recipe.getClass().getSimpleName());
						ex.printStackTrace();
					}
				});
		});
	}

	public static void register(NexusRecipe recipe) {
		if (recipe == null)
			return;

		try {
			for (Recipe recipe1 : Bukkit.getServer().getRecipesFor(recipe.getResult()))
				if (RecipeUtils.areEqual(recipe.getRecipe(), recipe1)) {
					Nexus.debug(recipe.getKey().getKey() + " == " + ((Keyed) recipe1).getKey().getKey());
					return;
				}

			Tasks.sync(() -> {
				try {
					Bukkit.addRecipe(recipe.getRecipe());
				} catch (IllegalStateException duplicate) {
					Nexus.log(duplicate.getMessage());
				} catch (Exception ex) {
					Nexus.log("Error while adding custom recipe " + recipe.getKey() + " to Bukkit");
					ex.printStackTrace();
				}
			});
		} catch (Exception ex) {
			Nexus.log("Error while adding custom recipe " + recipe.getKey());
			ex.printStackTrace();
		}
	}

	public NexusRecipe getCraftByRecipe(Recipe result) {
		return recipes.get(((Keyed) result).getKey());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreCraft(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		if (event.getRecipe() == null)
			return;

		NexusRecipe recipe = getCraftByRecipe(event.getRecipe());
		if (recipe == null)
			return;

		if (recipe.getPermission() != null && !player.hasPermission(recipe.getPermission()))
			event.getInventory().setResult(null);

		else if (recipe.getResult().hasItemMeta())
			event.getInventory().setResult(recipe.getResult());

		player.discoverRecipe(((Keyed) event.getRecipe()).getKey());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCraft(CraftItemEvent event) {
		NexusRecipe recipe = getCraftByRecipe(event.getRecipe());
		if (recipe == null)
			return;

		if (recipe.getPermission() == null)
			return;

		if (!event.getWhoClicked().hasPermission(recipe.getPermission()))
			event.setCancelled(true);

		final Player player = (Player) event.getWhoClicked();
		player.discoverRecipe(((Keyed) event.getRecipe()).getKey());
	}

	@EventHandler
	public void on(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		unlockRecipe(player, event.getItem().getItemStack());
	}

	@EventHandler
	public void on(InventoryClickEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		final Inventory inventory = event.getClickedInventory();
		if (inventory == null || inventory.getType() == InventoryType.PLAYER)
			return;

		final ItemStack item = player.getItemOnCursor();
		if (isNullOrAir(item))
			return;

		unlockRecipe(player, item);
	}

	private static void unlockRecipe(Player player, ItemStack eventItem) {
		for (NexusRecipe recipe : new ArrayList<>(CustomRecipes.getRecipes().values()))
			unlockRecipe(player, eventItem, recipe);
	}

	private static void unlockRecipe(Player player, ItemStack eventItem, NexusRecipe recipe) {
		List<ItemStack> unlockItems = recipe.getUnlockedByList();
		if (unlockItems.isEmpty())
			return;

		Keyed keyedRecipe = (Keyed) recipe.getRecipe();
		NamespacedKey key = keyedRecipe.getKey();
		if (player.hasDiscoveredRecipe(key))
			return;

		for (ItemStack unlockItem : unlockItems) {
			if (Nullables.isNullOrAir(eventItem) || Nullables.isNullOrAir(unlockItem))
				continue;

			if (!ItemUtils.isFuzzyMatch(eventItem, unlockItem))
				continue;

			player.discoverRecipe(key);
			return;
		}
	}

	@NotNull
	public static RecipeChoice choiceOf(CustomBlockTag tag) {
		return choiceOf(tag.getValues().stream().map(customBlock -> customBlock.get().getItemStack()).toList());
	}

	@NotNull
	public static RecipeChoice choiceOf(Tag<Material> tag) {
		return new MaterialChoice(tag.getValues().toArray(new Material[0]));
	}

	@NotNull
	public static RecipeChoice choiceOf(Material... material) {
		return new MaterialChoice(material);
	}

	@NotNull
	public static RecipeChoice choiceOf(CustomBlock customBlock) {
		return new ExactChoice(customBlock.get().getItemStack());
	}

	@NotNull
	public static RecipeChoice choiceOf(ItemStack... items) {
		return new ExactChoice(items);
	}

	public static RecipeChoice choiceOf(CustomMaterial material) {
		return new MaterialChoice(material.getMaterial());
	}

	@NonNull
	public static RecipeChoice choiceOf(List<?> choices) {
		if (Nullables.isNullOrEmpty(choices))
			throw new InvalidInputException("Recipe choices cannot be empty");

		final Object object = choices.get(0);
		if (object instanceof Material)
			return new MaterialChoice((List<Material>) choices);
		else if (object instanceof ItemStack)
			return new ExactChoice((List<ItemStack>) choices);
		else if (object instanceof CustomMaterial)
			return new ExactChoice(choices.stream().map(customMaterial -> ((CustomMaterial) customMaterial).getItem()).toList());
		else if (object instanceof CustomBlock)
			return new ExactChoice(choices.stream().map(customBlock -> ((CustomBlock) customBlock).get().getItemStack()).toList());
		else if (object instanceof ICustomBlock)
			return new ExactChoice(choices.stream().map(customBlock -> ((ICustomBlock) customBlock).getItemStack()).toList());
		else
			throw new InvalidInputException("Unrecognized recipe choice class " + object.getClass().getSimpleName());
	}

	public static String keyOf(Keyed keyed) {
		return keyed.getKey().getKey();
	}

	public static String keyOf(Material material) {
		return material.name();
	}

	public static String keyOf(CustomMaterial material) {
		return material.name();
	}

	public static String keyOf(ItemStack item) {
		return pretty(item);
	}

	public static String keyOf(ItemStack item, int amount) {
		return pretty(item, amount);
	}

	public static String keyOf(CustomModel item) {
		return pretty(item.getItem());
	}

	public void registerDyes() {
		final Map<MaterialTag, RecipeGroup> surround = new LinkedHashMap<>() {{
			put(MaterialTag.CONCRETE_POWDERS, new RecipeGroup(1, "Concrete Powders", new ItemStack(Material.CYAN_CONCRETE_POWDER)));
			put(MaterialTag.CONCRETES, new RecipeGroup(2, "Concretes", new ItemStack(Material.YELLOW_CONCRETE)));
			put(MaterialTag.STAINED_GLASS, new RecipeGroup(3, "Stained Glass", new ItemStack(Material.CYAN_STAINED_GLASS)));
			put(MaterialTag.STAINED_GLASS_PANES, new RecipeGroup(4, "Stained Glass Panes", new ItemStack(Material.YELLOW_STAINED_GLASS_PANE)));
			put(MaterialTag.COLORED_TERRACOTTAS, new RecipeGroup(5, "Terracottas", new ItemStack(Material.TERRACOTTA)));
		}};

		final Map<MaterialTag, RecipeGroup> shapeless = new LinkedHashMap<>() {{
			put(MaterialTag.BEDS, new RecipeGroup(1, "Beds", new ItemStack(Material.CYAN_BED)));
			put(MaterialTag.STANDING_BANNERS, new RecipeGroup(2, "Banners", new ItemStack(Material.YELLOW_BANNER)));
		}};

		RecipeGroup concrete = new RecipeGroup(1, "Concrete", new ItemStack(Material.CYAN_CONCRETE));
		for (ColorType color : ColorType.getDyes()) {
			final Material dye = color.switchColor(Material.WHITE_DYE);

			surround.keySet().forEach(tag -> surround(dye).with(tag).toMake(color.switchColor(tag.first()), 8).register(RecipeType.DYES, surround.get(tag)));
			shapeless.keySet().forEach(tag -> shapeless(dye).add(tag).toMake(color.switchColor(tag.first())).register(RecipeType.BEDS_BANNERS, shapeless.get(tag)));
			surround(Material.WATER_BUCKET).with(color.getConcretePowder()).toMake(color.getConcrete(), 8).register(RecipeType.CONCRETES, concrete);
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
				} catch (IllegalArgumentException ignore) {
				}
			}

			blockMaterial = switch (slab) {
				case QUARTZ_SLAB -> Material.QUARTZ_BLOCK;
				case DEEPSLATE_TILE_SLAB -> Material.DEEPSLATE_TILES;
				case BAMBOO_SLAB -> Material.BAMBOO_PLANKS;
				default -> blockMaterial;
			};

			if (blockMaterial == null) continue;

			shaped("11", "11").add('1', slab).toMake(blockMaterial, 2).register(RecipeType.SLABS);
		}
	}

	public void registerQuartz() {
		shapeless(Material.QUARTZ_BLOCK).toMake(Material.QUARTZ, 4).register(RecipeType.QUARTZ);
		shapeless(Material.QUARTZ_PILLAR).toMake(Material.QUARTZ_BLOCK).register(RecipeType.QUARTZ);
		shapeless(Material.CHISELED_QUARTZ_BLOCK).toMake(Material.QUARTZ_SLAB, 2).register(RecipeType.QUARTZ);
		shapeless(Material.QUARTZ_BRICKS).toMake(Material.QUARTZ_BLOCK).register(RecipeType.QUARTZ);
	}

	public void registerStoneBricks() {
		shapeless(Material.STONE_BRICKS).toMake(Material.STONE).register(RecipeType.STONE_BRICK);
		shapeless(Material.CHISELED_STONE_BRICKS).toMake(Material.STONE_BRICK_SLAB, 2).register(RecipeType.STONE_BRICK);
		shapeless(Material.MOSSY_STONE_BRICKS).toMake(Material.STONE_BRICKS).register(RecipeType.STONE_BRICK);
		shapeless(Material.CHISELED_DEEPSLATE).toMake(Material.COBBLED_DEEPSLATE_SLAB, 2).register(RecipeType.STONE_BRICK);
		shapeless(Material.DEEPSLATE_TILES).toMake(Material.DEEPSLATE_BRICKS).register(RecipeType.STONE_BRICK);
		shapeless(Material.DEEPSLATE_BRICKS).toMake(Material.POLISHED_DEEPSLATE).register(RecipeType.STONE_BRICK);
		shapeless(Material.POLISHED_DEEPSLATE).toMake(Material.COBBLED_DEEPSLATE).register(RecipeType.STONE_BRICK);
	}

	private void registerFurnace() {
		smelt(Material.RAW_COPPER_BLOCK).toMake(Material.COPPER_BLOCK).exp(6.3f).time(1200).build().register();
		smelt(Material.RAW_IRON_BLOCK).toMake(Material.IRON_BLOCK).exp(6.3f).time(1200).build().register();
		smelt(Material.RAW_GOLD_BLOCK).toMake(Material.GOLD_BLOCK).exp(9f).time(1200).build().register();

		blast(Material.RAW_COPPER_BLOCK).toMake(Material.COPPER_BLOCK).exp(6.3f).time(600).build().hideFromMenu().register();
		blast(Material.RAW_IRON_BLOCK).toMake(Material.IRON_BLOCK).exp(6.3f).time(600).build().hideFromMenu().register();
		blast(Material.RAW_GOLD_BLOCK).toMake(Material.GOLD_BLOCK).exp(9f).time(600).build().hideFromMenu().register();
	}

	public void misc() {
		shaped("SLS", "L L", "LLL").add('S', Material.STRING).add('L', Material.LEATHER).toMake(Material.BUNDLE).register(RecipeType.MISC);

		surround(Material.WATER_BUCKET).with(MaterialTag.WOOL).toMake(Material.WHITE_WOOL, 8).register(RecipeType.WOOL);
		surround(Material.BLACKSTONE).with(Material.GOLD_NUGGET).toMake(Material.GILDED_BLACKSTONE).register(RecipeType.MISC);
		surround(Material.WATER_BUCKET).with(MaterialTag.MUDABLE_DIRT).toMake(Material.MUD, 8).register(RecipeType.MISC);

		shapeless(Material.DROPPER).add(Material.BOW).toMake(Material.DISPENSER).register(RecipeType.MISC);
		shapeless(Material.NETHER_WART_BLOCK).toMake(Material.NETHER_WART, 9).register(RecipeType.MISC);
		shapeless(Material.BLUE_ICE).toMake(Material.PACKED_ICE, 9).register(RecipeType.MISC);
		shapeless(Material.PACKED_ICE).toMake(Material.ICE, 9).register(RecipeType.MISC);
		shapeless(Material.CHISELED_RED_SANDSTONE).toMake(Material.RED_SANDSTONE_SLAB, 2).register(RecipeType.MISC);
		shapeless(Material.CHISELED_SANDSTONE).toMake(Material.SANDSTONE_SLAB, 2).register(RecipeType.MISC);
		shapeless(Material.GLOWSTONE).toMake(Material.GLOWSTONE_DUST, 3).register(RecipeType.MISC);
		shapeless(Material.BLAZE_POWDER, Material.BLAZE_POWDER).toMake(Material.BLAZE_ROD).register(RecipeType.MISC);
		shapeless(Material.DRIPSTONE_BLOCK).toMake(Material.POINTED_DRIPSTONE, 4).register(RecipeType.MISC);
		shapeless(Material.HONEYCOMB_BLOCK).toMake(Material.HONEYCOMB, 4).register(RecipeType.MISC);
		shapeless(Material.MELON).toMake(Material.MELON_SLICE, 5).register(RecipeType.MISC);
		shapeless(MaterialTag.WOOL).toMake(Material.STRING, 4).register(RecipeType.WOOL);
		shapeless(Material.PRISMARINE).toMake(Material.PRISMARINE_SHARD, 4).register(RecipeType.MISC);
		shapeless(Material.PRISMARINE_BRICKS).toMake(Material.PRISMARINE_SHARD, 9).register(RecipeType.MISC);
		shapeless(Material.MOSS_CARPET, 3).toMake(Material.MOSS_BLOCK, 2).register(RecipeType.MISC);
		shapeless(Material.SAND).add(Material.PAPER).toMake(CustomMaterial.SAND_PAPER.getNamedItem()).register(RecipeType.MISC);
		shapeless(Material.RED_SAND).add(Material.PAPER).toMake(CustomMaterial.RED_SAND_PAPER.getNamedItem()).register(RecipeType.MISC);
		shapeless(Material.SADDLE).toMake(Material.LEATHER, 4).register(RecipeType.MISC);
		shapeless(Material.CHEST).toMake(Material.BARREL).register(RecipeType.MISC);
		shapeless(Material.CLAY).toMake(Material.CLAY_BALL, 4).register(RecipeType.MISC);
		shapeless(Material.AMETHYST_BLOCK).toMake(Material.AMETHYST_SHARD, 4).register(RecipeType.MISC);
		shapeless(Material.BONE_MEAL, 3).toMake(Material.BONE).register(RecipeType.MISC);

		for (CopperState state : CopperState.values())
			if (state.hasNext())
				for (CopperBlockType blockType : CopperState.CopperBlockType.values())
					surround(Material.WATER_BUCKET).with(blockType.of(state)).toMake(blockType.of(state.next()), 8).register(RecipeType.COPPER);

		RecipeGroup carpets = new RecipeGroup(1, "Carpets", new ItemStack(Material.CYAN_CARPET));
		RecipeGroup concretePowders = new RecipeGroup(2, "Concrete Powders", new ItemStack(Material.YELLOW_CONCRETE_POWDER));
		for (ColorType color : ColorType.getDyes()) {
			shapeless(color.getCarpet(), 3).toMake(color.getWool(), 2).register(RecipeType.WOOL, carpets);
			shapeless(color.getConcrete(), 2).toMake(color.getConcretePowder(), 2).register(RecipeType.CONCRETES, concretePowders);
		}

		RecipeGroup logs = new RecipeGroup(1, "Logs", new ItemStack(Material.OAK_LOG));
		RecipeGroup woods = new RecipeGroup(2, "Wood", new ItemStack(Material.OAK_WOOD));
		RecipeGroup planks = new RecipeGroup(3, "Planks from Stairs", new ItemStack(Material.OAK_PLANKS));
		RecipeGroup strippedLogs = new RecipeGroup(4, "Stripped Logs from Logs", new ItemStack(Material.STRIPPED_OAK_LOG));
		RecipeGroup strippedLogs2 = new RecipeGroup(5, "Stripped Logs from Wood", new ItemStack(Material.STRIPPED_OAK_LOG));
		final List<ItemStack> sandpaper = List.of(CustomMaterial.SAND_PAPER.getNamedItem(), CustomMaterial.RED_SAND_PAPER.getNamedItem());
		for (WoodType wood : WoodType.values()) {
			shapeless(wood.getStrippedLog(), 2).toMake(wood.getLog(), 2).register(RecipeType.WOOD, logs);
			shapeless(wood.getStrippedWood(), 2).toMake(wood.getWood(), 2).register(RecipeType.WOOD, woods);
			shapeless(wood.getStair(), 2).toMake(wood.getPlanks(), 3).register(RecipeType.WOOD, planks);
			surround(sandpaper).with(wood.getLog()).toMake(wood.getStrippedLog(), 8).register(RecipeType.WOOD, strippedLogs);
			surround(sandpaper).with(wood.getWood()).toMake(wood.getStrippedWood(), 8).register(RecipeType.WOOD, strippedLogs2);
		}

		shapeless(Material.STRIPPED_BAMBOO_BLOCK, 2).toMake(Material.BAMBOO_BLOCK, 2).register(RecipeType.WOOD, logs);
		shapeless(Material.BAMBOO_STAIRS, 2).toMake(Material.BAMBOO_PLANKS, 3).register(RecipeType.WOOD, planks);
		shapeless(Material.BAMBOO_MOSAIC_STAIRS, 2).toMake(Material.BAMBOO_MOSAIC, 3).register(RecipeType.WOOD, planks);
		surround(sandpaper).with(Material.BAMBOO_BLOCK).toMake(Material.STRIPPED_BAMBOO_BLOCK, 8).register(RecipeType.WOOD, strippedLogs);

		CustomBench.registerRecipes();
		DecorationType.registerRecipes();

		light();
		invisibleItemFrame();
	}

	private void light() {
		List<ItemStack> centerItems = getInvisPotions();
		if (centerItems == null)
			return;

		surround(centerItems).with(Material.GLOWSTONE).toMake(Material.LIGHT, 4).register(RecipeType.FUNCTIONAL);
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

	@EventHandler
	public void on(PrepareItemCraftEvent event) {
		if (!(event.getRecipe() instanceof Keyed keyed))
			return;

		if (!keyed.getKey().getNamespace().equalsIgnoreCase("minecraft"))
			return;

		for (ItemStack item : event.getInventory().getMatrix())
			if (ModelId.of(item) != 0 && !(CustomMaterial.of(item) != null && CustomMaterial.of(item).isAllowedInVanillaRecipes()))
				event.getInventory().setResult(new ItemStack(Material.AIR));
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
