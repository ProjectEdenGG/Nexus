package gg.projecteden.nexus.features.resourcepack.decoration.types.craftable;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.recipes.models.builders.ShapedBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.CraftableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.CeilingThing;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.ambience.AmbienceConfig.Ambience.AmbienceType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class WindChime extends CeilingThing implements CraftableDecoration {
	@Getter
	private final WindChimeType type;

	public WindChime(String name, WindChimeType type) {
		super(false, name, type.getItemModelType());
		this.type = type;
	}

	public static Set<String> ids() {
		return Arrays.stream(WindChimeType.values())
			.map(type -> type.getItemModelType().getModel())
			.collect(Collectors.toSet());
	}

	@Getter
	@AllArgsConstructor
	public enum WindChimeType {
		IRON(Material.IRON_INGOT, ItemModelType.WINDCHIMES_IRON),
		GOLD(Material.GOLD_INGOT, ItemModelType.WINDCHIMES_GOLD),
		COPPER(Material.COPPER_INGOT, ItemModelType.WINDCHIMES_COPPER),
		AMETHYST(Material.AMETHYST_SHARD, ItemModelType.WINDCHIMES_AMETHYST),
		LAPIS(Material.LAPIS_LAZULI, ItemModelType.WINDCHIMES_LAPIS),
		NETHERITE(Material.NETHERITE_INGOT, ItemModelType.WINDCHIMES_NETHERITE),
		DIAMOND(Material.DIAMOND, ItemModelType.WINDCHIMES_DIAMOND),
		REDSTONE(Material.REDSTONE, ItemModelType.WINDCHIMES_REDSTONE),
		EMERALD(Material.EMERALD, ItemModelType.WINDCHIMES_EMERALD),
		QUARTZ(Material.QUARTZ, ItemModelType.WINDCHIMES_QUARTZ),
		COAL(Material.COAL, ItemModelType.WINDCHIMES_COAL),
		ICE(Material.ICE, ItemModelType.WINDCHIMES_ICE),
		;

		private final Material ingot;
		private final ItemModelType itemModelType;

		public static @Nullable WindChimeType of(ItemStack item) {
			if (Nullables.isNullOrAir(item))
				return null;

			String modelId = Model.of(item);
			Material material = item.getType();

			return Arrays.stream(WindChimeType.values())
				.filter(type -> Objects.equals(type.itemModelType.getModel(), modelId))
				.filter(type -> type.itemModelType.getMaterial() == material)
				.findFirst()
				.orElse(null);
		}
	}

	public static boolean isWindchime(ItemStack item) {
		return WindChimeType.of(item) != null;
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
		return type.getItemModelType().getNamedItem();
	}

	@Override
	public RecipeGroup getGroup() {
		return new RecipeGroup(2, "Windchimes", new ItemBuilder(ItemModelType.WINDCHIMES_AMETHYST).build());
	}

	static {
		Nexus.registerListener(new WindChimesListener());
	}

	private static class WindChimesListener implements Listener {

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
