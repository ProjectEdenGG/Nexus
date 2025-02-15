package gg.projecteden.nexus.features.resourcepack.decoration.types.craftable;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.recipes.models.builders.ShapedBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.CraftableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.MultiState;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.stream.Collectors;

public class BirdHouse extends DecorationConfig implements CraftableDecoration, MultiState {
	@Getter
	private final BirdHouseType type;
	private final ItemModelType itemModelType;
	private final boolean craftable;

	public BirdHouse(String name, ItemModelType itemModelType, boolean craftable) {
		super(false, name, itemModelType);
		this.itemModelType = itemModelType;
		this.type = BirdHouseType.valueOf(itemModelType.name().split("_")[1].toUpperCase());
		this.craftable = craftable;

	}

	public static Set<String> ids() {
		return DecorationConfig.getALL_DECOR_CONFIGS().stream()
				.filter(config -> config instanceof BirdHouse)
				.map(DecorationConfig::getModel)
				.collect(Collectors.toSet());
	}

	public static boolean isBirdHouse(ItemStack item) {
		return DecorationConfig.of(item) instanceof BirdHouse;
	}

	@Override
	public ItemModelType getBaseItemModel() {
		return this.type.getBaseItemModelType();
	}

	@Getter
	@AllArgsConstructor
	public enum BirdHouseType {
		FOREST(ItemModelType.BIRDHOUSE_FOREST_HORIZONTAL, Material.RED_TERRACOTTA, Material.DARK_OAK_PLANKS, Material.BIRCH_PLANKS),
		ENCHANTED(ItemModelType.BIRDHOUSE_ENCHANTED_HORIZONTAL, Material.BLUE_TERRACOTTA, Material.DARK_PRISMARINE, Material.CYAN_CONCRETE_POWDER),
		DEPTHS(ItemModelType.BIRDHOUSE_DEPTHS_HORIZONTAL, Material.GREEN_TERRACOTTA, Material.DEEPSLATE_TILES, Material.STONE),
		;

		private final ItemModelType baseItemModelType;
		private final Material roof, hole, siding;

		public String getName() {
			return StringUtils.camelCase(this) + " Birdhouse";
		}
	}

	@Override
	public boolean isCraftable() {
		return this.craftable;
	}

	@Override
	public RecipeBuilder<?> getRecipeBuilder() {
		return new ShapedBuilder("111", "232", "444")
			.add('1', type.getRoof())
			.add('2', type.getHole())
			.add('3', Material.FEATHER)
			.add('4', type.getSiding());
	}

	@Override
	public ItemStack getResult() {
		return itemModelType.getItemBuilder().name(type.getName()).build();
	}

	@Override
	public RecipeGroup getGroup() {
		return new RecipeGroup(1, "Birdhouses", new ItemBuilder(ItemModelType.BIRDHOUSE_FOREST_HORIZONTAL).build());
	}

	static {
		Nexus.registerListener(new BirdHouseListener());
	}

	public static class BirdHouseListener implements Listener {
		@EventHandler
		public void on(DecorationPrePlaceEvent event) {
			Decoration decoration = event.getDecoration();
			if (!(decoration.getConfig() instanceof BirdHouse birdHouse))
				return;

			String modelId = birdHouse.getType().getBaseItemModelType().getModel();
			BlockFace facing = event.getAttachedFace().getOppositeFace();

			if (facing == BlockFace.UP)
				modelId = modelId.replace("horizontal", "hanging");
			else if (facing != BlockFace.DOWN)
				modelId = modelId.replace("horizontal", "vertical");

			event.setItem(new ItemBuilder(event.getItem()).model(modelId).build());

			switch (facing) {
				case NORTH, SOUTH, EAST, WEST -> event.setRotation(ItemFrameRotation.DEGREE_0);
			}
		}
	}
}
