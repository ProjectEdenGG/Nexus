package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.events.CreativePickBlockEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationCooldown;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationModifyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPaintEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlacedEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationRotateEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
import gg.projecteden.nexus.features.workbenches.dyestation.CreativeBrushMenu;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.debug;
import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@SuppressWarnings("deprecation")
public class DecorationListener implements Listener {

	public DecorationListener() {
		Nexus.registerListener(this);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationPrePlaceEvent e) {
		debug(e.getPlayer(), "&b" + e.getEventName() + " - PrePlace");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationPlacedEvent e) {
		debug(e.getPlayer(), "&b" + e.getEventName() + " - Placed");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationDestroyEvent e) {
		debug(e.getPlayer(), "&b" + e.getEventName() + " - Destroy");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationInteractEvent e) {
		debug(e.getPlayer(), "&b" + e.getEventName() + " - Interact");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationSitEvent e) {
		debug(e.getPlayer(), "&b" + e.getEventName() + " - Sit");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationModifyEvent e) {
		debug(e.getPlayer(), "&b" + e.getEventName() + " - Modify");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationPaintEvent e) {
		debug(e.getPlayer(), "&b" + e.getEventName() + " - Paint");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationRotateEvent e) {
		debug(e.getPlayer(), "&b" + e.getEventName() + " - Rotate");
	}

	@EventHandler
	public void on(CreativePickBlockEvent event) {
		Player player = event.getPlayer();

		Block clicked = player.getTargetBlockExact(5);
		if (isNullOrAir(clicked))
			return;

		debug(player, "CreativePickBlock");
		DecorationInteractData data = new DecorationInteractData(clicked, BlockFace.UP);
		if (data.getDecoration() == null) {
			debug(player, " decoration == null");
			return;
		}

		ItemStack newItem = data.getDecoration().getItemDrop(player);

		PlayerUtils.giveItem(player, newItem);
		PlayerUtils.selectHotbarItem(player, newItem);

		/* TODO DECORATIONS:
			if empty slot in hotbar, set item to slot, select slot
			if item exists in hotbar, select slot
			if item doesnt exist in hotbar, and hotbar is full, move selected item into inv (delete if full), set item to slot
		 */
	}

	@EventHandler
	public void onItemFrameInteract(PlayerInteractEntityEvent event) {
		EquipmentSlot slot = event.getHand();
		if (slot != EquipmentSlot.HAND)
			return;

		Player player = event.getPlayer();
		ItemStack tool = ItemUtils.getTool(player);

		DecorationConfig toolConfig = DecorationConfig.of(tool);
		boolean playerHoldingDecor = toolConfig != null;

		Entity entity = event.getRightClicked();
		if (!(entity instanceof ItemFrame itemFrame)) {
			if (!playerHoldingDecor)
				return;

			event.setCancelled(true);
			return;
		}

		boolean frameHoldingItem = !Nullables.isNullOrAir(itemFrame.getItem());

		DecorationConfig frameConfig = DecorationConfig.of(itemFrame);
		boolean frameHoldingDecor = frameConfig != null;

		if (frameHoldingItem && !frameHoldingDecor)
			return;

		debug(player, "onInteractItemFrame:");

		if (!frameHoldingItem) {
			if (!playerHoldingDecor)
				return;

			// cancel trying to place decoration into item frame
			event.setCancelled(true);
			return;
		}

		final Decoration decoration = new Decoration(frameConfig, itemFrame);
		decoration.interact(player, itemFrame.getLocation().getBlock(), InteractType.RIGHT_CLICK, tool);

		debug(player, " cancelling PlayerInteractEntityEvent");
		event.setCancelled(true); // cancel rotation
	}

	@EventHandler
	public void onItemFrameDamage(EntityDamageByEntityEvent event) {
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

		DecorationConfig config = DecorationConfig.of(itemStack);
		if (config == null) {
			debug(player, "config == null");
			return;
		}

		Tasks.wait(1, () -> {
			if (itemFrame.isValid())
				itemFrame.setSilent(true);
		});

		Decoration decoration = new Decoration(config, itemFrame);
		DecorationInteractData data = new DecorationInteractData.DecorationInteractDataBuilder()
				.player(player)
				.decoration(decoration)
				.tool(ItemUtils.getTool(player))
				.blockFaceOverride(itemFrame.getAttachedFace())
				.build();

		boolean cancel = destroy(data, player);
		if (cancel)
			event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerFlowerPotManipulateEvent event) {
		Block flowerPot = event.getFlowerpot();
		if (!DecorationConfig.getHitboxTypes().contains(flowerPot.getType()))
			return;

		DecorationInteractData data = new DecorationInteractData.DecorationInteractDataBuilder()
				.player(event.getPlayer())
				.block(flowerPot)
				.blockFace(null)
				.tool(event.getItem())
				.build();

		if (data.getDecoration() == null)
			return;

		debug(event.getPlayer(), "manipulate decoration hitbox (flowerpot)");
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR) // To prevent mcmmo "you ready your fists" sound
	public void on(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (isCancelled(event))
			return;

		Player player = event.getPlayer();
		final ItemStack tool = ItemUtils.getTool(player);

		Block clicked = event.getClickedBlock();
		if (clicked == null)
			return;

		DecorationInteractData data = getData(event, player, tool, clicked);

		debug(player, "onInteract:");
		boolean cancel = false;

		switch (event.getAction()) {
			case LEFT_CLICK_BLOCK -> cancel = destroy(data, player);
			case RIGHT_CLICK_BLOCK -> {
				boolean shouldInteract = false;
				if (!player.isSneaking()) {
					if (isNullOrAir(tool))
						shouldInteract = true;
					else if (data.isInteractable())
						shouldInteract = true;
				}

				boolean shouldInteractFinal = shouldInteract || DyeStation.isPaintbrush(tool);
				debug(player, "should interact = " + shouldInteractFinal);

				if (shouldInteractFinal)
					cancel = interact(data, InteractType.RIGHT_CLICK);
				else
					cancel = place(data);
			}
		}

		if (cancel) {
			debug(player, "&aHandled interaction, cancelling original event");
			event.setCancelled(true);
		}
	}

	private static final Map<String, InteractData> playerInteractDataMap = new HashMap<>();

	private static DecorationInteractData getData(PlayerInteractEvent event, Player player, ItemStack tool, Block clicked) {
		DecorationInteractData data;
		String playerUUID = player.getUniqueId().toString();
		InteractData interactData = playerInteractDataMap.getOrDefault(playerUUID, null);
		if (interactData != null && interactData.equals(event)) {
			debug(player, "\nuse cached DecorationInteractData");
			data = interactData.getData();
		} else {
			debug(player, "\ncreated new DecorationInteractData");
			data = new DecorationInteractData.DecorationInteractDataBuilder()
					.player(player)
					.block(clicked)
					.blockFace(event.getBlockFace())
					.blockFaceOverride(event.getBlockFace().getOppositeFace())
					.tool(tool)
					.build();

			// if decoration was not found, check for light hitbox next
			if (!data.isDecorationValid()) {
				debug(player, " invalid decoration 1, checking for light");
				Block inFront = clicked.getRelative(event.getBlockFace());
				if (inFront.getType() == Material.LIGHT) {
					debug(player, " - found light in front");
					data = new DecorationInteractData.DecorationInteractDataBuilder()
							.player(player)
							.block(inFront)
							.blockFace(event.getBlockFace())
							.blockFaceOverride(event.getBlockFace().getOppositeFace())
							.tool(tool)
							.build();
				}

				if (data.isDecorationValid())
					debug(player, " valid decoration 2");
				else
					debug(player, " invalid decoration 2");
			} else
				debug(player, " valid decoration 1");

			playerInteractDataMap.put(playerUUID, new InteractData(event, data));
		}
		return data;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(HangingBreakEvent event) {
		if (!(event.getEntity() instanceof ItemFrame itemFrame))
			return;

		DecorationConfig config = DecorationConfig.of(itemFrame);
		if (config == null)
			return;

		Decoration decoration = new Decoration(config, itemFrame);

		if (DecorationEntityData.of(itemFrame).isProcessDestroy())
			return;

		new DecorationDestroyEvent(null, decoration).callEvent();
	}

	//

	// Returning whether to cancel interact event
	boolean destroy(DecorationInteractData data, Player debugger) {
		debug(data.getPlayer(), "try destroy");
		if (!data.isDecorationValid()) {
			debug(data.getPlayer(), "decoration invalid");
			return false;
		}

		if (!data.getDecoration().canEdit(data.getPlayer())) {
			if (!DecorationCooldown.LOCKED.isOnCooldown(data.getPlayer()))
				DecorationError.LOCKED.send(data.getPlayer());
			return true;
		}

		final GameMode gamemode = data.getPlayer().getGameMode();
		if (!GameModeWrapper.of(gamemode).canBuild()) {
			debug(data.getPlayer(), "can't build in this gamemode");
			return true;
		}

		if (gamemode == GameMode.SURVIVAL) {
			if (!DecorationCooldown.DESTROY.isOnCooldown(data.getPlayer(), data.getDecoration().getItemFrame().getUniqueId())) {
				debug(data.getPlayer(), "first punch, returning");
				DecorationUtils.getSoundBuilder(data.getDecoration().getConfig().getHitSound()).location(data.getLocation()).play();
				data.interact(InteractType.LEFT_CLICK);
				return true;
			}
		}

		debug(data.getPlayer(), "attempting to destroy...");

		if (DecorationCooldown.DESTROY.isOnCooldown(data.getPlayer())) {
			debug(data.getPlayer(), "&cslow down (destroy)");
			return true;
		}

		if (!data.getDecoration().getConfig().isMultiBlockWallThing())
			data.setBlockFaceOverride(null);

		if (data.getBlockFaceOverride() != null)
			debug(debugger, "BlockFace Override 2: " + data.getBlockFaceOverride());

		data.destroy(debugger);
		return true;
	}

	private boolean interact(DecorationInteractData data, InteractType type) {
		if (data == null)
			return false;

		debug(data.getPlayer(), "attempting to interact...");

		if (!data.isDecorationValid()) {
			debug(data.getPlayer(), "invalid decoration 2 (interact)");

			if (CreativeBrushMenu.canOpenMenu(data.getPlayer())) {
				CreativeBrushMenu.openMenu(data.getPlayer());
				return true;
			}

			return false;
		}

		data.interact(type);
		data.getPlayer().swingMainHand();

		return true;
	}

	private boolean place(DecorationInteractData data) {
		debug(data.getPlayer(), "attempting to place...");

		final DecorationConfig config = DecorationConfig.of(data.getTool());
		if (config == null) {
			debug(data.getPlayer(), "config == null");
			return false;
		}

		data.setDecoration(new Decoration(config, null));

		if (DecorationCooldown.PLACE.isOnCooldown(data.getPlayer())) {
			debug(data.getPlayer(), "&cslow down (place)");
			return true;
		}

		if (!data.playerCanWGEdit()) {
			DecorationError.WORLDGUARD_USAGE.send(data.getPlayer());
			return true;
		}

		data.place();
		return true;
	}

	public static boolean isCancelled(PlayerInteractEvent event) {
		return event.useInteractedBlock() == Result.DENY || event.useInteractedBlock() == Result.DENY;
	}

	@Getter
	private static class InteractData {
		;
		PlayerInteractEvent event;
		DecorationInteractData data;
		int tick;

		public InteractData(PlayerInteractEvent event, DecorationInteractData data) {
			this.event = event;
			this.data = data;
			this.tick = Bukkit.getCurrentTick();
		}

		public boolean equals(PlayerInteractEvent _event) {
			if (Bukkit.getCurrentTick() - this.tick > TickTime.SECOND.x(5))
				return false;

			if (_event.getMaterial() != event.getMaterial())
				return false;

			Location interact1 = _event.getInteractionPoint();
			Location interact2 = event.getInteractionPoint();

			if (interact1 == null || interact2 == null)
				return false;

			if (!interact1.equals(interact2))
				return false;

			if (isNotNullOrAir(_event.getItem())) {
				if (!ItemUtils.isFuzzyMatch(_event.getItem(), event.getItem()))
					return false;
			}

			return true;
		}
	}

}
