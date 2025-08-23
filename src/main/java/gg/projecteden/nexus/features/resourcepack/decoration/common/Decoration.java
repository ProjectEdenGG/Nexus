package gg.projecteden.nexus.features.resourcepack.decoration.common;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationEntityData;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationCooldown;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Addition;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPaintEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPlacedEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPrePlaceEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationRotateEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.workbenches.dyestation.CreativeBrushMenu;
import gg.projecteden.nexus.models.trust.TrustsUser.TrustType;
import gg.projecteden.nexus.models.trust.TrustsUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import io.papermc.paper.entity.TeleportFlag.EntityState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Decoration {
	private final DecorationConfig config;
	@Setter
	private ItemFrame itemFrame;
	private final Rotation bukkitRotation;
	private Boolean canEdit;

	public Decoration(DecorationConfig config) {
		this(config, null);
	}

	public Decoration(DecorationConfig config, @Nullable ItemFrame itemFrame) {
		this(config, itemFrame, itemFrame == null ? null : itemFrame.getRotation(), null);
	}

	public Location getOrigin() {
		if (!isValidFrame())
			return null;

		return itemFrame.getLocation().toBlockLocation().clone();
	}

	public boolean is(DecorationType type) {
		return type.getConfig().getId().equals(config.getId());
	}

	private boolean isValidFrame() {
		return itemFrame != null && itemFrame.isValid();
	}

	public UUID getOwner(Player debugger) {
		ItemStack item = getItem(debugger);
		if (Nullables.isNullOrAir(item))
			return null;

		NBTItem nbtItem = new NBTItem(item);
		if (!nbtItem.hasKey(DecorationConfig.NBT_OWNER_KEY)) {
			DecorationLang.debug(debugger, "&cMissing NBT Key: Owner");
			return null;
		}

		String owner = nbtItem.getString(DecorationConfig.NBT_OWNER_KEY);
		DecorationLang.debug(debugger, "&eOwner: " + PlayerUtils.getPlayer(owner).getName());

		return UUID.fromString(owner);
	}

	public void setOwner(UUID uuid, Player debugger) {
		ItemStack item = getItem(debugger);
		if (Nullables.isNullOrAir(item))
			return;

		if (!new NBTItem(item).hasKey(DecorationConfig.NBT_OWNER_KEY)) {
			DecorationLang.debug(debugger, "&cMissing NBT Key: Owner");
			return;
		}

		ItemStack newItem = new ItemBuilder(item)
			.nbt(nbt -> nbt.setString(DecorationConfig.NBT_OWNER_KEY, uuid.toString()))
			.build();

		itemFrame.setItem(newItem);
	}

	public ItemStack getItem(OfflinePlayer debugger) {
		if (!isValidFrame())
			return null;

		ItemStack frameItem = itemFrame.getItem();
		if (Nullables.isNullOrAir(frameItem))
			return null;

		return frameItem;
	}

	public ItemStack getItemDrop(Player debugger) {
		ItemStack frameItem = getItem(debugger);
		if (Nullables.isNullOrAir(frameItem))
			return null;

		final NBTItem nbtItem = new NBTItem(frameItem);
		if (nbtItem.hasKey(DecorationConfig.NBT_DECOR_NAME)) {
			ItemBuilder item = new ItemBuilder(frameItem)
				.name(nbtItem.getString(DecorationConfig.NBT_DECOR_NAME))
				.nbt(_nbtItem -> _nbtItem.removeKey(DecorationConfig.NBT_DECOR_NAME));

			frameItem = item.build();
		}

		if (nbtItem.hasKey(DecorationConfig.NBT_OWNER_KEY)) {
			ItemBuilder item = new ItemBuilder(frameItem)
				.nbt(_nbtItem -> _nbtItem.removeKey(DecorationConfig.NBT_OWNER_KEY));

			frameItem = item.build();
		}

		return frameItem;
	}
	public @Nullable ItemFrameRotation getRotation() {
		if (!isValidFrame())
			return null;

		return ItemFrameRotation.of(itemFrame);
	}

	public boolean destroy(@NonNull Player player, BlockFace blockFace) {
		NBT.modifyPersistentData(itemFrame, nbt -> {
			nbt.setBoolean(DecorationConfig.NBT_DECORATION_KEY, true);
		});
		final Decoration decoration = new Decoration(config, itemFrame);

		ItemStack tool = ItemUtils.getTool(player);
		if (CreativeBrushMenu.isCreativePaintbrush(tool)) {
			DecorationLang.debug(player, "is creative paintbrush (destroy)");
			if (CreativeBrushMenu.copyDye(player, tool, decoration))
				DecorationLang.debug(player, "  copying dye");
			return false;
		}

		if (config instanceof Seat seat) {
			DecorationLang.debug(player, "is seat");
			if (isValidFrame()) {
				if (seat.isOccupied(config, itemFrame, player)) {
					DecorationError.SEAT_OCCUPIED.send(player);
					return false;
				}
			}
		}

		if (DecorationEntityData.of(itemFrame).isProcessDestroy())
			return false;

		DecorationDestroyEvent destroyEvent = new DecorationDestroyEvent(player, decoration);
		if (!destroyEvent.callEvent())
			return false;

		if (!destroyEvent.isIgnoreLocked()) {
			if (!canEdit(player)) {
				if (!DecorationCooldown.LOCKED.isOnCooldown(player, TickTime.SECOND.x(2)))
					DecorationError.LOCKED.send(player);
				DecorationLang.debug(player, "locked decoration (destroy)");

				return false;
			}
		}

		DecorationEntityData.of(itemFrame).setProcessDestroy(true);
		new HangingBreakByEntityEvent(itemFrame, player, RemoveCause.ENTITY).callEvent(); // For CoreProtect

		ItemFrameRotation rotation = getRotation();
		BlockFace finalFace = BlockFace.UP;
		if (rotation != null)
			finalFace = rotation.getBlockFace();

		if (getConfig().isMultiBlockWallThing()) {
			DecorationLang.debug(player, "is WallThing & Multiblock");
			finalFace = blockFace;
		}

		DecorationLang.debug(player, "Final BlockFace: " + finalFace);
		Hitbox.destroy(player, decoration, finalFace);

		Location origin = decoration.getOrigin();
		if (!destroyEvent.getDrops().isEmpty()) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				for (ItemStack item : destroyEvent.getDrops()) {
					player.getWorld().dropItemNaturally(origin, item);
				}
			}
		}

		DecorationUtils.getSoundBuilder(config.getBreakSound()).location(origin).play();
		itemFrame.remove();
		return true;
	}

	public boolean place(Player player, EquipmentSlot hand, Block block, BlockFace clickedFace, ItemStack item, ItemFrameRotation rotationOverride, boolean override) {
		if (!override) { // Extra checks for placing decorations with unique restrictions
			if (config instanceof Addition addition) {
				addition.placementError(player);
				return false;
			}
		}

		final Decoration decoration = new Decoration(config);
		DecorationLang.debug(player, "validating placement...");
		if (!config.isValidPlacement(block, clickedFace, player)) {
			DecorationLang.debug(player, "- invalid placement");
			return false;
		}

		Location origin = block.getRelative(clickedFace).getLocation().clone();

		ItemFrameRotation frameRotation;
		boolean placedOnWall = DecorationUtils.getCardinalFaces().contains(clickedFace);
		boolean canPlaceOnWall = !decoration.getConfig().disabledPlacements.contains(PlacementType.WALL);
		BlockFace blockFaceOverride = null;


		if (placedOnWall && canPlaceOnWall) {
			frameRotation = ItemFrameRotation.DEGREE_0;
			blockFaceOverride = frameRotation.getBlockFace();
			DecorationLang.debug(player, "is placing on wall");

			if (config.isMultiBlock()) {
				DecorationLang.debug(player, "is multiblock");
				blockFaceOverride = clickedFace.getOppositeFace();
				DecorationLang.debug(player, "BlockFace Override 4: " + blockFaceOverride);
			}

			if (!config.isValidLocation(origin, frameRotation, blockFaceOverride, false, player)) {
				DecorationLang.debug(player, "- invalid frame location");
				return false;
			}
		} else {
			frameRotation = config.findValidFrameRotation(origin, ItemFrameRotation.of(player), player);
		}

		if (rotationOverride != null)
			frameRotation = rotationOverride;

		if (frameRotation == null) {
			DecorationLang.debug(player, "- couldn't find a valid frame rotation");
			return false;
		}
		//


		if (clickedFace == BlockFace.DOWN) { // Ceiling changes
			switch (PlayerUtils.getBlockFace(player)) {
				case EAST, WEST -> frameRotation = frameRotation.getOppositeRotation();
				case SOUTH_WEST, NORTH_EAST ->
					frameRotation = frameRotation.rotateCounterClockwise().rotateCounterClockwise();
				case NORTH_WEST, SOUTH_EAST -> frameRotation = frameRotation.rotateClockwise().rotateClockwise();
			}
		}

		DecorationLang.debug(player, "frameRotation = " + frameRotation.name());

		DecorationPrePlaceEvent prePlaceEvent = new DecorationPrePlaceEvent(player, decoration, item, clickedFace, frameRotation);
		if (!prePlaceEvent.callEvent()) {
			DecorationLang.debug(player, "&6DecorationPrePlaceEvent was cancelled");
			return false;
		}

		ItemStack finalItem = config.getFrameItem(player, prePlaceEvent.getItem());
		ItemUtils.subtract(player, item);

		final BlockFace finalFace = prePlaceEvent.getAttachedFace();
		final ItemFrameRotation finalRotation = prePlaceEvent.getRotation();

		ItemFrame itemFrame = block.getWorld().spawn(origin, ItemFrame.class, _itemFrame -> {
			_itemFrame.customName(null);
			_itemFrame.setCustomNameVisible(false);
			_itemFrame.setFacingDirection(finalFace, true);
			_itemFrame.setRotation(finalRotation.getRotation());
			_itemFrame.setVisible(false);
			_itemFrame.setGlowing(false);
			_itemFrame.setSilent(true);
			_itemFrame.setItem(finalItem, false);
		});
		NBT.modifyPersistentData(itemFrame, nbt -> {
			nbt.setBoolean(DecorationConfig.NBT_DECORATION_KEY, true);
		});

		decoration.setItemFrame(itemFrame);

		BlockFace placeFace = frameRotation.getBlockFace();
		if (blockFaceOverride != null) {
			placeFace = blockFaceOverride;
			DecorationLang.debug(player, "BlockFace Override 3: " + blockFaceOverride);
		}

		Hitbox.place(player, config.getHitboxes(), origin, placeFace);

		DecorationUtils.getSoundBuilder(config.hitSound).location(origin).play();

		DecorationLang.debug(player, "placed");
		new DecorationPlacedEvent(player, decoration, finalItem, finalFace, finalRotation, itemFrame.getLocation()).callEvent();
		new HangingPlaceEvent(itemFrame, player, block, finalFace, hand, finalItem).callEvent(); // For CoreProtect
		return true;
	}

	public boolean canEdit(Player player) {
		DecorationLang.debug(player, "Can Edit?");
		if (canEdit != null) {
			DecorationLang.debug(player, " --> " + canEdit);
			return canEdit;
		}

		if (this.getOrigin() == null) {
			// Only seems to occur if the decoration is clientside, thus always return false
			return setCanEdit(false);
		}

		boolean isWGEdit = WorldGuardEditCommand.canWorldGuardEdit(player);
		boolean isInRegion = !new WorldGuardUtils(player).getRegionsAt(this.getOrigin()).isEmpty();


		if (isWGEdit) {
			DecorationLang.debug(player, " WGEdit is on --> yes");
			return setCanEdit(true);
		}

		if (isInRegion) { // TODO || flag == allow
			DecorationLang.debug(player, " Is in region --> no");
			return setCanEdit(false);
		}

		if (Nullables.isNullOrAir(getItem(player))) {
			DecorationLang.debug(player, " Item is null --> yes");
			return setCanEdit(true);
		}

		UUID owner = getOwner(player);

		if (owner == null) {
			DecorationLang.debug(player, " Owner is null --> yes");
			return setCanEdit(true);
		}

		if (player.getUniqueId().equals(owner)) {
			DecorationLang.debug(player, " Is owner --> yes");
			return setCanEdit(true);
		}

		boolean isTrusted = new TrustsUserService().get(owner).trusts(TrustType.DECORATIONS, player);
		DecorationLang.debug(player, " Is trusted --> " + isTrusted);

		return setCanEdit(isTrusted);
	}

	private boolean setCanEdit(boolean bool) {
		this.canEdit = bool;
		return this.canEdit;
	}

	public boolean interact(Player player, Block block, InteractType type, ItemStack tool) {
		if (DecorationCooldown.INTERACT.isOnCooldown(player, 2)) {
			DecorationLang.debug(player, "&cslow down (interact)");
			return true;
		}

		DecorationInteractEvent interactEvent = new DecorationInteractEvent(player, block, this, type);
		if (!interactEvent.callEvent()) {
			DecorationLang.debug(player, "&6DecorationInteractEvent was cancelled");
			return false;
		}

		DecorationLang.debug(player, "&eId: " + config.getId());

		if (config instanceof Dyeable) {
			if (paint(player, block, tool))
				return false;
		}

		if (CreativeBrushMenu.canOpenMenu(player)) {
			CreativeBrushMenu.openMenu(player);
			return false;
		}

		if (type == InteractType.RIGHT_CLICK) {
			if (config instanceof Seat && !player.isSneaking()) {
				DecorationLang.debug(player, "attempting to sit...");
				DecorationSitEvent sitEvent = new DecorationSitEvent(player, block, this, bukkitRotation);
				if (trySit(sitEvent, player, block))
					return true;
				else
					DecorationLang.debug(player, "&6DecorationSitEvent was cancelled 1");
			}

			if (config.isRotatable() && canEdit(player)) {
				DecorationRotateEvent rotateEvent = new DecorationRotateEvent(player, block, this, InteractType.RIGHT_CLICK);
				if (!rotateEvent.callEvent())
					return false;

				itemFrame.setRotation(itemFrame.getRotation().rotateClockwise());
			}
		}

		return true;
	}

	@Getter
	private static final Map<UUID, Integer> backlessTasks = new HashMap<>();

	public boolean trySit(DecorationSitEvent sitEvent, Player player, Block block) {
		if (!sitEvent.callEvent())
			return false;

		ArmorStand armorStand = sitEvent.getSeat().trySit(player, block, sitEvent.getRotation(), sitEvent.getDecoration());
		if (armorStand == null)
			return false;

		if (sitEvent.getSeat().isBackless()) {
			int taskId = Tasks.repeat(0, TickTime.TICK.x(4), () -> {
				Location rotated = armorStand.getLocation().clone();
				rotated.setYaw(player.getLocation().getYaw());
				armorStand.teleport(rotated, EntityState.RETAIN_PASSENGERS);
			});

			backlessTasks.put(player.getUniqueId(), taskId);
		}

		return true;
	}

	public boolean paint(Player player, Block block, ItemStack tool) {
		if (!DecorationUtils.canUsePaintbrush(player, tool)) {
			return false;
		}

		if (!canEdit(player)) {
			if (!DecorationCooldown.LOCKED.isOnCooldown(player, TickTime.SECOND.x(1)))
				DecorationError.LOCKED.send(player);
			DecorationLang.debug(player, "locked decoration (paint)");

			return false;
		}

		if (player.isSneaking()) {
			if (CreativeBrushMenu.tryOpenMenu(player))
				return false;
		}

		if (DecorationUtils.isSameColor(tool, this.getItemFrame().getItem())) {
			DecorationLang.debug(player, "same color");
			return false;
		}

		Color paintbrushColor = new ItemBuilder(tool).dyeColor();
		DecorationPaintEvent paintEvent = new DecorationPaintEvent(player, block, this, tool, this.getItemFrame(), paintbrushColor);
		if (!paintEvent.callEvent()) {
			DecorationLang.debug(player, "&6DecorationPaintEvent was cancelled");
			return false;
		}

		ItemStack item = paintEvent.getItemFrame().getItem();

		getItemFrame().setItem(new ItemBuilder(item).dyeColor(paintEvent.getColor()).updateDecorationLore(true).build(), false);

		DecorationUtils.usePaintbrush(player, tool);

		DecorationLang.debug(player, "painted");
		return true;
	}

	public Hitbox getHitbox(Location location) {
		if (!isValidFrame())
			return null;

		List<Hitbox> hitboxes = Hitbox.rotateHitboxes(config, itemFrame);
		for (Hitbox hitbox : hitboxes)
			if (hitbox.getOffsetBlock(getOrigin()).equals(location.getBlock()))
				return hitbox;

		return null;
	}
}
