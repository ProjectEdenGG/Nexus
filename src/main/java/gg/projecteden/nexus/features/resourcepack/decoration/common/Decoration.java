package gg.projecteden.nexus.features.resourcepack.decoration.common;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationDestroyEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent.InteractType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSitEvent;
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
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

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

	private boolean isValidFrame() {
		return itemFrame != null && itemFrame.isValid();
	}

	public UUID getOwner() {
		ItemStack item = getItem();

		NBTItem nbtItem = new NBTItem(item);
		if (!nbtItem.hasKey(DecorationConfig.NBT_OWNER_KEY))
			return null;

		return UUID.fromString(nbtItem.getString(DecorationConfig.NBT_OWNER_KEY));
	}

	public ItemStack getDroppedItem() {
		if (!isValidFrame())
			return null;

		ItemStack frameItem = itemFrame.getItem();
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

	public ItemStack getItem() {
		if (!isValidFrame())
			return null;

		ItemStack frameItem = itemFrame.getItem();
		final NBTItem nbtItem = new NBTItem(frameItem);
		if (nbtItem.hasKey(DecorationConfig.NBT_DECOR_NAME)) {
			ItemBuilder item = new ItemBuilder(frameItem)
				.name(nbtItem.getString(DecorationConfig.NBT_DECOR_NAME))
				.nbt(_nbtItem -> _nbtItem.removeKey(DecorationConfig.NBT_DECOR_NAME));
			frameItem = item.build();
		}

		return frameItem;
	}

	public ItemFrameRotation getRotation() {
		if (!isValidFrame())
			return null;

		return ItemFrameRotation.of(itemFrame);
	}

	public boolean destroy(@NonNull Player player) {
		World world = player.getWorld();

		final Decoration decoration = new Decoration(config, itemFrame);
		DecorationDestroyEvent destroyEvent = new DecorationDestroyEvent(player, decoration);
		if (!destroyEvent.callEvent())
			return false;

		if (this instanceof Seat seat) {
			if (seat.isOccupied(config, itemFrame)) {
				PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cSeat is occupied");
				return false;
			}
		}

		if (!canEdit(player, decoration.getOrigin())) {
			PlayerUtils.send(player, DecorationUtils.getPrefix() + "&cThis decoration is locked.");
			return false;
		}

		Hitbox.destroy(decoration);

		if (!player.getGameMode().equals(GameMode.CREATIVE))
			world.dropItemNaturally(decoration.getOrigin(), decoration.getDroppedItem());

		itemFrame.remove();

		new SoundBuilder(config.getBreakSound()).location(decoration.getOrigin()).play();

		return true;
	}

	private boolean canEdit(Player player, Location origin) {
		if (Nullables.isNullOrAir(getItem()))
			return true;

		Rank playerRank = Rank.of(player);

		if (player.getUniqueId().equals(getOwner()))
			return true;

		// TODO: Use locks
		if (getOwner() != null) {
			if (new TrustService().get(getOwner()).trusts(Type.DECORATIONS, player))
				return true;
		}

		if (playerRank.isStaff()) {
			if (playerRank.isSeniorStaff() || playerRank.equals(Rank.ARCHITECT) || player.isOp())
				return true;

			if (WorldGroup.STAFF == WorldGroup.of(player))
				return true;

			if (WorldGuardEditCommand.canWorldGuardEdit(player) && new WorldGuardUtils(player).getRegionsAt(origin).size() > 0)
				return true;
		}

		return false;
	}

	public boolean interact(Player player, Block block, InteractType type) {
		final Decoration decoration = new Decoration(config, itemFrame);
		DecorationInteractEvent interactEvent = new DecorationInteractEvent(player, decoration, type);
		if (!interactEvent.callEvent())
			return false;

		if (config.isSeat()) {
			DecorationSitEvent sitEvent = new DecorationSitEvent(player, decoration, bukkitRotation, block);

			if (sitEvent.callEvent())
				sitEvent.getSeat().trySit(player, block, sitEvent.getRotation(), config);
		}

		return true;
	}

	public boolean isOwner(UUID uuid) {
		return uuid.equals(getOwner());
	}

}
