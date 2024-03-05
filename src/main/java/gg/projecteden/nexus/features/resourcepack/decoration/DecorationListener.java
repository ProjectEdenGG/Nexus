package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationModifyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPaintEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlacedEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.debug;
import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.error;
import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.isSameColor;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@SuppressWarnings("deprecation")
public class DecorationListener implements Listener {

	public DecorationListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(DecorationPrePlaceEvent e) {
		debug(e.getPlayer(), e.getEventName() + " - PrePlace");
	}

	@EventHandler
	public void on(DecorationPlacedEvent e) {
		debug(e.getPlayer(), e.getEventName() + " - Placed");
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
	public void on(DecorationPaintEvent e) {
		debug(e.getPlayer(), e.getEventName() + " - Paint");
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

	@EventHandler
	public void onPaintSign(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Block clicked = event.getClickedBlock();
		if (clicked == null || !MaterialTag.ALL_SIGNS.isTagged(clicked))
			return;

		if (!(clicked.getState() instanceof Sign sign))
			return;

		Player player = event.getPlayer();
		ItemStack tool = ItemUtils.getTool(player);
		if (!DecorationUtils.canUsePaintbrush(player, tool))
			return;

		SignSide side = sign.getSide(sign.getInteractableSideFor(player));
		if (isSameColor(tool, side))
			return;

		if (isCancelled(event)) {
			debug(player, "PlayerInteractEvent was cancelled (onPaintSign)");
			return;
		}

		// TODO DECORATIONS - Remove on release
		if (!DecorationUtils.canUseFeature(event.getPlayer()))
			return;
		//

		event.setCancelled(true);

		Color paintbrushDye = new ItemBuilder(tool).dyeColor();
		String lineColor = "&" + StringUtils.toHex(paintbrushDye);

		int index = 0;
		for (String line : side.getLines()) {
			if (Nullables.isNotNullOrEmpty(line))
				side.setLine(index, StringUtils.colorize(lineColor + StringUtils.stripColor(line)));
			++index;
		}
		side.setColor(DyeColor.BLACK);
		sign.update();

		DecorationUtils.usePaintbrush(player, tool);
		player.swingMainHand();
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

		debug(player, "onInteractItemFrame:");

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
				debug(player, " decor is seat -> new SitEvent");
				DecorationSitEvent sitEvent = new DecorationSitEvent(player, decoration, itemFrame);
				if (sitEvent.callEvent()) {
					debug(player, "Attempting to sit");
					if (seat.trySit(player, itemFrame, frameConfig)) {
						debug(player, "sat player");
						event.setCancelled(true);
						return;
					}
					debug(player, "failed to sit");
				} else {
					debug(player, "SitEvent cancelled");
				}
			}
		}

		if (!decoration.interact(player, itemFrame.getLocation().getBlock(), InteractType.RIGHT_CLICK, tool)) {
			event.setCancelled(true);
			return;
		}

		debug(player, "attempting to rotate");
		if (!frameConfig.isRotatable()) {
			debug(player, "decoration is not rotatable");
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

		Block clicked = event.getClickedBlock();
		if (clicked == null)
			return;

		Player player = event.getPlayer();
		if (isCancelled(event)) {
			debug(player, "PlayerInteractEvent was cancelled (Decorations)");
			return;
		}

		if (!new CooldownService().check(event.getPlayer(), "decoration-interaction-" + event.getPlayer().getUniqueId(), TickTime.TICK.x(5)))
			return;

		Action action = event.getAction();

		debug(player, " - - - ");
		debug(player, "new DecorationInteractData");
		final ItemStack tool = ItemUtils.getTool(player);
		DecorationInteractData data = new DecorationInteractData.DecorationInteractDataBuilder()
				.player(player)
				.block(clicked)
				.blockFace(event.getBlockFace())
				.blockFaceOverride(event.getBlockFace().getOppositeFace())
				.tool(tool)
				.build();

		debug(player, "onInteract:");
		boolean cancel = false;

		// if decoration was not found, check for light hitbox next
		if (!data.isDecorationValid()) {
			debug(player, "invalid decoration 1");
			if (!List.of(BlockFace.UP, BlockFace.DOWN).contains(event.getBlockFace())) {
				debug(player, "checking for light");
				Block inFront = clicked.getRelative(event.getBlockFace());
				if (inFront.getType() == Material.LIGHT) {
					debug(player, "light in front");
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
				debug(player, "valid decoration 2");
			else
				debug(player, "invalid decoration 2");
		} else
			debug(player, "valid decoration 1");

		switch (action) {
			case LEFT_CLICK_BLOCK -> cancel = destroy(data, player);
			case RIGHT_CLICK_BLOCK -> {
				boolean shouldInteract = false;
				if (!player.isSneaking()) {
					if (isNullOrAir(tool))
						shouldInteract = true;
					else if (data.isInteractable())
						shouldInteract = true;
				}

				if (shouldInteract || DyeStation.isMagicPaintbrush(tool))
					cancel = interact(data, InteractType.RIGHT_CLICK);
				else
					cancel = place(data);
			}
		}

		if (cancel) {
			debug(player, "cancelling interact event");
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
			error(data.getPlayer());
			return false;
		}

		final GameMode gamemode = data.getPlayer().getGameMode();
		if (!GameModeWrapper.of(gamemode).canBuild())
			return true;

		if (gamemode == GameMode.SURVIVAL) {
			if (!isOnCooldown(data.getPlayer(), DecorationAction.DESTROY, data.getDecoration().getItemFrame(), TickTime.TICK.x(5))) {
				new SoundBuilder(data.getDecoration().getConfig().getHitSound()).location(data.getLocation()).play();
				debug(data.getPlayer(), "first punch, returning");
				data.interact(InteractType.LEFT_CLICK);
				return true;
			}
		}

		if (isOnCooldown(data.getPlayer(), DecorationAction.DESTROY)) {
			debug(data.getPlayer(), "slow down");
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

		debug(data.getPlayer(), "interact");

		if (!data.isDecorationValid()) {
			debug(data.getPlayer(), "invalid decoration 2");
			return false;
		}

		data.interact(type);
		data.getPlayer().swingMainHand();

		return true;
	}

	private boolean place(DecorationInteractData data) {
		debug(data.getPlayer(), "place");

		final DecorationConfig config = DecorationConfig.of(data.getTool());
		if (config == null) {
			debug(data.getPlayer(), "config == null");
			return false;
		}

		// TODO DECORATIONS - Remove on release
		if (!DecorationUtils.canUseFeature(data.getPlayer(), config)) {
			debug(data.getPlayer(), "can't use feature");
			return false;
		}
		//

		data.setDecoration(new Decoration(config, null));

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

	public enum DecorationAction {
		INTERACT,
		PLACE,
		DESTROY,
	}

	public static boolean isOnCooldown(Player player, DecorationAction action) {
		return !new CooldownService().check(player, "decoration-" + action.name().toLowerCase(), TickTime.TICK.x(5));
	}

	public static boolean isOnCooldown(Player player, DecorationAction action, HasUniqueId entity, long ticks) {
		return !new CooldownService().check(player, "decoration-" + action.name().toLowerCase() + "-" + entity.getUniqueId(), ticks);
	}

}
