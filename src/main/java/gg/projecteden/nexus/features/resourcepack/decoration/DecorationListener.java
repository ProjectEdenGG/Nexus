package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationInteractData.DecorationInteractDataBuilder;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationCooldown;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration.DecorationEditType;
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
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSpawnEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreType;
import gg.projecteden.nexus.features.workbenches.dyestation.CreativeBrushMenu;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import io.papermc.paper.event.player.PlayerPickEntityEvent;
import lombok.Getter;
import lombok.NonNull;
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
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;

@SuppressWarnings("deprecation")
public class DecorationListener implements Listener {

	public DecorationListener() {
		Nexus.registerListener(this);
	}

	//

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationDestroyEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Destroy");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationInteractEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Interact");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationModifyEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Modify");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationPaintEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Paint");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationPlacedEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Placed");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationPrePlaceEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - PrePlace");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationRotateEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Rotate");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationSitEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Sit");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationSpawnEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Spawn");
	}

	//

	// TODO: PICK BLOCK LIGHT BLOCK HITBOXES
	// TODO: IF PLAYER IS HOLDING A BARRIER ON PICK BLOCK, THE DECORATION OVERWRITES THE BARRIER
	@EventHandler
	public void onPickBlock(PlayerPickBlockEvent event) {
		GameMode gameMode = event.getPlayer().getGameMode();
		if (gameMode != GameMode.CREATIVE)
			return;

		ItemStack itemStack = null;
		Location location = event.getBlock().getLocation();
		if (location != null) {
			DecorationInteractData data = new DecorationInteractData(location.getBlock(), BlockFace.UP);
			if (data.getDecoration() != null)
				itemStack = data.getDecoration().getItemDrop(event.getPlayer());
		}

		if (itemStack == null)
			return;

		event.setCancelled(true);
		DecorationLang.debug(event.getPlayer(), "PlayerPickBlockEvent");
		onPickBlock(event.getPlayer(), itemStack, event.getTargetSlot());
	}

	@EventHandler
	public void onPickBlock(PlayerPickEntityEvent event) {
		GameMode gameMode = event.getPlayer().getGameMode();
		if (gameMode != GameMode.CREATIVE)
			return;

		ItemStack itemStack = null;
		Entity entity = event.getEntity();
		if (entity instanceof ItemFrame itemFrame) {
			DecorationConfig _config = DecorationConfig.of(itemFrame);
			if (_config != null) {

				itemStack = new Decoration(_config, itemFrame).getItemDrop(event.getPlayer());
			}
		}

		if (itemStack == null)
			return;

		event.setCancelled(true);
		DecorationLang.debug(event.getPlayer(), "PlayerPickEntityEvent");
		onPickBlock(event.getPlayer(), itemStack, event.getTargetSlot());
	}

	public void onPickBlock(Player player, @NonNull ItemStack itemStack, int targetSlot) {
		PlayerInventory inventory = player.getInventory();

		// Check if picked block is in hotbar already
		int contentSlot = 0;
		ItemStack[] hotbarContents = PlayerUtils.getHotbarContents(player);
		for (ItemStack content : hotbarContents) {
			if (!Nullables.isNullOrAir(content) && ItemUtils.isModelMatch(content, itemStack)) {
				inventory.setHeldItemSlot(contentSlot);
				return;
			}
			contentSlot++;
		}

		inventory.setHeldItemSlot(targetSlot);
		inventory.setItem(targetSlot, itemStack);
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

		DecorationLang.debug(player, "\nonInteractItemFrame:");

		if (!frameHoldingItem) {
			if (!playerHoldingDecor)
				return;

			// cancel trying to place decoration into item frame
			event.setCancelled(true);
			return;
		}

		final Decoration decoration = new Decoration(frameConfig, itemFrame);
		decoration.interact(player, itemFrame.getLocation().getBlock(), InteractType.RIGHT_CLICK, tool);

		DecorationLang.debug(player, " cancelling PlayerInteractEntityEvent");
		event.setCancelled(true); // cancel rotation
	}

	@EventHandler
	public void onItemFrameDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player))
			return;

		if (!(event.getEntity() instanceof ItemFrame itemFrame))
			return;

		if (event.isCancelled()) {
			DecorationLang.debug(player, "entity damage event was cancelled");
			return;
		}

		ItemStack itemStack = itemFrame.getItem();
		if (Nullables.isNullOrAir(itemStack))
			return;

		DecorationConfig config = DecorationConfig.of(itemStack);
		if (config == null) {
			DecorationLang.debug(player, "config == null");
			return;
		}

		Tasks.wait(1, () -> {
			if (itemFrame.isValid())
				itemFrame.setSilent(true);
		});

		Decoration decoration = new Decoration(config, itemFrame);
		DecorationInteractData data = new DecorationInteractDataBuilder()
				.player(player)
				.decoration(decoration)
				.tool(ItemUtils.getTool(player))
				.blockFaceOverride(itemFrame.getAttachedFace())
				.build();

		boolean cancel = destroy(data);
		if (cancel)
			event.setCancelled(true);
	}

	@EventHandler
	public void onHitboxSkullBreak(BlockFromToEvent event) {
		Block block = event.getToBlock();
		if (!DecorationConfig.getHitboxTypes().contains(block.getType()) || !MaterialTag.PLAYER_SKULLS.isTagged(block))
			return;

		DecorationInteractData data = new DecorationInteractDataBuilder()
			.block(block)
			.build();

		if (data.getDecoration() == null)
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void on(PlayerFlowerPotManipulateEvent event) {
		Block flowerPot = event.getFlowerpot();
		if (!DecorationConfig.getHitboxTypes().contains(flowerPot.getType()))
			return;

		DecorationInteractData data = new DecorationInteractDataBuilder()
				.player(event.getPlayer())
				.block(flowerPot)
				.blockFace(null)
				.tool(event.getItem())
				.build();

		if (data.getDecoration() == null)
			return;

		DecorationLang.debug(event.getPlayer(), "manipulate decoration hitbox (flowerpot)");
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

		DecorationLang.debug(player, "onInteract:");
		boolean cancel = false;

		switch (event.getAction()) {
			case LEFT_CLICK_BLOCK -> cancel = destroy(data);
			case RIGHT_CLICK_BLOCK -> {
				boolean shouldInteract = false;
				if (data.isSpecialTool())
					shouldInteract = true;
				else if (!player.isSneaking()) {
					if (Nullables.isNullOrAir(tool))
						shouldInteract = true;
					else if (data.isInteractable())
						shouldInteract = true;
					else if (data.isToolInteractable())
						shouldInteract = true;
				}

				DecorationLang.debug(player, "should interact = " + shouldInteract);

				if (shouldInteract)
					cancel = interact(data, InteractType.RIGHT_CLICK);
				else
					cancel = place(data);
			}
		}

		if (cancel) {
			DecorationLang.debug(player, "&aHandled interaction, cancelling original event");
			event.setCancelled(true);
		}
	}

	private static final Map<String, InteractData> playerInteractDataMap = new HashMap<>();

	private static DecorationInteractData getData(PlayerInteractEvent event, Player player, ItemStack tool, Block clicked) {
		DecorationInteractData data;
		String playerUUID = player.getUniqueId().toString();
		InteractData interactData = playerInteractDataMap.getOrDefault(playerUUID, null);
		if (interactData != null && interactData.equals(event)) {
			DecorationLang.debug(player, "\nuse cached DecorationInteractData");
			data = interactData.getData();
		} else {
			DecorationLang.debug(player, "\ncreated new DecorationInteractData");
			data = new DecorationInteractDataBuilder()
					.player(player)
					.block(clicked)
					.blockFace(event.getBlockFace())
					.blockFaceOverride(event.getBlockFace().getOppositeFace())
					.tool(tool)
					.build();

			// if decoration was not found, check for light hitbox next
			if (!data.isDecorationValid()) {
				DecorationLang.debug(player, " invalid decoration 1, checking for light");
				Block inFront = clicked.getRelative(event.getBlockFace());
				if (inFront.getType() == Material.LIGHT) {
					data = new DecorationInteractDataBuilder()
						.player(player)
						.block(inFront)
						.blockFace(event.getBlockFace())
						.blockFaceOverride(event.getBlockFace().getOppositeFace())
						.tool(tool)
						.build();
				}

				if (data.isDecorationValid())
					DecorationLang.debug(player, " valid decoration 2");
				else
					DecorationLang.debug(player, " invalid decoration 2");
			} else
				DecorationLang.debug(player, " valid decoration 1");

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
	boolean destroy(DecorationInteractData data) {
		Player player = data.getPlayer();
		DecorationLang.debug(player, "try destroy");
		if (!data.isDecorationValid()) {
			DecorationLang.debug(player, "decoration invalid");
			return false;
		}

		if (CreativeBrushMenu.isCreativePaintbrush(data.getTool())) {
			DecorationLang.debug(player, "is creative paintbrush (destroy)");
			if (CreativeBrushMenu.copyDye(player, data.getTool(), data.getDecoration()))
				DecorationLang.debug(player, "  copying dye");
			return true;
		}

		if (!data.getDecoration().canEdit(player, DecorationEditType.BREAK)) {
			DecorationStoreType storeType = DecorationStoreType.of(player);
			if (storeType == null) {
				if (!DecorationCooldown.LOCKED.isOnCooldown(player))
					DecorationError.LOCKED.send(player);
			}

			return true;
		}

		final GameMode gamemode = player.getGameMode();
		if (!GameModeWrapper.of(gamemode).canBuild()) {
			DecorationLang.debug(player, "can't build in this gamemode");
			return true;
		}

		if (gamemode == GameMode.SURVIVAL) {
			if (!DecorationCooldown.DESTROY.isOnCooldown(player, data.getDecoration().getItemFrame().getUniqueId())) {
				DecorationLang.debug(player, "first punch, returning");

				if (data.doPlayHitSound())
					DecorationUtils.getSoundBuilder(data.getDecoration().getConfig().getHitSound()).location(data.getLocation()).play();

				data.interact(InteractType.LEFT_CLICK);
				return true;
			}
		}

		DecorationLang.debug(player, "attempting to destroy...");

		if (DecorationCooldown.DESTROY.isOnCooldown(player)) {
			DecorationLang.debug(player, "&cslow down (destroy)");
			return true;
		}

		if (!data.getDecoration().getConfig().isMultiBlockWallThing()) {
			data.setBlockFaceOverride(null);
		}


		data.destroy();
		return true;
	}

	private boolean interact(DecorationInteractData data, InteractType type) {
		if (data == null)
			return false;

		DecorationLang.debug(data.getPlayer(), "attempting to interact...");

		if (!data.isDecorationValid()) {
			DecorationLang.debug(data.getPlayer(), "invalid decoration 2 (interact)");

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
		DecorationLang.debug(data.getPlayer(), "attempting to place...");

		final DecorationConfig config = DecorationConfig.of(data.getTool());
		if (config == null) {
			DecorationLang.debug(data.getPlayer(), "config == null");
			return false;
		}

		data.setDecoration(new Decoration(config));

		if (DecorationCooldown.PLACE.isOnCooldown(data.getPlayer())) {
			DecorationLang.debug(data.getPlayer(), "&cslow down (place)");
			return true;
		}

		if (!PlayerUtils.canPlace(data.getPlayer(), data.getLocation())) {
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
