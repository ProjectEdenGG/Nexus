package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.recipes.models.builders.ShapedBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.common.CraftableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.PlacementType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.ambience.AmbienceConfig.Ambience.AmbienceType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class WindChime extends DecorationConfig implements CraftableDecoration {
	private final WindChimeType type;

	public WindChime(String name, WindChimeType type) {
		super(name, type.getCustomMaterial());
		this.type = type;
		this.disabledPlacements = List.of(PlacementType.WALL, PlacementType.FLOOR);
	}

	@Getter
	@AllArgsConstructor
	public enum WindChimeType {
		IRON(Material.IRON_INGOT, CustomMaterial.WINDCHIMES_IRON),
		GOLD(Material.GOLD_INGOT, CustomMaterial.WINDCHIMES_GOLD),
		COPPER(Material.COPPER_INGOT, CustomMaterial.WINDCHIMES_COPPER),
		AMETHYST(Material.AMETHYST_SHARD, CustomMaterial.WINDCHIMES_AMETHYST),
		LAPIS(Material.LAPIS_LAZULI, CustomMaterial.WINDCHIMES_LAPIS),
		NETHERITE(Material.NETHERITE_INGOT, CustomMaterial.WINDCHIMES_NETHERITE),
		DIAMOND(Material.DIAMOND, CustomMaterial.WINDCHIMES_DIAMOND),
		REDSTONE(Material.REDSTONE, CustomMaterial.WINDCHIMES_REDSTONE),
		EMERALD(Material.EMERALD, CustomMaterial.WINDCHIMES_EMERALD),
		QUARTZ(Material.QUARTZ, CustomMaterial.WINDCHIMES_QUARTZ),
		COAL(Material.COAL, CustomMaterial.WINDCHIMES_COAL),
		ICE(Material.ICE, CustomMaterial.WINDCHIMES_ICE),
		;

		private final Material ingot;
		private final CustomMaterial customMaterial;

		public static @Nullable WindChimeType of(ItemStack itemStack) {
			if (Nullables.isNullOrAir(itemStack))
				return null;

			int modelId = ModelId.of(itemStack);
			Material material = itemStack.getType();

			return Arrays.stream(WindChimeType.values())
				.filter(type -> type.customMaterial.getModelId() == modelId)
				.filter(type -> type.customMaterial.getMaterial() == material)
				.findFirst()
				.orElse(null);
		}

		public static Set<Integer> ids() {
			return Arrays.stream(WindChimeType.values())
				.map(type -> type.getCustomMaterial().getModelId())
				.collect(toSet());
		}
	}

	@Override
	public RecipeBuilder<?> getRecipeBuilder() {
		return new ShapedBuilder("111", "222", "343")
			.add('1', Material.STICK)
			.add('2', Material.CHAIN)
			.add('3', type.getIngot())
			.add('4', MaterialTag.WOOD_BUTTONS);
	}

	@Override
	public ItemStack getResult() {
		return type.getCustomMaterial().getNamedItem();
	}

	@Override
	public RecipeGroup getGroup() {
		return new RecipeGroup(2, "Windchimes", new ItemBuilder(CustomMaterial.WINDCHIMES_AMETHYST).build());
	}

	public static boolean isWindchime(ItemStack item) {
		return WindChimeType.of(item) != null;
	}

	static {
		Nexus.registerListener(new WindchimesListener());
	}

	private static class WindchimesListener implements Listener {

		@EventHandler
		public void on(DecorationInteractEvent event) {
			Decoration decoration = event.getDecoration();
			if (!(decoration.getConfig() instanceof WindChime))
				return;

			Player player = event.getPlayer();
			if (player.isSneaking())
				return;

			if (event.getInteractType() != InteractType.RIGHT_CLICK)
				return;

			event.setCancelled(true);
			player.swingMainHand();
			AmbienceType.METAL_WINDCHIMES.play(decoration.getItemFrame().getLocation());
		}
	}
}
