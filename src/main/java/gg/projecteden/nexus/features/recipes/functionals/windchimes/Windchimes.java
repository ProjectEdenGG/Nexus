package gg.projecteden.nexus.features.recipes.functionals.windchimes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.RecipeGroup;
import gg.projecteden.nexus.features.recipes.models.RecipeType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.ambience.AmbienceConfig.Ambience.AmbienceType;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;
import static gg.projecteden.nexus.utils.MathUtils.isBetween;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static java.util.stream.Collectors.toSet;

public abstract class Windchimes extends FunctionalRecipe {
	private static final int CUSTOM_MODEL_DATA_START = CustomMaterial.WINDCHIMES_IRON.getModelId();
	private static final int CUSTOM_MODEL_DATA_END = CUSTOM_MODEL_DATA_START + 19;

	@Getter
	@AllArgsConstructor
	public enum WindchimeType {
		IRON(Material.IRON_INGOT),
		GOLD(Material.GOLD_INGOT),
		COPPER(Material.COPPER_INGOT),
		AMETHYST(Material.AMETHYST_SHARD),
		LAPIS(Material.LAPIS_LAZULI),
		NETHERITE(Material.NETHERITE_INGOT),
		DIAMOND(Material.DIAMOND),
		REDSTONE(Material.REDSTONE),
		EMERALD(Material.EMERALD),
		QUARTZ(Material.QUARTZ),
		COAL(Material.COAL),
		ICE(Material.ICE),
		;

		private final Material ingot;

		public static Set<Integer> ids() {
			return Arrays.stream(WindchimeType.values())
				.map(windchimeType -> windchimeType.ordinal() + CUSTOM_MODEL_DATA_START)
				.collect(toSet());
		}

		public ItemStack getItem() {
			return new ItemBuilder(Material.PAPER)
				.name(camelCase(this) + " Windchimes")
				.modelId(ordinal() + CUSTOM_MODEL_DATA_START)
				.build();
		}
	}

	@Override
	public ItemStack getResult() {
		return getWindchimeType().getItem();
	}

	abstract WindchimeType getWindchimeType();

	@Override
	public @NotNull Recipe getRecipe() {
		return shaped("111", "222", "343")
			.add('1', Material.STICK)
			.add('2', Material.CHAIN)
			.add('3', getWindchimeType().getIngot())
			.add('4', MaterialTag.WOOD_BUTTONS)
			.toMake(getResult())
			.getRecipe();
	}

	@Override
	public RecipeType getRecipeType() {
		return RecipeType.DECORATION;
	}

	@Override
	public RecipeGroup getGroup() {
		return new RecipeGroup(2, "Windchimes", new ItemBuilder(CustomMaterial.WINDCHIMES_AMETHYST).build());
	}

	public static boolean isWindchime(ItemStack item) {
		if (isNullOrAir(item))
			return false;

		if (!item.getType().equals(Material.PAPER))
			return false;

		if (!isBetween(ModelId.of(item), CUSTOM_MODEL_DATA_START, CUSTOM_MODEL_DATA_END))
			return false;

		return true;
	}

	static {
		Nexus.registerListener(new WindchimesListener());
	}

	private static class WindchimesListener implements Listener {

		@EventHandler
		public void onClickWindchime(PlayerInteractEvent event) {
			Player player = event.getPlayer();

			if (player.isSneaking())
				return;
			if (!EquipmentSlot.HAND.equals(event.getHand()))
				return;
			if (!ActionGroup.RIGHT_CLICK.applies(event))
				return;

			ItemStack itemStack = null;
			Location location = null;
			ItemFrame itemFrame = PlayerUtils.getTargetItemFrame(player, 4, Map.of(BlockFace.UP, 1));
			if (itemFrame == null) {
				Block clickedBlock = event.getClickedBlock();
				if (isNullOrAir(clickedBlock))
					return;

				Location _location = clickedBlock.getRelative(event.getBlockFace()).getLocation();
				for (var entity : ClientSideConfig.getEntities(_location)) {
					if (entity.getType() == ClientSideEntityType.ITEM_FRAME) {
						ClientSideItemFrame clientSideItemFrame = (ClientSideItemFrame) entity;
						itemStack = clientSideItemFrame.content();
						location = clientSideItemFrame.location();
						break;
					}
				}
			} else {
				itemStack = itemFrame.getItem();
				location = itemFrame.getLocation();
			}


			if (isNullOrAir(itemStack))
				return;

			if (!isWindchime(itemStack))
				return;

			event.setCancelled(true);
			event.getPlayer().swingMainHand();
			AmbienceType.METAL_WINDCHIMES.play(location);
		}

		@EventHandler
		public void onInteractItemFrame(PlayerInteractEntityEvent event) {
			Player player = event.getPlayer();

			if (player.isSneaking())
				return;
			if (!EquipmentSlot.HAND.equals(event.getHand()))
				return;

			Entity clicked = event.getRightClicked();
			if (!(clicked instanceof ItemFrame itemFrame))
				return;
			if (!isWindchime(itemFrame.getItem()))
				return;

			event.setCancelled(true);
			event.getPlayer().swingMainHand();
			AmbienceType.METAL_WINDCHIMES.play(itemFrame.getLocation());
		}
	}

}
