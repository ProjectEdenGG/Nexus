package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Set;

public class ContainerPassthrough implements Listener {

	private static final Set<EntityType> PASSTHROUGH_ENTITIES = new HashSet<>();
	private static final Set<Material> PASSTHROUGH_BLOCKS = new HashSet<>();

	private boolean ignoreInteractEvents = false;

	static {
		PASSTHROUGH_ENTITIES.add(EntityType.PAINTING);
		PASSTHROUGH_ENTITIES.add(EntityType.ITEM_FRAME);

		PASSTHROUGH_BLOCKS.addAll(MaterialTag.WALL_SIGNS.getValues());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		if (event.getPlayer().isSneaking()) return;
		if (!PASSTHROUGH_ENTITIES.contains(event.getRightClicked().getType())) return;

		if (!tryOpeningContainerRaytrace(event.getPlayer()))
			return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (ignoreInteractEvents) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		if (event.getClickedBlock() == null) return;
		if (!PASSTHROUGH_BLOCKS.contains(event.getClickedBlock().getType())) return;

		if (event.getPlayer().isSneaking()) {
			handleSneak(event);
			return;
		}

		Block behind = event.getClickedBlock().getRelative(event.getBlockFace().getOppositeFace());
		if (!(behind.getState() instanceof Container container)) return;
		if (!canOpenContainer(event.getPlayer(), behind, event.getBlockFace())) return;

		tryOpeningContainer(event.getPlayer(), container);
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		HumanEntity player = event.getPlayer();

		if (!player.hasMetadata("doublechest-open")) return;

		DoubleChest doubleChest = (DoubleChest) event.getInventory().getHolder();
		closeDoubleChest(doubleChest, player);
		player.removeMetadata("doublechest-open", Nexus.getInstance());
	}

	private boolean canOpenContainer(Player player, Block block, BlockFace face) {
		ignoreInteractEvents = true;
		PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), block, face, EquipmentSlot.HAND);
		event.callEvent();
		ignoreInteractEvents = false;
		return event.useInteractedBlock() != Result.DENY;
	}

	private boolean tryOpeningContainerRaytrace(Player player) {
		RayTraceResult result = player.rayTraceBlocks(5.0, FluidCollisionMode.NEVER);

		if (result == null) return false;
		if (result.getHitBlock() == null) return false;
		if (!(result.getHitBlock().getState() instanceof Container container)) return false;

		if (canOpenContainer(player, result.getHitBlock(), result.getHitBlockFace()))
			tryOpeningContainer(player, container);

		return true;
	}

	private void tryOpeningContainer(Player player, Container container) {
		if (player.getOpenInventory().getTopInventory().equals(container.getInventory())) return;

		player.openInventory(container.getInventory());

		if (container.getInventory() instanceof DoubleChestInventory)
			player.setMetadata("doublechest-open", new FixedMetadataValue(Nexus.getInstance(), true));
	}

	private void closeDoubleChest(DoubleChest doubleChest, HumanEntity player) {
		Chest left = (Chest) doubleChest.getLeftSide();
		Chest right = (Chest) doubleChest.getRightSide();

		Tasks.wait(4, () -> {
			CraftHumanEntity human = (CraftHumanEntity) player;
			((CraftChest) left).getBlockEntity().onClose(human);
			((CraftChest) right).getBlockEntity().onClose(human);
		});
	}

	private void handleSneak(PlayerInteractEvent event) {
		if (Nullables.isNullOrAir(event.getItem()))
			return;

		this.ignoreInteractEvents = true;
		event.getPlayer().setSneaking(false);
		Location loc = event.getClickedBlock().getLocation();
		BlockHitResult result = new BlockHitResult(new Vec3(loc.x(), loc.y(), loc.z()), Direction.valueOf(event.getBlockFace().name()), NMSUtils.toNMS(loc), false);
		ServerboundUseItemOnPacket packet = new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, result, 0);
		packet.handle(((CraftPlayer) event.getPlayer()).getHandle().connection);
		event.getPlayer().setSneaking(true);
		this.ignoreInteractEvents = false;
	}

}
