package gg.projecteden.nexus.features.recipes.functionals;

import com.destroystokyo.paper.ParticleBuilder;
import de.tr7zw.nbtapi.NBT;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

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
		ItemStack tool = ItemUtils.getTool(player, getCustomMaterial());
		if (!Nullables.isNullOrAir(item)) {
			if (shouldRotate(event)) {
				if (glued) {
					event.setCancelled(true);
					return;
				}

				if (!Nullables.isNullOrAir(tool)) {
					event.setCancelled(true);
					glue(itemFrame, player, tool);
					return;
				}

				return;
			}

			if (canOpenContainer(player)) {
				// Let ContainerPassThrough handle the event
				return;
			}

			if (glued) {
				event.setCancelled(true);
				return;
			}
		}

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
		glue(itemFrame, player, tool);
	}

	private void glue(ItemFrame itemFrame, Player player, ItemStack glue) {
		NBT.modifyPersistentData(itemFrame, nbt -> {
			nbt.setBoolean(NBT_KEY, true);
		});
		ItemUtils.subtract(player, glue);

		new SoundBuilder(Sound.ITEM_HONEYCOMB_WAX_ON).location(itemFrame).play();

		BlockFace facing = itemFrame.getAttachedFace();
		Location frameLoc = itemFrame.getLocation().toCenterLocation();
		frameLoc.add(facing.getModX() * 0.4, facing.getModY() * 0.4, facing.getModZ() * 0.4);

		new ParticleBuilder(Particle.COMPOSTER).location(frameLoc)
			.offset(0.2, 0.2, 0.2).extra(0.1).count(7).spawn();
	}

	private static final Set<EntityType> passthroughEntities = Set.of(EntityType.PAINTING, EntityType.ITEM_FRAME);

	private static boolean canOpenContainer(Player player) {
		RayTraceResult result = player.rayTraceBlocks(5.0, FluidCollisionMode.NEVER);
		if (result == null || result.getHitBlock() == null || !(result.getHitBlock().getState() instanceof Container)) {
			return false;
		}

		return true;
	}

	private static boolean shouldRotate(PlayerInteractEntityEvent event) {
		return event.getPlayer().isSneaking()
			|| !passthroughEntities.contains(event.getRightClicked().getType())
			|| event.getHand() == EquipmentSlot.OFF_HAND;
	}
}
