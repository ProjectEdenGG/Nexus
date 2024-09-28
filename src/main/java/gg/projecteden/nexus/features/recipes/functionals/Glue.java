package gg.projecteden.nexus.features.recipes.functionals;

import de.tr7zw.nbtapi.NBT;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ContainerPassThrough;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder.shaped;

public class Glue extends FunctionalRecipe {

	private static final String NBT_KEY = "Glued";

	public static CustomMaterial getCustomMaterial() {
		return CustomMaterial.GLUE;
	}

	public static CustomModel getCustomModel() {
		return getCustomMaterial().getCustomModel();
	}

	@Override
	public ItemStack getResult() {
		return getCustomModel().getItem();
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return shaped("12", "34")
			.add('1', Material.SLIME_BALL)
			.add('2', Material.PAPER)
			.add('3', Material.IRON_NUGGET)
			.add('4', Material.HONEYCOMB)
			.toMake(getResult())
			.getRecipe();
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		boolean glued = NBT.getPersistentData(itemFrame, nbt -> nbt.getBoolean(NBT_KEY));
		ItemStack item = itemFrame.getItem();
		if (!Nullables.isNullOrAir(item)) {
			if (ContainerPassThrough.shouldRotate(event)) {
				if (glued) {
					event.setCancelled(true);
					return;
				}
				return;
			}

			if (ContainerPassThrough.tryOpeningContainerRaytrace(player)) {
				event.setCancelled(true);
				return;
			}

			if (glued) {
				event.setCancelled(true);
				return;
			}
		}

		ItemStack tool = ItemUtils.getTool(player, getCustomMaterial());
		if (Nullables.isNullOrAir(tool)) {
			return;
		}

		if (glued) {
			return;
		}

		// Allow players to place glue in item frame without gluing if sneaking
		if (player.isSneaking()) {
			return;
		}

		event.setCancelled(true);
		NBT.modifyPersistentData(itemFrame, nbt -> {
			nbt.setBoolean(NBT_KEY, true);
		});
	}
}
