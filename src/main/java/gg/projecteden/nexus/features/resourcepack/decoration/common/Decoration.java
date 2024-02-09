package gg.projecteden.nexus.features.resourcepack.decoration.common;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationListener.DecorationAction;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationPaintEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.workbenches.DyeStation;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationListener.isOnCooldown;
import static gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils.debug;

@Data
@AllArgsConstructor
public class Decoration {
	@NonNull
	private final DecorationConfig config;
	private final ItemFrame itemFrame;
	private final Rotation bukkitRotation;

	public Decoration(@NonNull DecorationConfig config, ItemFrame itemFrame) {
		this(config, itemFrame, itemFrame == null ? null : itemFrame.getRotation());
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
			debug(debugger, "Missing NBT Key: Owner");
			return null;
		}

		String owner = nbtItem.getString(DecorationConfig.NBT_OWNER_KEY);
		debug(debugger, "Owner: " + owner);

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

	public boolean destroy(@NonNull Player player, BlockFace blockFace, Player debugger) {
		World world = player.getWorld();

		final Decoration decoration = new Decoration(config, itemFrame);

		if (config instanceof Seat seat) {
			debug(debugger, "is seat");
			if (isValidFrame()) {
				if (seat.isOccupied(config, itemFrame, debugger)) {
					PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cSeat is occupied");
					return false;
				}
			}
		}

		if (!canEdit(player)) {
			if (!new CooldownService().check(player, "decoration-edit-locked", TickTime.SECOND.x(2)))
				PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cThis decoration is locked.");

			return false;
		}

		DecorationDestroyEvent destroyEvent = new DecorationDestroyEvent(player, decoration);
		if (!destroyEvent.callEvent())
			return false;

		ItemFrameRotation rotation = getRotation();
		BlockFace finalFace = BlockFace.UP;
		if (rotation != null)
			finalFace = rotation.getBlockFace();

		if (getConfig().isMultiBlockWallThing()) {
			debug(player, "is WallThing & Multiblock");
			finalFace = blockFace;
		}

		debug(player, "Final BlockFace: " + finalFace);
		Hitbox.destroy(decoration, finalFace, player);

		if (!player.getGameMode().equals(GameMode.CREATIVE)) // TODO: CREATIVE PICK BLOCK
			world.dropItemNaturally(decoration.getOrigin(), decoration.getItemDrop(debugger));

		itemFrame.remove();

		new SoundBuilder(config.getBreakSound()).location(decoration.getOrigin()).play();

		return true;
	}

	public boolean canEdit(Player player) {
		if (Nullables.isNullOrAir(getItem(player)))
			return true;

		Rank playerRank = Rank.of(player);
		UUID owner = getOwner(player);

		if (owner == null)
			return true;

		if (player.getUniqueId().equals(owner))
			return true;

		boolean isTrusted = new TrustService().get(owner).trusts(Type.DECORATIONS, player);

		if (playerRank.isStaff()) {
			if (WorldGroup.STAFF == WorldGroup.of(player) && isTrusted)
				return true;

			if (playerRank.isSeniorStaff() || playerRank.equals(Rank.ARCHITECT) || player.isOp())
				return true;

			if (WorldGuardEditCommand.canWorldGuardEdit(player) && new WorldGuardUtils(player).getRegionsAt(this.getOrigin()).size() > 0)
				return true;
		}

		return isTrusted;
	}

	public boolean interact(Player player, Block block, InteractType type, ItemStack tool) {
		final Decoration decoration = new Decoration(config, itemFrame);
		DecorationInteractEvent interactEvent = new DecorationInteractEvent(player, decoration, type);
		if (!interactEvent.callEvent()) {
			debug(player, "decoration interact event cancelled");
			return false;
		}

		if (isOnCooldown(player, DecorationAction.INTERACT)) {
			debug(player, "slow down");
			return true;
		}

		debug(player, "Id: " + config.getId());

		if (config instanceof Dyeable && DyeStation.isMagicPaintbrush(tool)) {
			debug(player, "attempting to paint...");
			int usesLeft = DyeStation.getUses(tool);
			if (usesLeft <= 0) {
				debug(player, "no more uses");
				return false;
			}

			Color paintbrushColor = new ItemBuilder(tool).dyeColor();
			Color itemColor = new ItemBuilder(decoration.getItemFrame().getItem()).dyeColor();
			if (!itemColor.equals(paintbrushColor)) {
				DecorationPaintEvent paintEvent = new DecorationPaintEvent(player, decoration, tool, decoration.getItemFrame(), paintbrushColor);
				if (paintEvent.callEvent()) {
					ItemStack item = paintEvent.getItemFrame().getItem();
					decoration.getItemFrame().setItem(new ItemBuilder(item).dyeColor(paintEvent.getColor()).build(), false);
					// TODO: SOUND + PARTICLE

					if (player.getGameMode() != GameMode.CREATIVE) {
						ItemBuilder toolBuilder = new ItemBuilder(tool);
						ItemBuilder toolResult = DyeStation.decreaseUses(toolBuilder);
						tool.setItemMeta(toolResult.build().getItemMeta());
					}

					debug(player, "painted");
					return false; // cancel interact event
				}
			}
		}

		if (config instanceof Seat && type == InteractType.RIGHT_CLICK && !player.isSneaking()) {
			debug(player, "attempting to sit...");

			DecorationSitEvent sitEvent = new DecorationSitEvent(player, decoration, bukkitRotation, block);

			if (sitEvent.callEvent()) {
				sitEvent.getSeat().trySit(player, block, sitEvent.getRotation(), config);
				return true;
			}
		}

		return true;
	}

}
