package gg.projecteden.nexus.features.resourcepack.decoration.types.special;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.CraftableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.MultiState;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.component.DataComponents;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("removal")
@Getter
public class Edible extends DecorationConfig implements MultiState, CraftableDecoration {
	private final EdibleType edibleType;
	private final int stage;

	public Edible(EdibleType edibleType, ItemModelType stageItemModelType, int stage) {
		super(false, edibleType.getName(), stageItemModelType);
		this.breakSound = Sound.BLOCK_WOOD_BREAK.getKey().getKey();

		this.edibleType = edibleType;
		this.stage = stage;
		this.disabledPlacements = PlacementType.FLOOR.getDisabledPlacements();

		if (this.stage > edibleType.getMaxServings())
			throw new InvalidInputException("Edible Decoration: " + name + " stage must be <= EdibleType#maxServings!");
	}

	@Override
	public boolean isCraftable() {
		return this.stage == 0;
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.FOOD;
	}

	@Override
	public ItemStack getResult() {
		return this.edibleType.getStage0().getItemBuilder().name(this.getName()).build();
	}

	@Override
	public RecipeBuilder<?> getRecipeBuilder() {
		return getEdibleType().getRecipeBuilder();
	}

	@Override
	public ItemModelType getBaseItemModel() {
		return this.edibleType.getStage0();
	}

	@Getter
	@AllArgsConstructor
	public enum EdibleType {
		//CAKE(null, 7, 14, 2.8, CustomMaterial.CAKE_SLICE, CustomMaterial.ITEM_WOODEN_PLATE), // These values are defined by vanilla
		//PUMPKIN_PIE(CustomMaterial.CUSTOM_PUMPKIN_PIE, 4, 8, 0.3, CustomMaterial.PUMPKIN_PIE_SLICE, CustomMaterial.ITEM_WOODEN_PLATE), // These values are defined by vanilla
		ROAST_CHICKEN("Roast Chicken", ItemModelType.ROAST_CHICKEN_STAGE_0, ItemModelType.ROAST_CHICKEN_SERVING, Material.BOWL, 4, 8, 18, null),
		;

		private final String name;
		private final ItemModelType stage0;
		private final ItemModelType servingItemModelType;
		private final ItemStack plateItem;
		private final int maxServings; // max stages
		private final int servingHunger;
		private final double servingSaturation;
		private final RecipeBuilder<?> recipeBuilder;

		EdibleType(String name, ItemModelType model, ItemModelType servingItemModelType, Material plateType, int servings, int servingHunger, double servingSaturation, RecipeBuilder<?> recipeBuilder) {
			this(name, model, servingItemModelType, new ItemStack(plateType), servings, servingHunger, servingSaturation, recipeBuilder);
		}

		EdibleType(String name, ItemModelType model, ItemModelType servingItemModelType, ItemModelType plateType, int servings, int servingHunger, double servingSaturation, RecipeBuilder<?> recipeBuilder) {
			this(name, model, servingItemModelType, new ItemBuilder(plateType).build(), servings, servingHunger, servingSaturation, recipeBuilder);
		}

		public static @Nullable EdibleType ofServingItem(ItemStack servingItem) {
			for (EdibleType type : values()) {
				if (type.getServingItemModelType().is(servingItem))
					return type;
			}
			return null;
		}

		public ItemStack getServingItem() {
			return new ItemBuilder(servingItemModelType).name("Serving of " + this.name).build();
		}

		public ItemModelType getModel(int stage) {
			String baseMaterialStr = getStage0().name().replaceAll("_STAGE_0", "");
			String nextItemModelStr = baseMaterialStr + "_STAGE_" + stage;
			ItemModelType itemModelType = ItemModelType.valueOf(nextItemModelStr);
			return ItemModelType.of(new ItemBuilder(getStage0()).model(itemModelType));
		}

		public void eat(Player player, Location soundOrigin, ItemStack originalItem) {
			int finalServingHunger = servingHunger;

			// Since the consume event isn't cancelled, subtract the hunger obtained from the original food item
			if (Nullables.isNullOrAir(originalItem)) {
				var nmsItem = NMSUtils.toNMS(originalItem);
				if (nmsItem.has(DataComponents.FOOD)) {
					var nmsFoodProperties = nmsItem.get(DataComponents.FOOD);
					finalServingHunger = Math.abs(finalServingHunger - nmsFoodProperties.nutrition());
				}
			}

			int playerFoodLevel = player.getFoodLevel();
			player.setFoodLevel(Math.min(20, playerFoodLevel + finalServingHunger));
			player.setSaturation((float) Math.min(playerFoodLevel, player.getSaturation() + servingSaturation - 0.4));
			new SoundBuilder(Sound.ENTITY_GENERIC_EAT).location(soundOrigin).category(SoundCategory.PLAYERS).play();
		}
	}

	private static boolean tryEat(Player player, Edible edible, Decoration decoration) {
		ItemStack tool = ItemUtils.getTool(player);
		if (ItemUtils.isModelMatch(edible.getEdibleType().getPlateItem(), tool)) {
			if (edible.getStage() < edible.getEdibleType().getMaxServings())
				edible.takeServing(player, tool, decoration.getOrigin());
			tryNextStage(player, edible, decoration);
			return true;
		}

		if (Nullables.isNotNullOrAir(tool))
			return false;

		if (GameModeWrapper.of(player).is(GameMode.SURVIVAL)) {
			if (player.getFoodLevel() >= 20)
				return false;
		}

		edible.getEdibleType().eat(player, decoration.getOrigin(), null);
		tryNextStage(player, edible, decoration);
		return true;
	}

	private void takeServing(Player player, ItemStack tool, Location soundOrigin) {
		tool.subtract();
		PlayerUtils.giveItem(player, edibleType.getServingItem());
		new SoundBuilder(Sound.ITEM_DYE_USE).location(soundOrigin).category(SoundCategory.PLAYERS).play();
	}

	private static void tryNextStage(Player player, Edible edible, Decoration decoration) {
		if (edible.getStage() > edible.getEdibleType().getMaxServings()) {
			breakEdible(player, decoration);
			return;
		}

		ItemFrame itemFrame = decoration.getItemFrame();
		if (itemFrame == null)
			return;

		ItemStack item = itemFrame.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		int nextStage = edible.getStage() + 1;
		if (nextStage > edible.getEdibleType().getMaxServings()) {
			breakEdible(player, decoration);
			return;
		}

		ItemBuilder itemBuilder = new ItemBuilder(item).material(edible.getEdibleType().getModel(nextStage));
		itemBuilder.resetName();

		itemFrame.setItem(itemBuilder.build(), false);
	}

	private static void breakEdible(Player player, Decoration decoration) {
		decoration.destroy(player, BlockFace.UP, player);
	}

	static {
		Nexus.registerListener(new EdibleDecorationListener());
	}

	public static class EdibleDecorationListener implements Listener {
		@EventHandler
		public void on(DecorationInteractEvent event) {
			if (event.isCancelled())
				return;

			Player player = event.getPlayer();
			if (player.isSneaking() || event.getInteractType() != InteractType.RIGHT_CLICK)
				return;

			if (!(event.getDecoration().getConfig() instanceof Edible edible))
				return;

			if (tryEat(player, edible, event.getDecoration()))
				event.setCancelled(true);
		}

		@EventHandler
		public void on(DecorationDestroyEvent event) {
			if (event.isCancelled())
				return;

			if (!(event.getDecoration().getConfig() instanceof Edible edible))
				return;

			int remainingServings = edible.getEdibleType().getMaxServings() - edible.getStage();

			List<ItemStack> drops = new ArrayList<>() {{
				add(new ItemStack(edible.getEdibleType().getPlateItem()));
				add(new ItemBuilder(Material.BONE_MEAL).amount(remainingServings + RandomUtils.randomInt(1, 3)).build());
			}};

			event.setDrops(drops);
			event.setIgnoreLocked(true);
		}

		@EventHandler
		public void on(PlayerItemConsumeEvent event) {
			EdibleType edibleType = EdibleType.ofServingItem(event.getItem());
			if (edibleType == null)
				return;

			Player player = event.getPlayer();
			edibleType.eat(player, player.getLocation(), event.getItem());
			PlayerUtils.giveItem(player, edibleType.getPlateItem());
		}
	}


}
