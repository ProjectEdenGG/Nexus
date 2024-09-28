package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.RayTraceResult;

import java.util.Set;

// These methods are not available through an API
// https://github.com/techchrism/container-passthrough/tree/master
public class ContainerPassThrough {
	private static final Set<EntityType> passthroughEntities = Set.of(EntityType.PAINTING, EntityType.ITEM_FRAME);

	private static boolean canOpenContainer(Player player, Block block, BlockFace face) {
		// Send out an event to ensure container locking plugins aren't bypassed
		PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), block, face, EquipmentSlot.HAND);
		interactEvent.callEvent();

		return !(interactEvent.useInteractedBlock() == Event.Result.DENY || interactEvent.isCancelled());
	}

	public static boolean tryOpeningContainerRaytrace(Player player) {
		RayTraceResult result = player.rayTraceBlocks(5.0, FluidCollisionMode.NEVER);
		if (result == null || result.getHitBlock() == null || !(result.getHitBlock().getState() instanceof Container container)) {
			return false;
		}

		if (canOpenContainer(player, result.getHitBlock(), result.getHitBlockFace()))
			tryOpeningContainer(player, container);

		return true;
	}

	private static void tryOpeningContainer(Player player, Container container) {
		if (!player.getOpenInventory().getTopInventory().equals(container.getInventory())) {
			player.openInventory(container.getInventory());
			if (container.getInventory() instanceof DoubleChestInventory) {
				player.setMetadata("doublechest-open", new FixedMetadataValue(Nexus.getInstance(), true));
			}
		}
	}

	public static boolean shouldRotate(PlayerInteractEntityEvent event) {
		return event.getPlayer().isSneaking()
			|| !passthroughEntities.contains(event.getRightClicked().getType())
			|| event.getHand() == EquipmentSlot.OFF_HAND;
	}


}
