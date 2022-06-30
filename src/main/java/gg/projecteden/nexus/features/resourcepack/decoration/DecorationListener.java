package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationModifyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.debug;
import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.error;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class DecorationListener implements Listener {

	public DecorationListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(DecorationPlaceEvent e) {
		debug(e.getPlayer(), e.getEventName() + " - Place");
	}

	@EventHandler
	public void on(DecorationDestroyEvent e) {
		debug(e.getPlayer(), e.getEventName() + " - Destroy");
	}

	@EventHandler
	public void on(DecorationInteractEvent e) {
		debug(e.getPlayer(), e.getEventName() + " - Interact");
	}

	@EventHandler
	public void on(DecorationSitEvent e) {
		debug(e.getPlayer(), e.getEventName() + " - Sit");
	}

	@EventHandler
	public void on(DecorationModifyEvent e) {
		debug(e.getPlayer(), e.getEventName() + " - Modify");
	}

	@EventHandler
	public void on(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getDismounted() instanceof ArmorStand armorStand)) return;
		if (Seat.isSeat(armorStand)) {
			event.getDismounted().remove();
			player.teleport(player.getLocation().add(0, 1, 0));
		}
	}

	// TODO: allow staff/builders to bypass? Maybe only allow players who are in creative mode?
	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		EquipmentSlot slot = event.getHand();
		if (slot != EquipmentSlot.HAND)
			return;

		// TODO: Remove
		if (!isWakkaOrGriffin(event.getPlayer()))
			return;
		//

		Player player = event.getPlayer();
		ItemStack tool = ItemUtils.getTool(player);

		DecorationType toolType = DecorationType.of(tool);
		boolean playerHoldingDecor = toolType != null;

		Entity entity = event.getRightClicked();
		if (entity instanceof ItemFrame itemFrame) {
			ItemStack frameItem = itemFrame.getItem();
			boolean frameHoldingItem = !Nullables.isNullOrAir(frameItem);

			DecorationType frameType = DecorationType.of(frameItem);
			boolean frameHoldingDecor = frameType != null;

			if (frameHoldingItem && !frameHoldingDecor)
				return;

			if (!frameHoldingItem) {
				if (!playerHoldingDecor)
					return;

				// cancel trying to place decoration into item frame
				event.setCancelled(true);
			} else {
				final Decoration decoration = new Decoration(frameType.getConfig(), itemFrame);
				DecorationModifyEvent modifyEvent = new DecorationModifyEvent(player, decoration, tool);
				if (!modifyEvent.callEvent())
					event.setCancelled(true);

				return;
			}

		}

		if (!playerHoldingDecor)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player))
			return;

		if (!(event.getEntity() instanceof ItemFrame itemFrame))
			return;

		if (event.isCancelled()) {
			debug(player, "entity damage event was cancelled");
			return;
		}

		ItemStack itemStack = itemFrame.getItem();
		if (isNullOrAir(itemStack))
			return;

		DecorationType type = DecorationType.of(itemStack);
		if (type == null)
			return;

		DecorationInteractData data = new DecorationInteractData.DecorationInteractDataBuilder()
			.player(player)
			.decoration(new Decoration(type.getConfig(), itemFrame))
			.tool(ItemUtils.getTool(player))
			.build();

		boolean cancel = destroy(data);
		if (cancel)
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		EquipmentSlot slot = event.getHand();
		if (slot != EquipmentSlot.HAND)
			return;

		Block clicked = event.getClickedBlock();
		if (clicked == null)
			return;

		Player player = event.getPlayer();
		if (isCancelled(event)) {
			debug(player, "player interact event was cancelled");
			return;
		}

		final ItemStack tool = ItemUtils.getTool(player);
		DecorationInteractData data = new DecorationInteractData.DecorationInteractDataBuilder()
			.player(player)
			.block(clicked)
			.blockFace(event.getBlockFace())
			.tool(tool)
			.build();

		boolean cancel = false;
		Action action = event.getAction();
		if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			if (isNullOrAir(tool))
				cancel = interact(data);
			else
				cancel = place(data);
		} else if (action.equals(Action.LEFT_CLICK_BLOCK)) {
			cancel = destroy(data);
		}

		if (cancel)
			event.setCancelled(true);
	}

	boolean destroy(DecorationInteractData data) {
		// TODO: Remove
		if (!isWakkaOrGriffin(data.getPlayer()))
			return false;
		//

		if (!data.validate())
			return false;

		if (!data.playerCanEdit()) {
			error(data.getPlayer());
			return false;
		}

		if (!isNullOrAir(data.getTool())) {
			debug(data.getPlayer(), "holding something, returning");
			return true;
		}

		if (isOnCooldown(data.getPlayer(), DecorationAction.DESTROY)) {
			debug(data.getPlayer(), "slow down");
			return true;
		}

		data.destroy();
		return true;
	}

	private boolean interact(DecorationInteractData data) {
		if (data == null)
			return false;

		if (!data.validate())
			return false;

		if (isOnCooldown(data.getPlayer(), DecorationAction.INTERACT)) {
			debug(data.getPlayer(), "slow down");
			return true;
		}

		data.interact();
		return true;
	}

	private boolean place(DecorationInteractData data) {
		// TODO: Remove
		if (!isWakkaOrGriffin(data.getPlayer()))
			return false;
		//

		final DecorationType type = DecorationType.of(data.getTool());
		if (type == null)
			return false;

		data.setDecoration(new Decoration(type.getConfig(), null));

		if (isOnCooldown(data.getPlayer(), DecorationAction.PLACE)) {
			debug(data.getPlayer(), "slow down");
			return true;
		}

		if (!data.playerCanEdit()) {
			error(data.getPlayer());
			return true;
		}

		data.place();
		return true;
	}

	private boolean isCancelled(PlayerInteractEvent event) {
		return event.useInteractedBlock() == Result.DENY || event.useInteractedBlock() == Result.DENY;
	}

	private enum DecorationAction {
		INTERACT,
		PLACE,
		DESTROY,
	}

	private boolean isOnCooldown(Player player, DecorationAction action) {
		return !new CooldownService().check(player, "decoration-" + action.name().toLowerCase(), TickTime.TICK.x(5));
	}

	private boolean isWakkaOrGriffin(Player player) {
//		return true;
		return Dev.WAKKA.is(player) || Dev.GRIFFIN.is(player);
	}
}
