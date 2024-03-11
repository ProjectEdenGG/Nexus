package gg.projecteden.nexus.features.resourcepack.decoration.types.craftable;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.recipes.models.builders.ShapedBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.CraftableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.MultiState;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
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

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public class BirdHouse extends DecorationConfig implements CraftableDecoration, MultiState {
	@Getter
	private final BirdHouseType type;
	private final CustomMaterial customMaterial;
	private final boolean craftable;

	public BirdHouse(String name, CustomMaterial customMaterial, boolean craftable) {
		super(false, name, customMaterial);
		this.customMaterial = customMaterial;
		this.type = BirdHouseType.valueOf(customMaterial.name().split("_")[1].toUpperCase());
		this.craftable = craftable;

	}

	public static Set<Integer> ids() {
		return DecorationConfig.getAllDecorationTypes().stream()
				.filter(config -> config instanceof BirdHouse)
				.map(DecorationConfig::getModelId)
				.collect(Collectors.toSet());
	}

	public static boolean isBirdHouse(ItemStack item) {
		return DecorationConfig.of(item) instanceof BirdHouse;
	}

	@Override
	public CustomMaterial getDroppedMaterial() {
		return this.type.getBaseMaterial();
	}

	@Getter
	@AllArgsConstructor
	public enum BirdHouseType {
		FOREST(CustomMaterial.BIRDHOUSE_FOREST_HORIZONTAL, Material.RED_TERRACOTTA, Material.DARK_OAK_PLANKS, Material.BIRCH_PLANKS),
		ENCHANTED(CustomMaterial.BIRDHOUSE_ENCHANTED_HORIZONTAL, Material.BLUE_TERRACOTTA, Material.DARK_PRISMARINE, Material.CYAN_CONCRETE_POWDER),
		DEPTHS(CustomMaterial.BIRDHOUSE_DEPTHS_HORIZONTAL, Material.GREEN_TERRACOTTA, Material.DEEPSLATE_TILES, Material.STONE),
		;

		private final CustomMaterial baseMaterial;
		private final Material roof, hole, siding;

		public String getName() {
			return camelCase(this) + " Birdhouse";
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
		return customMaterial.getItemBuilder().name(type.getName()).build();
	}

	@Override
	public RecipeGroup getGroup() {
		return new RecipeGroup(1, "Birdhouses", new ItemBuilder(CustomMaterial.BIRDHOUSE_FOREST_HORIZONTAL).build());
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

			int modelId = birdHouse.getType().getBaseMaterial().getModelId();
			BlockFace facing = event.getAttachedFace().getOppositeFace();

			if (facing == BlockFace.UP)
				modelId += 2;
			else if (facing != BlockFace.DOWN)
				modelId += 1;

			event.setItem(new ItemBuilder(event.getItem()).modelId(modelId).build());

			switch (facing) {
				case NORTH, SOUTH, EAST, WEST -> event.setRotation(ItemFrameRotation.DEGREE_0);
			}
		}
	}
}
