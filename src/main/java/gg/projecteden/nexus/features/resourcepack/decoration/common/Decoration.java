package gg.projecteden.nexus.features.resourcepack.decoration.common;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationEntityData;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationCooldown;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPaintEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationRotateEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.workbenches.dyestation.CreativeBrushMenu;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
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
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Decoration {
	@NonNull
	private final DecorationConfig config;
	private ItemFrame itemFrame;
	private final Rotation bukkitRotation;
	private Boolean canEdit;

	public Decoration(@NonNull DecorationConfig config, ItemFrame itemFrame) {
		this(config, itemFrame, itemFrame == null ? null : itemFrame.getRotation(), null);
	}

	public void setItemFrame(ItemFrame itemFrame) {
		this.itemFrame = itemFrame;
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

	public ItemStack getItem(Player debugger) {
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

	public boolean destroy(@Nullable Player player, BlockFace blockFace, Player debugger) {
		final Decoration decoration = new Decoration(config, itemFrame);

		ItemStack tool = ItemUtils.getTool(player);
		if (CreativeBrushMenu.isCreativePaintbrush(tool)) {
			DecorationLang.debug(debugger, "is creative paintbrush (destroy)");
			if (CreativeBrushMenu.copyDye(player, tool, decoration))
				DecorationLang.debug(debugger, "  copying dye");
			return false;
		}

		if (config instanceof Seat seat) {
			DecorationLang.debug(debugger, "is seat");
			if (isValidFrame()) {
				if (seat.isOccupied(config, itemFrame, debugger)) {
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

		ItemFrameRotation rotation = getRotation();
		BlockFace finalFace = BlockFace.UP;
		if (rotation != null)
			finalFace = rotation.getBlockFace();

		if (getConfig().isMultiBlockWallThing()) {
			DecorationLang.debug(player, "is WallThing & Multiblock");
			finalFace = blockFace;
		}

		DecorationLang.debug(player, "Final BlockFace: " + finalFace);
		Hitbox.destroy(decoration, finalFace, player);

		Location origin = decoration.getOrigin();
		if (!destroyEvent.getDrops().isEmpty()) {
			if (!player.getGameMode().equals(GameMode.CREATIVE)) {
				for (ItemStack item : destroyEvent.getDrops()) {
					player.getWorld().dropItemNaturally(origin, item);
				}
			}

		}

		DecorationUtils.getSoundBuilder(config.getBreakSound()).location(origin).play();
		itemFrame.remove();

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

		boolean isTrusted = new TrustService().get(owner).trusts(Type.DECORATIONS, player);
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

		ArmorStand armorStand = sitEvent.getSeat().trySit(player, block, sitEvent.getRotation(), config);
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
}
