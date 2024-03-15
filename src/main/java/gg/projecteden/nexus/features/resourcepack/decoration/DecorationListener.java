package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationCooldown;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationModifyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPaintEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlacedEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import org.bukkit.GameMode;
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

import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@SuppressWarnings("deprecation")
public class DecorationListener implements Listener {

	public DecorationListener() {
		Nexus.registerListener(this);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationPrePlaceEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - PrePlace");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationPlacedEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Placed");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationDestroyEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Destroy");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationInteractEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Interact");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationSitEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Sit");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationModifyEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Modify");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(DecorationPaintEvent e) {
		DecorationLang.debug(e.getPlayer(), "&b" + e.getEventName() + " - Paint");
	}

	@EventHandler
	public void on(CreativePickBlockEvent event) {
		Player player = event.getPlayer();
		// TODO DECORATIONS - Remove on release
		if (!DecorationUtils.canUseFeature(player))
			return;
		//

		Block clicked = player.getTargetBlockExact(5);
		if (isNullOrAir(clicked))
			return;

		DecorationLang.debug(player, "CreativePickBlock");
		DecorationInteractData data = new DecorationInteractData(clicked, BlockFace.UP);
		if (data.getDecoration() == null) {
			DecorationLang.debug(player, " decoration == null");
			return;
		}

		ItemStack newItem = data.getDecoration().getItemDrop(player);

		PlayerUtils.giveItem(player, newItem);
		PlayerUtils.selectHotbarItem(player, newItem);

		if (PlayerUtils.playerHas(player, Material.BARRIER)) {
			Dev.BLAST.send("has barrier 1");
		}

		/* TODO:
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
		// TODO DECORATIONS - Remove on release
		if (!DecorationUtils.canUseFeature(event.getPlayer(), toolConfig))
			return;
		//

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

		DecorationLang.debug(player, "onInteractItemFrame:");

		if (!frameHoldingItem) {
			if (!playerHoldingDecor)
				return;

			// cancel trying to place decoration into item frame
			event.setCancelled(true);
			return;
		}

		final Decoration decoration = new Decoration(frameConfig, itemFrame);

		// if attempt to rotate seat itemFrame without sneaking
		if (decoration.getConfig() instanceof Seat seat) {
			if (!player.isSneaking()) {
				DecorationLang.debug(player, " decor is seat -> new SitEvent");
				DecorationSitEvent sitEvent = new DecorationSitEvent(player, decoration, itemFrame);
				if (sitEvent.callEvent()) {
					DecorationLang.debug(player, "Attempting to sit");
					if (seat.trySit(player, itemFrame, frameConfig)) {
						DecorationLang.debug(player, "sat player");
						event.setCancelled(true);
						return;
					}
					DecorationLang.debug(player, "&cfailed to sit");
				} else {
					DecorationLang.debug(player, "&6DecorationSitEvent was cancelled 2");
				}
			}
		}

		if (!decoration.interact(player, itemFrame.getLocation().getBlock(), InteractType.RIGHT_CLICK, tool)) {
			event.setCancelled(true);
			return;
		}

		DecorationLang.debug(player, "attempting to rotate");
		if (!frameConfig.isRotatable()) {
			DecorationLang.debug(player, "decoration is not rotatable");
			event.setCancelled(true);
		}
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
		if (isNullOrAir(itemStack))
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

		DecorationLang.debug(event.getPlayer(), "manipulate decoration hitbox (flowerpot)");
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR) // To prevent mcmmo "you ready your fists" sound
	public void on(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Block clicked = event.getClickedBlock();
		if (clicked == null)
			return;

		Player player = event.getPlayer();
		if (isCancelled(event)) {
			DecorationLang.debug(player, "PlayerInteractEvent was cancelled (Decorations)");
			return;
		}

		DecorationLang.debug(player, "\nnew DecorationInteractData");
		final ItemStack tool = ItemUtils.getTool(player);
		DecorationInteractData data = new DecorationInteractData.DecorationInteractDataBuilder()
				.player(player)
				.block(clicked)
				.blockFace(event.getBlockFace())
				.blockFaceOverride(event.getBlockFace().getOppositeFace())
				.tool(tool)
				.build();

		DecorationLang.debug(player, "onInteract:");
		boolean cancel = false;

		// if decoration was not found, check for light hitbox next
		if (!data.isDecorationValid()) {
			DecorationLang.debug(player, " invalid decoration 1");
			if (!List.of(BlockFace.UP, BlockFace.DOWN).contains(event.getBlockFace())) {
				DecorationLang.debug(player, " - checking for light");
				Block inFront = clicked.getRelative(event.getBlockFace());
				if (inFront.getType() == Material.LIGHT) {
					DecorationLang.debug(player, " -- found light in front");
					data = new DecorationInteractData.DecorationInteractDataBuilder()
							.player(player)
							.block(inFront)
							.blockFace(event.getBlockFace())
							.blockFaceOverride(event.getBlockFace().getOppositeFace())
							.tool(tool)
							.build();
				}
			}

			if (data.isDecorationValid())
				DecorationLang.debug(player, " valid decoration 2");
			else
				DecorationLang.debug(player, " invalid decoration 2");
		} else
			DecorationLang.debug(player, " valid decoration 1");

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

				boolean shouldInteractFinal = shouldInteract || DyeStation.isMagicPaintbrush(tool);
				DecorationLang.debug(player, "should interact = " + shouldInteractFinal);

				if (shouldInteractFinal)
					cancel = interact(data, InteractType.RIGHT_CLICK);
				else
					cancel = place(data);
			}
		}

		if (cancel) {
			DecorationLang.debug(player, "&aHandled interaction, cancelling original event\n");
			event.setCancelled(true);
		}
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

	boolean destroy(DecorationInteractData data, Player debugger) {
		if (!data.isDecorationValid())
			return false;

		// TODO DECORATIONS - Remove on release
		if (!DecorationUtils.canUseFeature(data.getPlayer(), data.getDecoration().getConfig()))
			return false;
		//

		if (!data.playerCanEdit()) {
			if (!DecorationCooldown.LOCKED.isOnCooldown(data.getPlayer()))
				DecorationError.LOCKED.send(data.getPlayer());
			return false;
		}

		final GameMode gamemode = data.getPlayer().getGameMode();
		if (!GameModeWrapper.of(gamemode).canBuild())
			return true;

		if (gamemode == GameMode.SURVIVAL) {
			if (!DecorationCooldown.DESTROY.isOnCooldown(data.getPlayer(), data.getDecoration().getItemFrame().getUniqueId())) {
				DecorationLang.debug(data.getPlayer(), "first punch, returning");
				DecorationUtils.getSoundBuilder(data.getDecoration().getConfig().getHitSound()).location(data.getLocation()).play();
				data.interact(InteractType.LEFT_CLICK);
				return true;
			}
		}

		DecorationLang.debug(data.getPlayer(), "attempting to destroy...");

		if (DecorationCooldown.DESTROY.isOnCooldown(data.getPlayer())) {
			DecorationLang.debug(data.getPlayer(), "&cslow down (destroy)");
			return true;
		}

		if (!data.getDecoration().getConfig().isMultiBlockWallThing())
			data.setBlockFaceOverride(null);

		if (data.getBlockFaceOverride() != null)
			DecorationLang.debug(debugger, "BlockFace Override 2: " + data.getBlockFaceOverride());

		data.destroy(debugger);
		return true;
	}

	private boolean interact(DecorationInteractData data, InteractType type) {
		if (data == null)
			return false;

		DecorationLang.debug(data.getPlayer(), "attempting to interact...");

		if (!data.isDecorationValid()) {
			DecorationLang.debug(data.getPlayer(), "invalid decoration 2");
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

		// TODO DECORATIONS - Remove on release
		if (!DecorationUtils.canUseFeature(data.getPlayer(), config)) {
			DecorationLang.debug(data.getPlayer(), "can't use feature");
			return false;
		}
		//

		data.setDecoration(new Decoration(config, null));

		if (DecorationCooldown.PLACE.isOnCooldown(data.getPlayer())) {
			DecorationLang.debug(data.getPlayer(), "&cslow down (place)");
			return true;
		}

		if (!data.playerCanEdit()) {
			if (!DecorationCooldown.LOCKED.isOnCooldown(data.getPlayer()))
				DecorationError.LOCKED.send(data.getPlayer());
			return true;
		}

		data.place();
		return true;
	}

	public static boolean isCancelled(PlayerInteractEvent event) {
		return event.useInteractedBlock() == Result.DENY || event.useInteractedBlock() == Result.DENY;
	}

}
