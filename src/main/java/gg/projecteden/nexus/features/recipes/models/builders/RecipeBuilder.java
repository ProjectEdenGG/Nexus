package gg.projecteden.nexus.features.recipes.models.builders;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.recipes.CustomRecipes;
import gg.projecteden.nexus.features.recipes.models.NexusRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.recipes.CustomRecipes.choiceOf;
import static gg.projecteden.nexus.features.recipes.CustomRecipes.keyOf;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public abstract class RecipeBuilder<T extends RecipeBuilder<?>> {
	protected ItemStack result;
	protected List<ItemStack> unlockedByList = new ArrayList<>();

	protected List<String> ingredientIds = new ArrayList<>();
	protected String resultId = "__to_make__";

	public T toMake(CustomBlock result) {
		this.resultId += keyOf(result);
		this.result = result.get().getItemStack();
		return (T) this;
	}

	public T toMake(CustomBlock result, int amount) {
		this.resultId += amount + "_" + keyOf(result);
		this.result = new ItemBuilder(result.get().getItemStack()).amount(amount).build();
		return (T) this;
	}

	public T toMake(Material result) {
		this.resultId += keyOf(result);
		this.result = new ItemStack(result);
		return (T) this;
	}

	public T toMake(Material result, int amount) {
		this.resultId += amount + "_" + keyOf(result);
		this.result = new ItemStack(result, amount);
		return (T) this;
	}

	public T toMake(ItemStack result) {
		this.resultId += keyOf(result);
		this.result = result;
		return (T) this;
	}

	public T toMake(CustomMaterial result) {
		this.resultId += keyOf(result);
		this.result = new ItemBuilder(result).build();
		return (T) this;
	}

	public T toMake(CustomMaterial result, int amount) {
		this.resultId += amount + "_" + keyOf(result);
		this.result = new ItemBuilder(result).build();
		return (T) this;
	}

	public T toMake(ItemStack result, int amount) {
		this.resultId += keyOf(result, amount);
		this.result = new ItemBuilder(result).amount(amount).build();
		return (T) this;
	}

	public T toMake(CustomModel result) {
		this.resultId += keyOf(result);
		this.result = result.getItem();
		return (T) this;
	}

	public T unlockedBy(CustomBlock customBlock) {
		this.unlockedByList.add(customBlock.get().getItemStack());
		return (T) this;
	}

	public T unlockedByCustomBlocks(CustomBlock... customBlocks) {
		return unlockedByCustomBlocks(List.of(customBlocks));
	}

	public T unlockedByCustomBlocks(List<CustomBlock> customBlocks) {
		for (CustomBlock customBlock : customBlocks)
			this.unlockedByList.add(customBlock.get().getItemStack());

		return (T) this;
	}

	public T unlockedBy(Material material) {
		this.unlockedByList.add(new ItemStack(material));
		return (T) this;
	}

	public T unlockedByMaterials(Material... materials) {
		return unlockedByMaterials(List.of(materials));
	}


	public T unlockedByMaterials(List<Material> materials) {
		for (Material material : materials) {
			this.unlockedByList.add(new ItemStack(material));
		}

		return (T) this;
	}

	public T unlockedBy(ItemStack itemStack) {
		if (!Nullables.isNullOrAir(itemStack))
			this.unlockedByList.add(itemStack);

		return (T) this;
	}

	public T unlockedByItems(ItemStack... items) {
		return unlockedByItems(List.of(items));
	}

	public T unlockedByItems(List<ItemStack> items) {
		for (ItemStack item : items) {
			if (!Nullables.isNullOrAir(item))
				this.unlockedByList.add(new ItemStack(item));
		}

		return (T) this;
	}

	protected String getKey() {
		return String.join("__and__", ingredientIds) + resultId;
	}

	@NotNull
	abstract <R extends Recipe> R getRecipe();

	public void register(RecipeType type) {
		build().register(type);
	}

	public void register(RecipeType type, RecipeGroup group) {
		build().register(type, group);
	}

	public void register() {
		build().register();
	}

	public NexusRecipe build() {
		NexusRecipe recipe = new NexusRecipe(getRecipe(), unlockedByList);
		CustomRecipes.recipes.put(key(), recipe);
		return recipe;
	}

	@NotNull
	protected NamespacedKey key() {
		return new NamespacedKey(Nexus.getInstance(), "custom__" + stripColor(getKey())
			.trim()
			.toLowerCase()
			.replaceAll(" ", "_")
			.replaceAll("[^a-z0-9/._-]", ""));
	}

	public static ShapedBuilder shaped(String... pattern) {
		return new ShapedBuilder(pattern);
	}

	public static ShapelessBuilder shapeless() {
		return new ShapelessBuilder();
	}

	public static ShapelessBuilder shapeless(Material... ingredients) {
		return shapeless().add(ingredients);
	}

	public static ShapelessBuilder shapeless(ItemStack... items) {
		return shapeless().add(items);
	}

	public static ShapelessBuilder shapeless(Material ingredient, int count) {
		return shapeless().add(ingredient, count);
	}

	public static ShapelessBuilder shapeless(ItemStack item, int count) {
		return shapeless().add(item, count);
	}

	public static ShapelessBuilder shapeless(CustomModel... items) {
		return shapeless().add(items);
	}

	public static ShapelessBuilder shapeless(Tag<Material> tag) {
		return shapeless().add(tag);
	}

	public static ShapelessBuilder shapeless(RecipeChoice ingredient) {
		return shapeless().add(ingredient);
	}

	public static SurroundBuilder surround(CustomBlockTag center) {
		final SurroundBuilder builder = surround(choiceOf(center));
		builder.ingredientIds.add(keyOf(center));
		return builder;
	}

	public static SurroundBuilder surround(MaterialTag center) {
		final SurroundBuilder builder = surround(choiceOf(center));
		builder.ingredientIds.add(keyOf(center));
		return builder;
	}

	public static SurroundBuilder surround(Material center) {
		final SurroundBuilder builder = surround(choiceOf(center));
		builder.ingredientIds.add(keyOf(center));
		return builder;
	}

	public static SurroundBuilder surround(ItemStack center) {
		final SurroundBuilder builder = surround(choiceOf(center));
		builder.ingredientIds.add(keyOf(center));
		return builder;
	}

	public static SurroundBuilder surround(List<?> choices) {
		final SurroundBuilder builder = surround(choiceOf(choices));

		for (Object choice : choices) {
			if (choice instanceof ItemStack item)
				builder.ingredientIds.add(keyOf(item));
			else if (choice instanceof Material material)
				builder.ingredientIds.add(keyOf(material));
			else if (choice instanceof CustomMaterial material)
				builder.ingredientIds.add(keyOf(material));
			else if (choice instanceof Keyed keyed)
				builder.ingredientIds.add(keyOf(keyed));
			else {
				Nexus.warn("[SurroundRecipeBuilder] Unsupported type " + choice.getClass().getSimpleName());
				builder.ingredientIds.add("unknown");
			}
		}

		return builder;
	}

	protected static SurroundBuilder surround(RecipeChoice center) {
		return new SurroundBuilder(center);
	}

	public static FurnaceBuilder smelt(Material smelt) {
		return new FurnaceBuilder(smelt);
	}

	public static FurnaceBuilder blast(Material smelt) {
		return new BlastFurnaceBuilder(smelt);
	}

	public static StoneCutterBuilder stoneCutter(Material material) {
		return new StoneCutterBuilder(material);
	}
}
